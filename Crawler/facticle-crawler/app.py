# 엔트리포인트, 전체 모듈을 순차적으로 실행하는 역할

"""
import time
import concurrent.futures
from apscheduler.schedulers.blocking import BlockingScheduler
from naver_news import crawl_news_list
from crawler import crawl_news_articles
from postprocess import analyze_news
from db import save_news

# 멀티스레딩을 위한 최대 스레드 개수 설정
MAX_THREADS = 5

def process_news(news):
    """하나의 뉴스 데이터를 크롤링하고, 처리하고, 저장하는 함수"""
    try:
        # 1. 뉴스 기사 본문 크롤링
        full_news = crawl_news_articles(news)

        # 2. 뉴스 데이터 분석 (GPT-4o-mini 활용)
        processed_news = analyze_news(full_news)

        # 3. 뉴스 데이터를 DB에 저장
        save_news(processed_news)

        print(f"[INFO] 뉴스 처리 완료: {news['title']}")
        return processed_news
    except Exception as e:
        print(f"[ERROR] 뉴스 처리 중 오류 발생: {e}")
        return None

def fetch_and_process_news():
    """뉴스 크롤링 및 처리 전체 과정 (5분마다 실행)"""
    print("[INFO] 크롤링할 뉴스 목록을 가져옵니다...")
    news_list = crawl_news_list()  # 크롤링할 뉴스 목록 가져오기

    if not news_list:
        print("[INFO] 새로운 뉴스가 없습니다. 5분 후 다시 시도합니다.")
        return

    print(f"[INFO] {len(news_list)}개의 뉴스를 병렬로 처리합니다...")

    # 멀티스레딩 실행
    news_data = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=MAX_THREADS) as executor:
        # 크롤링 작업을 개별적으로 실행하고 future 객체 저장
        futures = {executor.submit(process_news, news): news for news in news_list}

        # 작업이 완료된 순서대로 결과 처리
        for future in concurrent.futures.as_completed(futures):
            result = future.result()
            if result:
                news_data.append(result)

    print("[INFO] 모든 뉴스 처리가 완료되었습니다.\n")

# APScheduler 설정
scheduler = BlockingScheduler()
scheduler.add_job(fetch_and_process_news, 'interval', minutes=5)  # 5분마다 실행

if __name__ == "__main__":
    print("[INFO] 뉴스 수집 스케줄러 시작")
    fetch_and_process_news()  # 최초 실행
    scheduler.start()  # 5분마다 반복 실행

"""