# 스케줄링을 위한 모듈
from apscheduler.schedulers.background import BackgroundScheduler
import time
import queue
import concurrent.futures  # for 멀티스레딩
import random
from datetime import datetime
import itertools


news_queue = queue.Queue()
STOP_SIGNAL = "STOP"

# thread 수 지정
max_threads = 5

news_counter = itertools.count(1, step=1)  # 1부터 1씩 증가


def generate_mock_news_list(n=10):
    """이전 호출에서 마지막으로 생성된 값 +10부터 시작하여 n개의 뉴스 데이터를 생성"""
    global news_counter  # 전역 변수 사용
    start = next(news_counter)  # 현재 시작 번호 가져오기
    news_counter = itertools.count(start + n, step=1)  # 다음 호출 시 증가된 값 유지

    return [
        {"title": f"테스트 뉴스 {i}", "content": f"테스트 본문 {i}"}
        for i in range(start, start + n)
    ]

# 더미 함수 (뉴스 리스트 수집)
def crawl_news_list():
    """가짜 뉴스 리스트 반환 (실제 크롤링 대신)"""
    print("📰 가짜 뉴스 리스트 수집 완료!")
    return generate_mock_news_list(10)

# 더미 함수 (뉴스 상세 크롤링)
def crawl_news_articles(news):
    """뉴스 크롤링 모방"""
    time.sleep(random.uniform(1, 5))  # 1~5초 랜덤 대기
    print(f"📄 뉴스 크롤링 완료: {news['title']}")
    return news  # 원래 데이터 그대로 반환

# 더미 함수 (뉴스 분석)
def analyze_news(news):
    """뉴스 분석 모방"""
    time.sleep(random.uniform(1, 10))  # 1~10초 랜덤 대기
    news["summary"] = "요약된 뉴스 내용"
    news["category"] = "테스트 카테고리"
    news["headline_score"] = random.randint(50, 100)  # 50~100 랜덤 점수
    news["fact_score"] = random.randint(50, 100)
    print(f"🔍 뉴스 분석 완료: {news['title']}")
    return news

# 더미 함수 (뉴스 저장)
def save_news(news):
    """DB 저장 모방"""
    time.sleep(random.uniform(0.5, 1))  # 0.5~1초 랜덤 대기
    print(f"💾 뉴스 저장 완료: {news['title']}")

def fetch_news():
    """스케줄러가 실행하는 뉴스 수집 함수 (30초 마다 실행)"""
    print("🔄 뉴스 데이터 수집 시작...")
    news_list = crawl_news_list()  # 가짜 뉴스 리스트 수집

    for news in news_list:
        news_queue.put(news)  # Queue에 뉴스 추가
    print(f"✅ 수집된 뉴스 {len(news_list)}개 큐에 추가")

def process_news():
    """Queue에서 뉴스 데이터를 가져와 하나씩 처리하는 Worker 스레드"""

    print("[info] thread 실행...")

    while True:
        news = news_queue.get()  # Queue에서 뉴스 가져오기 (Blocking)

        if news is STOP_SIGNAL:  # 종료 신호 확인
            print("🛑 Worker 종료 신호 수신. 스레드 종료.")
            news_queue.task_done()
            break

        try:
            print(f"📰 뉴스 처리 시작: {news['title']}")
            news_data = crawl_news_articles(news)  # 기사 크롤링
            analyzed_data = analyze_news(news_data)  # 뉴스 분석
            save_news(analyzed_data)  # 뉴스 저장
            print(f"✅ 뉴스 처리 완료: {news['title']}")
        except Exception as e:
            print(f"❌ 뉴스 처리 실패: {news['title']} - {e}")
        finally:
            news_queue.task_done()  # 큐 작업 완료 처리

# 스케줄러 설정 (뉴스 수집 주기: 10초)
scheduler = BackgroundScheduler()
scheduler.add_job(fetch_news, 'interval', seconds=10, next_run_time=datetime.now()) # 처음 시작 시 바로 함수를 한 번 실행

if __name__ == "__main__":
    print("✅ 뉴스 크롤러 시작!")

    # 스케줄러 시작
    scheduler.start()

    # 뉴스 처리 워커 스레드 실행, 각자 queue에서 데이터를 가져와 처리
    executor = concurrent.futures.ThreadPoolExecutor(max_threads)
    for _ in range(max_threads):
        executor.submit(process_news)

    print("스케줄러 동작 및 쓰레드 실행....")

    # 메인 스레드 유지
    try:
        while True:
            time.sleep(10)  # 프로그램 실행 지속
    except (KeyboardInterrupt, SystemExit):
        print("🛑 종료 신호 감지, 뉴스 처리 중지")
        scheduler.shutdown()

        # Worker들에게 종료 신호 전달
        for _ in range(max_threads):
            news_queue.put(STOP_SIGNAL)  # Worker가 STOP_SIGNAL을 받으면 종료됨

        # 모든 큐 작업이 끝날 때까지 대기
        print("📌 큐의 남은 작업 대기 중...")
        news_queue.join()

        # 모든 워커 스레드 종료 대기
        print("📌 워커 스레드 종료 대기 중...")
        executor.shutdown(wait=True)

        print("✅ 모든 작업 종료. 프로그램 종료.")
        exit(0)  # 프로세스 종료
