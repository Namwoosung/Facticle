
from dotenv import load_dotenv
import os
import urllib.parse
import requests
import time


load_dotenv()

NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json"
CLIENT_ID = os.getenv("NAVER_CLIENT_ID")
CLIENT_SECRET = os.getenv("NAVER_CLIENT_SECRET")

# 테스트용 검색어 리스트
TEST_KEYWORDS = ["다", "이", "뉴스", "기자", "경제", "a", "e"]

def fetch_news():
    headers = {
        "X-Naver-Client-Id": os.getenv("NAVER_CLIENT_ID"),
        "X-Naver-Client-Secret": os.getenv("NAVER_CLIENT_SECRET"),
    }

    for query in TEST_KEYWORDS:
        try:
            # 검색어 URL 인코딩
            encoded_query = urllib.parse.quote(query, encoding="utf-8")
            api_url = f"{NAVER_NEWS_API_URL}?query={encoded_query}&display=10&sort=date"
            
            # API 호출
            response = requests.get(api_url, headers=headers)
            print(f"\n🔍 [검색어: '{query}'] - 응답 코드: {response.status_code}\n")
            
            if response.ok:
                print(f"✅ [검색어: '{query}'] - 응답 내용: {response.text}")
            else:
                print(f"❌ [검색어: '{query}'] - 요청 실패: 응답 코드: {response.status_code}")
            
            # API Rate Limit 보호를 위한 1초 대기
            time.sleep(1)
        except Exception as e:
            print(f"🚨 [검색어: '{query}'] - 뉴스 API 호출 중 오류 발생: {e}")

if __name__ == '__main__':
    fetch_news()