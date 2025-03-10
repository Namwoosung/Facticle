# 네이버 뉴스 크롤러
import requests
from bs4 import BeautifulSoup
import datetime, json
from concurrent.futures import ThreadPoolExecutor, as_completed

# 네이버 뉴스 리스트 가져오기
def get_news_list(page):
    today = datetime.datetime.now().strftime("%Y%m%d")

    url = f"https://news.naver.com/main/list.naver?mode=LSD&mid=sec&sid1=001&date={today}&page={page}"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, "html.parser")
    news_list = soup.find("div", class_="list_body newsflash_body").find_all("li")

    result = []
    for news in news_list:
        naverUrl = news.find("a")["href"]
        time = int(news.find("span", class_="date is_new").get_text().replace("\"", "").replace("분전", ""))
        if time >= 2: # 2분 이상 지난 뉴스는 크롤링하지 않음
            break

        result.append({
            "naverUrl": naverUrl,
            "news_type": "news"
        })

    return result

# 네이버 뉴스 가져오기
def get_news(data):
    try:
        url = data["naverUrl"]
        response = requests.get(url)
        soup = BeautifulSoup(response.text, "html.parser")
        title = soup.find("h2", id="title_area").get_text()
        content = soup.find("article", id="dic_area").get_text().strip()
        image_url = soup.find("img", class_="_LAZY_LOADING _LAZY_LOADING_INIT_HIDE")["data-src"]
        mediaName = soup.find("img", class_="media_end_head_top_logo_img")["title"]
        real_url = soup.find('a', string='기사원문')['href']

        return {
        'url': real_url,
        'naverUrl': url,
        "title": title,
        "content": content,
        "image_url": image_url,
        "mediaName": mediaName,
        "news_type": data["news_type"]
        }
    except:
        return None
    

    

if __name__ == "__main__":
    result = []
    stop_crawling = False  # 크롤링 중단 여부

    for page in range(1, 10):
        if stop_crawling:
            break  # 2분이 지난 뉴스가 나오면 반복 종료
        
        news_list = get_news_list(page)
        if not news_list:  # 만약 뉴스 리스트가 비어 있으면 종료
            break

        with ThreadPoolExecutor(max_workers=5) as executor:
            futures = [executor.submit(get_news, news) for news in news_list]
            for future in as_completed(futures):
                news = future.result()
                if news: # 뉴스 결과 값이 있으면 추가
                    result.append(news)

    # json 파일로 저장
    with open("news.json", "w", encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, indent=2)
        f.write("\n")
    

