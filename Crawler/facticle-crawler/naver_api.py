
from dotenv import load_dotenv
import os
import requests

load_dotenv()

NAVER_API_URL = "https://openapi.naver.com/v1/search/news.json"
CLIENT_ID = os.getenv("NAVER_CLIENT_ID")
CLIENT_SECRET = os.getenv("NAVER_CLIENT_SECRET")

# 테스트용 검색어 리스트
TEST_KEYWORDS = ["다", "이", "뉴스", "기자", "경제", "a", "e"]

