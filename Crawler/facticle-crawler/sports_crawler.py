# 네이버 스포츠 크롤러
import requests
from bs4 import BeautifulSoup
import datetime, json, random, time, re
from datetime import datetime, timedelta, timezone
from concurrent.futures import ThreadPoolExecutor, as_completed

#https://api-gw.sports.naver.com/news/scs/series?contentSort=contentId%3ADESC&size=18&page=2&contentSize=3&hasTotalCount=true&publishingType=SPORTS&serviceExposure=SE999

useragent_list = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:89.0) Gecko/20100101 Firefox/89.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 Edg/131.0.2903.86"
]

header = {
  "User-Agent": random.choice(useragent_list),
  'Accept': 'application/json, text/plain, */*',
  "Accept-Encoding": "gzip, deflate, br, zstd",
  "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
  'Referer': 'https://m.sports.naver.com/column/press/columnist?categoryId=ALL',
}

# 네이버 스포츠 리스트 가져오기
def get_sports_list(page):
    timestamp = int(time.time())
    today = datetime.now().strftime("%Y%m%d")
    
    url = f'https://api-gw.sports.naver.com/news/scs/series?page={page}&sort=lastModifiedContentDate%3ADESC&contentSort=contentId%3ADESC&contentSize=3&hasTotalCount=true&publishingType=SPORTS&serviceExposure=SE001&size=18&nocache={timestamp}'
    response = requests.get(url, headers=header)
    json_data = response.json()
    sports_list = json_data['result']['contents']

    result = []
    for sports in sports_list:
        for item in sports['packItemContents']:
            # 2분 이상이면 크롤링하지 않음
            _time = datetime.now(timezone.utc) - datetime.fromisoformat(item['createdDate'].rstrip('Z')).replace(tzinfo=timezone.utc)
            if _time >= timedelta(minutes=30):
                continue
            result.append({
                'url': item['orgUrl']['pc'],
                'naverUrl': item['linkUrl'],
                'image_url': item['imageUrl'],
                'news_type': "sport",
            })
    return result

# 네이버 스포츠 기사 가져오기
def get_sports(data):
    url = re.sub(r'https://m.sports.naver.com/[^/]+/article/', 'https://api-gw.sports.naver.com/news/article/', data['naverUrl']).replace('?type=series&cid=', '?cid=')
    try:
        response = requests.get(url, headers=header)
        json_data = response.json()
        article = json_data['result']['articleInfo']['article']

        return {
            'url': data['url'],
            'naverUrl': data['naverUrl'],
            'title': article['title'],
            'content': article['refinedContent'],
            'image_url': data['image_url'],
            'mediaName': json_data['result']['officeInfo']['hname'],
            "news_type": data["news_type"]
        }
    except:
        return None
    

if __name__ == "__main__":
    result = []
    stop_crawling = False  # 크롤링 중단 여부

    for page in range(0, 4):
      if stop_crawling:
          break
      sports_list = get_sports_list(page)
      if not sports_list:
          break
      with ThreadPoolExecutor(max_workers=1) as executor:
        futures = [executor.submit(get_sports, sports) for sports in sports_list]
        for future in as_completed(futures):
          data = future.result()
          if data:
              result.append(data)

    with open('sport.json', 'w', encoding='utf-8') as f:
      json.dump(result, f, ensure_ascii=False, indent=2)
      f.write("\n")
