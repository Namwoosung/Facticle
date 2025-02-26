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
KEYWORDS = ["다", "이", "뉴스", "기자", "경제", "a", "e", "등", "수", "한", "있"]


def fetch_news_links():

    headers = {
        "X-Naver-Client-Id": CLIENT_ID,
        "X-Naver-Client-Secret": CLIENT_SECRET,
    }

    unique_links = set()  # 중복 방지를 위한 Set

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
                    five_minutes_ago = lastBuildDate - timedelta(minutes=5)
                except Exception as e:
                    print(f"error [검색어: '{query}'] - lastBuildDate 변환 오류 ({lastBuildDate_str}): {e}")
                    continue

                # 최신 뉴스부터 검색
                for item in items:
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

                    # 유효한 뉴스 URL 추가
                    unique_links.add(url)

            else:
                print(f"error [검색어: '{query}'] - 요청 실패 (응답 코드: {response.status_code})")

        except Exception as e:
            print(f"error [검색어: '{query}'] - 뉴스 API 호출 중 오류 발생: {e}")

    return list(unique_links)  # 중복 제거된 리스트 반환



if __name__ == "__main__":
    links = fetch_news_links()

    print("\n 중복 제거된 뉴스 링크 리스트:")
    for link in links:
        print(link)

    print(f"\n 수집된 전체 link 개수: {len(links)}")