#DB 연동 및 CRUD를 위한 모듈, 로컬에서 돌아가는 거 확인하면 docker로 바꿔야 함(실제 통합 개발 테스트는 docker에서 할 거니까 docker에 두어야 함)
from dotenv import load_dotenv #for .env load
import os
import json
import pymysql
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker, scoped_session
from elasticsearch import Elasticsearch, helpers

load_dotenv()
# 환경 변수 설정, 정보 없다면 local DB로 연결
DB_USER = os.getenv("DB_USER")
DB_PASSWORD = os.getenv("DB_PASSWORD")
DB_HOST = os.getenv("DB_HOST")
DB_PORT = os.getenv("DB_PORT")
DB_NAME = os.getenv("DB_NAME")

DATABASE_URL = (
    f"mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"
    "?ssl_verify_cert=false"
)

# scoped_session을 활용하여 각 스레드에서 독립적인 세션을 제공
engine = create_engine(
    DATABASE_URL,
    echo=False, # 쿼리 출력 x
    pool_size=5, # 현재는 thread를 5개 구성할 예정이므로 5개의 connection pool 유지
    pool_timeout=30, #연결 시간 초과 제한
    pool_pre_ping=True # DB 연결이 끊어졌는 지 주기적으로 확인
)
SessionLocal = scoped_session(sessionmaker(bind=engine, autocommit=False, autoflush=False))

# Elasticsearch 설정
ES_HOST = os.getenv("ES_HOST")
ES_USER = os.getenv("ES_USER")
ES_PASSWORD = os.getenv("ES_PASSWORD")

es = Elasticsearch(
    [ES_HOST],
    basic_auth=(ES_USER, ES_PASSWORD)  # 기본 인증 추가
)

ES_INDEX = "news_index"


def check_db_connection():
    """DB 연결이 정상적으로 되는지 확인"""
    print("[info] 데이터베이스 연결 확인 중...")

    try:
        with engine.connect() as connection:
            connection.execute(text("SELECT 1"))  # 간단한 쿼리 실행하여 연결 확인
            print("[info] DB 연결 성공!")
    except Exception as e:
        print(f"[error] DB 연결 실패: {e}")
        exit(1)  # 연결 실패 시 프로그램 종료


def check_elasticsearch_connection():
    """Elasticsearch 연결 확인"""
    print("[info] Elasticsearch 연결 확인 중...")

    try:
        if es.ping():
            print("[info] Elasticsearch 연결 성공!")
        else:
            print("[error] Elasticsearch 연결 실패!")
            exit(1)
    except Exception as e:
        print(f"[error] Elasticsearch 연결 오류: {e}")
        exit(1)


# 뉴스 데이터를 DB에 삽입하는 함수, sqlalchemy은 기본적으로 ORM 라이브러리이지만, 해당 모듈에서는 단순히 news 데이터를 insert하는 동작만 담당하기에, Raw SQL로 사용
def save_news(news_data):
    """
    뉴스 데이터를 news 및 news_content 테이블에 저장
    """
    db = SessionLocal()

    try:
        # 기존 뉴스 ID 조회 (이미 존재하면 저장 안 함)
        news_id_query = text("""
            SELECT news_id FROM news WHERE url = :url
        """)
        result = db.execute(news_id_query, {"url": news_data["url"]}).fetchone()

        if result:
            print(f"[INFO] 이미 존재하는 뉴스: {news_data['url']} - 저장하지 않음.")
            return  # 이미 존재하면 아무 동작도 하지 않음

        # news 테이블 삽입
        news_sql = text("""
            INSERT INTO news (
                url, naver_url, title, summary, image_url, media_name, 
                category, headline_score, fact_score, headline_score_reason, fact_score_reason
            ) VALUES (:url, :naver_url, :title, :summary, :image_url, :media_name, 
                      :category, :headline_score, :fact_score, :headline_score_reason, :fact_score_reason)
        """)

        # news 테이블 데이터
        news_values = {
            "url": news_data["url"],
            "naver_url": news_data["naverUrl"],
            "title": news_data["title"],
            "summary": news_data["summary"],
            "image_url": news_data["image_url"],
            "media_name": news_data["mediaName"],
            "category": news_data["category"],
            "headline_score": news_data["headline_score"],
            "fact_score": news_data["fact_score"],
            "headline_score_reason": news_data["hs_reason"],
            "fact_score_reason": news_data["fs_reason"]
        }

        # SQL 실행
        result = db.execute(news_sql, news_values)
        news_id = result.lastrowid  # 삽입된 news_id 가져오기

        # news_content 테이블 삽입
        news_content_sql = text("""
            INSERT INTO news_content (news_id, content) 
            VALUES (:news_id, :content)
        """)

        news_content_values = {
            "news_id": news_id,
            "content": news_data["content"]
        }

        # SQL 실행
        db.execute(news_content_sql, news_content_values)
        db.commit()

        print(f"[info] 뉴스 저장 완료 (news_id={news_id})")

        # Elasticsearch에 저장
        es.index(index=ES_INDEX, id=news_id, document={
            "title": news_data["title"],
            "content": news_data["content"]
        })

        print(f"[info] Elasticsearch에 저장 완료 (news_id={news_id})")
    except Exception as e:
        db.rollback()
        print(f"[error] 데이터 삽입 오류: {e}")
    finally:
        db.close()
        SessionLocal.remove()

# mysql의 data들을 엘라스틱 서치로 동기화
def sync_mysql_to_elasticsearch():
    """
    MySQL에 저장된 뉴스 데이터를 Elasticsearch와 동기화
    """
    db = SessionLocal()

    try:
        # MySQL에서 뉴스 데이터 조회
        query = text("""
            SELECT n.news_id, n.title, nc.content
            FROM news n
            JOIN news_content nc ON n.news_id = nc.news_id
        """)
        result = db.execute(query)
        news_data = [dict(row) for row in result.mappings().all()]

        if not news_data:
            print("[info] 동기화할 데이터가 없습니다.")
            return
        
        # Elasticsearch에 배치 삽입 (Bulk API 사용)
        def generate_bulk_data():
            for row in news_data:
                yield {
                    "_index": ES_INDEX,
                    "_id": row["news_id"],
                    "_source": {
                        "title": row["title"],
                        "content": row["content"]
                    }
                }

        helpers.bulk(es, generate_bulk_data())

        print(f"[info] MySQL → Elasticsearch 동기화 완료! ({len(news_data)}건)")
    except Exception as e:
        print(f"[error] Elasticsearch 동기화 오류: {e}")
    finally:
        db.close()
        SessionLocal.remove()





# 테스트
if __name__ == "__main__":
    # 1. DB & Elasticsearch 연결 확인
    check_db_connection()
    check_elasticsearch_connection()

    input_file = "./analyzed_news.json"

    try:
        with open(input_file, "r", encoding="utf-8") as file:
            news_list = json.load(file)

            if not isinstance(news_list, list) or len(news_list) == 0:
                print("[error] 분석된 뉴스 데이터가 없습니다.")
                exit(1)  # 프로그램 종료

            news = news_list[0]  # 테스트를 위해 첫 번째 뉴스만 삽입
    except FileNotFoundError:
        print(f"[error] {input_file} 파일을 찾을 수 없습니다. 건너뜁니다.")
        exit(1)
    except json.JSONDecodeError:
        print(f"[error] {input_file} 파일의 JSON 형식이 올바르지 않습니다. 건너뜁니다.")
        exit(1)

    save_news(news)  # 뉴스 저장 실행

    # 3. MySQL → Elasticsearch 전체 동기화 실행
    # sync_mysql_to_elasticsearch()
