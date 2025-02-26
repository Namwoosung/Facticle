# 획득한 뉴스에 대해 정보를 크롤링하는 모듈
import os
import json
import time
from newspaper import Article
from naver_api import fetch_news_links

def crawl_news_articles(news_links, delay=1):
    news_data = []

    for link in news_links:
        try:
            print(f"🔍 크롤링 중: {link}")
            article = Article(link, language='ko')  # 한국어 기사 크롤링
            article.download()
            article.parse()
            article.nlp()  # 자연어 처리 실행 (요약, 키워드 등)

            # 수집할 정보
            news_item = {
                "url": link,                        # 기사 URL
                "title": article.title,             # 제목
                "text": article.text,               # 본문
                "authors": article.authors,         # 저자 리스트
                "publish_date": str(article.publish_date),  # 발행일 (없을 수도 있음)
                "top_image": article.top_image,     # 대표 이미지
                "movies": article.movies,           # 포함된 동영상 링크 리스트
                "summary": article.summary,         # 자동 요약 결과
                "keywords": article.keywords        # 자동 키워드 추출 결과
            }

            news_data.append(news_item)
            print(f" 크롤링 성공: {link}")

        except Exception as e:
            print(f" [오류] 크롤링 실패 ({link}): {e}")

        # 요청 간격 조정 (서버 부하 방지)
        time.sleep(delay)

    return news_data

if __name__ == "__main__":
    # 네이버 뉴스 API를 통해 최신 뉴스 링크 가져오기
    print("네이버 뉴스 링크 가져오는 중...")
    news_links = fetch_news_links()

    # 뉴스 데이터 크롤링
    print(f"\n {len(news_links)}개의 뉴스 기사 크롤링 시작...")
    crawled_news = crawl_news_articles(news_links[:2])

    # 결과를 JSON 파일로 저장
    output_file = "crawled_news.json"
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(crawled_news, f, ensure_ascii=False, indent=4)

    print(f"\n 크롤링 완료! 데이터가 '{output_file}' 파일에 저장되었습니다.")