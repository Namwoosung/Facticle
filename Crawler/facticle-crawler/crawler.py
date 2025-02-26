# 획득한 뉴스에 대해 정보를 크롤링하는 모듈
import os
import json
import time
import random
import nltk
import concurrent.futures  # for 멀티스레딩
from newspaper import Article, Config
from naver_api import fetch_news_links

# 현재 프로젝트 폴더에 있는 nltk_data 경로 추가
nltk_data_path = os.path.join(os.getcwd(), "nltk_data")
nltk.data.path.append(nltk_data_path)

# punkt 데이터가 없으면 자동 다운로드
punkt_path = os.path.join(nltk_data_path, "tokenizers", "punkt")
if not os.path.exists(punkt_path):
    print(f"[warn] punkt 데이터가 없습니다! 다운로드 진행 중...")
    nltk.download('punkt', download_dir=nltk_data_path)

# 랜덤 User-Agent 리스트 (PC, 크롬, 파이어폭스 등 포함) <- 대규모 크롤링을 위해 다양한 user-agent를 랜덤하게 선택해 요청하도록 설계
USER_AGENTS = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:89.0) Gecko/20100101 Firefox/89.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 Edg/131.0.2903.86"
]

def fetch_article(link):
    """개별 뉴스 기사 크롤링 함수 (for 멀티쓰레딩)"""
    try:
        print(f" 크롤링 중: {link}")

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
        news_item = {
            "url": link,                        
            "title": article.title or "",        
            "text": article.text or "",          
            "text_raw_html": article.html or "", 
            "authors": article.authors or [],    
            "publish_date": str(article.publish_date) if article.publish_date else "",  
            "top_image": article.top_image or "",    
            "movies": article.movies or [],     
            "summary": article.summary or "",   
            "keywords": article.keywords or [], 
            "meta_keywords": article.meta_keywords or [],  
            "meta_description": article.meta_description or "",  
            "meta_lang": article.meta_lang or "",     
            "meta_favicon": article.meta_favicon or "",  
            "canonical_link": article.canonical_link or "",  
            "tags": list(article.tags) if article.tags else [],  
            "additional_images": list(article.images) if article.images else []
        }

        print(f" 크롤링 성공: {link}")
        return news_item

    except Exception as e:
        print(f" [오류] 크롤링 실패 ({link}): {e}")
        return None

# 스레드에 분할해서 작업을 제공하기 위해 list의 목록을 스레드의 개수만큼 분할
def split_list(lst, n): 
    """리스트를 n개로 균등하게 분할"""
    k, m = divmod(len(lst), n)
    return [lst[i * k + min(i, m):(i + 1) * k + min(i + 1, m)] for i in range(n)]


def crawl_news_articles(news_links, max_threads=5):
    """멀티쓰레딩을 활용하여 뉴스 기사 크롤링"""
    news_data = []

    # 뉴스 기사 리스트를 스레드 개수만큼 균등하게 분할
    chunks = split_list(news_links, max_threads)

    def process_chunk(chunk):
        """각 스레드가 처리할 뉴스 기사 목록"""
        local_data = []
        for link in chunk:
            news_item = fetch_article(link)
            if news_item:
                local_data.append(news_item)
            time.sleep(random.uniform(0.5, 1.5))  # 랜덤 대기
        return local_data

    # ThreadPoolExecutor 활용
    with concurrent.futures.ThreadPoolExecutor(max_workers=max_threads) as executor:
        results = executor.map(process_chunk, chunks) # process_chunk 작업을 수행가능한 스레드에게 순차적으로 지급하면서 동작하게됨

    # 모든 결과를 합침
    for result in results:
        news_data.extend(result)

    return news_data



if __name__ == "__main__":
    print("네이버 뉴스 링크 가져오는 중...")
    news_links = fetch_news_links()

    print(f"\n {len(news_links)}개의 뉴스 기사 크롤링 시작...")
    crawled_news = crawl_news_articles(news_links[:10], max_threads=5)

    output_file = "crawled_news.json"
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(crawled_news, f, ensure_ascii=False, indent=4)

    print(f"\n 크롤링 완료! 데이터가 '{output_file}' 파일에 저장되었습니다.")