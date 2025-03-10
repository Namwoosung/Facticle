#DB 연동 및 CRUD를 위한 모듈
from dotenv import load_dotenv #for .env load
import os
import json
import pymysql

load_dotenv()

def connect_db():
    return pymysql.connect(
        host="localhost",         # 호스트 주소
        port=3306,                # 포트 번호
        user="facticle_user",     # 사용자명
        password=os.getenv("DB_PASSWORD"),     # 비밀번호
        database="facticle",      # 데이터베이스 이름
        charset="utf8",           # 문자 인코딩 (UTF-8)
        cursorclass=pymysql.cursors.DictCursor, #반환된 결과를 Dictionary 형태로 사용
        autocommit=False
    )

# 뉴스 데이터를 DB에 삽입하는 함수
def save_news(news_data):
    """
    뉴스 데이터를 news 및 news_content 테이블에 저장
    :param news_data: dict 형태의 뉴스 데이터
    """
    connection = connect_db()
    
    try:
        with connection.cursor() as cursor:
            # news 테이블 삽입 SQL
            news_sql = """
            INSERT INTO news (
                url, naver_url, title, summary, image_url, media_name, 
                category, headline_score, fact_score, headline_score_reason, fact_score_reason
            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """

            news_values = (
                news_data["url"], news_data["naverUrl"], news_data["title"], news_data["summary"],
                news_data["image_url"], news_data["mediaName"], news_data["category"],
                news_data["headline_score"], news_data["fact_score"],
                news_data["hs_reason"], news_data["fs_reason"]
            )

            cursor.execute(news_sql, news_values)
            news_id = cursor.lastrowid  # 삽입된 news_id 가져오기
            
            # news_content 테이블 삽입 SQL
            news_content_sql = """
            INSERT INTO news_content (news_id, content) 
            VALUES (%s, %s)
            """

            news_content_values = (news_id, news_data["content"])
            cursor.execute(news_content_sql, news_content_values)

        # 변경 사항 적용, pymysql을 사용하면 MySQL의 autocommit 설정을 false로 해놓고 시작
        connection.commit()
        print(f"[info] 뉴스 저장 완료 (news_id={news_id})")

    except pymysql.IntegrityError as e:
        print(f"[warn] 중복된 뉴스 데이터 삽입 시도: {news_data['url']}")
        connection.rollback()
    except pymysql.MySQLError as e:
        connection.rollback()
        print(f"[error] 데이터 삽입 오류: {e}")
    finally:
        connection.close()


if __name__ == "__main__":
    input_file = "./analyzed_news.json"

    try:
        with open(input_file, "r", encoding="utf-8") as file:
            news_list = json.load(file)

            if not isinstance(news_list, list) or len(news_list) == 0:
                print("[error] 분석된 뉴스 데이터가 없습니다.")
                exit(1)  # 프로그램 종료

            news = news_list[0]
    except FileNotFoundError:
        print(f"[error] {input_file} 파일을 찾을 수 없습니다. 건너뜁니다.")
        exit(1)
    except json.JSONDecodeError:
        print(f"[error] {input_file} 파일의 JSON 형식이 올바르지 않습니다. 건너뜁니다.")
        exit(1)

    save_news(news)
