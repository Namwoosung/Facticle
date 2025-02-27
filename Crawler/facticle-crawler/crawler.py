# 획득한 뉴스에 대해 정보를 크롤링하는 모듈
import os
import json
import time
import random
import nltk
import concurrent.futures  # for 멀티스레딩
from newspaper import Article, Config
from naver_api import fetch_news_links
import re
from urllib.parse import urlparse


# 현재 프로젝트 폴더에 있는 nltk_data 경로 추가
nltk_data_path = os.path.join(os.getcwd(), "nltk_data")
nltk.data.path.append(nltk_data_path)

# punkt 데이터가 없으면 자동 다운로드
punkt_path = os.path.join(nltk_data_path, "tokenizers", "punkt")
if not os.path.exists(punkt_path):
    print(f"[warn] punkt 데이터가 없습니다! 다운로드 진행 중...")
    nltk.download('punkt', download_dir=nltk_data_path)

# 랜덤 User-Agent 리스트 (PC, 크롬, 파이어폭스 등) <- 대규모 크롤링을 위해 다양한 user-agent를 랜덤하게 선택해 요청하도록 설계
USER_AGENTS = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:89.0) Gecko/20100101 Firefox/89.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 Edg/131.0.2903.86"
]

# 언론사 매핑 (딕셔너리를 활용해 빠른 조회)
MEDIA_MAPPING_FILE = os.path.join(os.path.dirname(__file__), "media_mapping.json")


# JSON 파일에서 MEDIA_MAPPING을 로드
def load_media_mapping():
    try:
        with open(MEDIA_MAPPING_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    except Exception as e:
        print(f"[ERROR] 미디어 매핑 파일 로드 실패: {e}")
        return {}

# MEDIA_MAPPING 변수 설정
MEDIA_MAPPING = load_media_mapping()

def get_media_name(url):
    try:
        parsed_url = urlparse(url)
        domain = parsed_url.netloc.lower()

        # 미리 정의된 언론사 매핑에 존재하면 반환
        for key, kor_name in MEDIA_MAPPING.items():
            if key in domain:
                return kor_name

        # www. 또는 news. 제거
        domain = re.sub(r"^(www|news)\.", "", domain)

        # 매핑되지 않은 경우 도메인 이름에서 최상위 도메인 제거 후 반환
        domain = re.sub(r"\.(co\.kr|com\.kr|net\.kr|org\.kr|co\.jp|com|net|org|jp)$", "", domain)

        # 매핑되지 않은 경우 url을 파일(url.txt)에 저장
        with open("url.txt", "a", encoding="utf-8") as f:
            f.write(url + "\n")

        return domain

    except Exception as e:
        print(f"오류 발생: {e}")
        return "unknown"


def fetch_article(news):
    """개별 뉴스 기사 크롤링 함수 (for 멀티쓰레딩)"""
    try:
        link = news["url"] #원본 url에서 추출 시도

        # 랜덤 User-Agent 선택
        user_agent = random.choice(USER_AGENTS)

        # Newspaper4k 설정 객체 생성 및 User-Agent 적용
        config = Config()
        config.browser_user_agent = user_agent  # 크롤링할 때 User-Agent 설정

        article = Article(link, language='ko', config=config)
        article.download()
        article.parse()
        article.nlp()

        # 수집할 정보
        news["title"] = article.title or "" # 제목
        news["context"] = article.text or "" # 본문
        news["imageUrl"] = article.top_image or "" # 메인 이미지

        if article.publish_date: # 발행일이 추출된 경우 덮어쓰기
            news["publishedAt"] = article.publish_date.isoformat()

        news["keywords"] = [
            kw for kw in article.keywords
            if len(kw) >= 2 and not re.fullmatch(r"[\W_]+", kw)
        ] if article.keywords else []  # 2글자 이상이면서 특수문자가 아닌 키워드만 추출

        news["mediaName"] = get_media_name(news["url"]) # 원본 링크이름에서 언론사 추출


        # 추가 수집 가능한 정보들
        #news["summary"] = article.summary or "" # 자동 본문 요약
        # news["authors"] = article.authors[0] if article.authors else "" 
        # news["html"] = article.html or "" #html
        # news["movies"] = article.movies[0] if article.movies else ""  # 첫 번째 동영상 링크만 추출
        # news["meta_keywords"] = article.meta_keywords or []  # HTML 메타 키워드 리스트
        # news["meta_description"] = article.meta_description or ""  # HTML 메타 설명
        # news["meta_lang"] = article.meta_lang or ""  # HTML 감지 언어
        # news["meta_favicon"] = article.meta_favicon or ""  # 사이트의 파비콘 URL
        # news["canonical_link"] = article.canonical_link or ""  # 정규화된 link(중복 방지)
        # news["tags"] = list(article.tags) if article.tags else []  # 본문에서 추출된 태그 리스트
        # news["additional_images"] = list(article.images) if article.images else []  # 기사 내 포함된 모든 이미지 리스트

        
        # 수집된 데이터 검증
        # "context" 길이 체크
        # if len(news["context"]) < 30:
        #     news["context"] = ""

        # "title", "context", "publishedAt", "mediaName"가 모두 유효해야 함
        required_fields = ["title", "context", "publishedAt", "mediaName"]
        if all(news[field] for field in required_fields):

            if len(news["context"]) < 80: # 길이 테스트를 위해 추가
                print(f"❌ 필터링된 기사: {news}")
                return None

            print(f"✅ 크롤링 성공: {link}")
            return news
        else:
            print(f"❌ 필터링된 기사: {news}")
            return None

    except Exception as e:
        print(f" [오류] 크롤링 실패 ({link}): {e}")
        return None



def crawl_news_articles(news_list, max_threads=5):
    """멀티쓰레딩을 활용하여 뉴스 기사 크롤링"""
    start_time = time.time()  # 시작 시간 측정
    news_data = []

    with concurrent.futures.ThreadPoolExecutor(max_workers=max_threads) as executor:
        # 크롤링 작업을 개별적으로 실행하고 future 객체 저장
        futures = {executor.submit(fetch_article, news): news for news in news_list}

        # 작업이 완료된 순서대로 결과 처리
        for future in concurrent.futures.as_completed(futures):
            result = future.result()
            if result:
                news_data.append(result)


    # 크롤링된 데이터 개수 출력
    valid_count = len(news_data)
    print(f"\n✅ 크롤링 완료: {valid_count}개의 뉴스 기사 수집 완료")

    elapsed_time = time.time() - start_time  # 걸린 시간 계산
    print(f"⏱ 크롤링에 걸린 실행 시간: {elapsed_time:.2f}초")

    return news_data



if __name__ == "__main__":
    print("네이버 뉴스 링크 가져오는 중...")
    news_list = fetch_news_links()

    print(f"\n {len(news_list)}개의 뉴스 기사 크롤링 시작...")
    crawled_news = crawl_news_articles(news_list, max_threads=5)

    output_file = "crawled_news.json"
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(crawled_news, f, ensure_ascii=False, indent=4)

    print(f"\n 크롤링 완료! 데이터가 '{output_file}' 파일에 저장되었습니다.")
