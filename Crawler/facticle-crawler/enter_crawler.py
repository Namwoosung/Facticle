# 네이버 엔터 크롤러
import requests
from bs4 import BeautifulSoup
import datetime, json, random
from concurrent.futures import ThreadPoolExecutor, as_completed

useragent_list = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:89.0) Gecko/20100101 Firefox/89.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 Edg/131.0.2903.86"
]

header = {
  "User-Agent": random.choice(useragent_list),
  "Accept-Encoding": "gzip, deflate, br, zstd",
  "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
}

# 네이버 엔터 기사사 리스트 가져오기
def get_enter_list(page):
  today = datetime.datetime.now().strftime("%Y%m%d")

  url = f'https://api-gw.entertain.naver.com/news/articles?date={today}&page={page}&pageSize=50'
  response = requests.get(url, headers=header)
  json_data = response.json()
  enter_list = json_data['result']['newsList']
  
  result = []
  for enter in enter_list:
    if enter['articleTime'] == '방금전':
      pass
    else:
      time = int(enter['articleTime'].replace('"', '').replace('분전', ''))
      if time >= 8: # 5분 이상 지난 뉴스는 크롤링하지 않음
        break
    result.append({
      'naverUrl': enter['url'],
      "image_url": enter['image'],
      "mediaName": enter['officeName'],
      "news_type": "enter",
    })
  return result

# 네이버 엔터 기사 가져오기
def get_enter(data):
  url = data['naverUrl'].replace('https://m.entertain.naver.com/now/article/','https://api-gw.entertain.naver.com/news/article/')
  try:
    response = requests.get(url, headers=header)
    json_data = response.json()
    article = json_data['result']['articleInfo']['article']
    
    return {
      'url': article['orgUrl']['pc']['url'],
      'naverUrl': data['naverUrl'],
      "title": article['title'],
      "content": article['refinedContent'],
      "image_url": data['image_url'],
      "mediaName": data['mediaName'],
      "news_type": data["news_type"]
    }
  except:
    return None
  
if __name__ == "__main__":
  result = []
  stop_crawling = False  # 크롤링 중단 여부

  for page in range(1, 5):
    if stop_crawling:
      break  # 5분이 지난 뉴스가 나오면 반복 종료
      
    enter_list = get_enter_list(page)
    if not enter_list:  # 만약 뉴스 리스트가 비어 있으면 종료
      break

    with ThreadPoolExecutor(max_workers=5) as executor:
      futures = [executor.submit(get_enter, url) for url in enter_list]
      for future in as_completed(futures):
        data = future.result()
        if data:
          result.append(data)

  with open('enter.json', 'w', encoding='utf-8') as f:
    json.dump(result, f, ensure_ascii=False, indent=2)
    f.write("\n")