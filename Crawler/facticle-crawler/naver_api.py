# 네이버 검색 api 호출을 위한 모듈
import os
import urllib.parse
import requests
import time
import nltk
from dotenv import load_dotenv
from datetime import datetime, timezone, timedelta

# .env 파일 로드
load_dotenv()

# 네이버 검색 API 설정
NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json"
CLIENT_ID = os.getenv("NAVER_CLIENT_ID")
CLIENT_SECRET = os.getenv("NAVER_CLIENT_SECRET")

#검색어 목록
KEYWORDS = ["다", "이", "뉴스", "기자", "경제", "a", "e", "등", "수", "한"]


def fetch_news_links():
    start_time = time.time()  # 시작 시간 측정

    headers = {
        "X-Naver-Client-Id": CLIENT_ID,
        "X-Naver-Client-Secret": CLIENT_SECRET,
    }

    unique_news = {} #중복 방지를 위해 dictionary 형태로 관리

    for query in KEYWORDS:
        try:
            # 검색어 URL 인코딩
            encoded_query = urllib.parse.quote(query, encoding="utf-8")
            api_url = f"{NAVER_NEWS_API_URL}?query={encoded_query}&display=100&sort=date"
            
            # API 호출
            response = requests.get(api_url, headers=headers)
            print(f"\n [검색어: '{query}'] - 응답 코드: {response.status_code}")

            if response.ok:
                json_data = response.json()
                items = json_data.get("items", [])

                # lastBuildDate를 기준으로 5분 이내의 데이터만 수집
                lastBuildDate_str = json_data.get("lastBuildDate")
                try:
                    lastBuildDate = datetime.strptime(lastBuildDate_str, "%a, %d %b %Y %H:%M:%S %z")
                    five_minutes_ago = lastBuildDate - timedelta(minutes=4, seconds=59)
                except Exception as e:
                    print(f"error [검색어: '{query}'] - lastBuildDate 변환 오류 ({lastBuildDate_str}): {e}")
                    continue

                # 최신 뉴스부터 검색
                for item in items:
                    #데이터가 없는 경우를 대비해 없는 경우 각각 다른 데이터라도 담도록 설계
                    naver_url = item["link"] if item["link"] else item["originallink"]
                    url = item["originallink"] if item["originallink"] else item["link"]
                    pubDate_str = item.get("pubDate")

                    # pubDate를 datetime 객체로 변환
                    try:
                        pubDate = datetime.strptime(pubDate_str, "%a, %d %b %Y %H:%M:%S %z")
                    except Exception as e:
                        print(f"error [검색어: '{query}'] - 시간 변환 오류 ({pubDate_str}): {e}")
                        continue

                    # lastBuildDate를 기준으로 5분 이상 지난 뉴스는 수집 중단
                    if pubDate < five_minutes_ago:
                        print(f" [검색어: '{query}'] - 5분 이상 지난 데이터 발견. 다음 검색어로 이동.")
                        break

                    # 중복 검사 후 저장
                    if url not in unique_news:
                        unique_news[url] = {
                            "url": url,
                            "naverUrl": naver_url,
                            "publishedAt": pubDate.isoformat()
                        }

            else:
                print(f"error [검색어: '{query}'] - 요청 실패 (응답 코드: {response.status_code})")

        except Exception as e:
            print(f"error [검색어: '{query}'] - 뉴스 API 호출 중 오류 발생: {e}")

    print(f"\n총 {len(unique_news)}개의 뉴스가 수집되었습니다.")
    elapsed_time = time.time() - start_time  # 걸린 시간 계산
    print(f"⏱ 네이버 검색 api에서 걸린 실행 시간: {elapsed_time:.2f}초")

    return list(unique_news.values())  # 중복 제거된 뉴스 정보 반환



if __name__ == "__main__":
    news_data = fetch_news_links()
