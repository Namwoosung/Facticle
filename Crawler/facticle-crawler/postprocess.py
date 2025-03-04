# -*- coding: utf-8 -*-
# 후처리 모듈. GPT로 요약 생성, 카테고리 분류, 제목과 본문 유사도 측정 및 분석
#현재는 위의 3가지 작업 모두 GPT를 활용, 추후 각 task마다 적합한 AI model을 활용하도록 리팩토링 가능
import os
import json
import time
from openai import OpenAI
from dotenv import load_dotenv

load_dotenv()
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
client = OpenAI(api_key=OPENAI_API_KEY)

def load_prompt():
    """./prompt.txt 파일에서 프롬프트를 로드"""
    with open("./prompt.txt", "r", encoding="utf-8") as file:
        return file.read()

def analyze_news(news_list):
    print(f"\n {len(news_list)}개의 뉴스 기사 분석 시작...")

    start_time = time.time()  # 시작 시간 측정

    """
    OpenAI API를 사용하여 뉴스 기사 목록을 분석하고 요약, 카테고리 분류, 제목 점수(HS), 팩트 점수(FS)를 반환
    """
    prompt_template = load_prompt()
    analyzed_news = []
    
    for news_data in news_list:
        title = news_data["title"]
        content = news_data["content"]
        
        prompt = prompt_template.replace("{{제목}}", title).replace("{{본문}}", content)
        
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {
                    "role": "system",
                    "content": "당신은 뉴스 분석 전문가입니다."
                },
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            max_tokens=500,
            temperature=0.2,       # 일관성 증가
            top_p=0.9              # 높은 확률의 단어 위주로 선택
        )
        
        print(response.choices[0].message.content)
        
        result = json.loads(response.choices[0].message.content)
        
        # 기존 뉴스 데이터에 분석 결과 추가
        news_data["summary"] = result["summary"]
        news_data["category"] = result["category"]
        news_data["headline_score"] = result["headline_score"]
        news_data["fact_score"] = result["fact_score"]
        
        analyzed_news.append(news_data)
    
    valid_count = len(analyzed_news)
    print(f"\n✅ 분석 완료: {valid_count}개의 뉴스 기사 수집 완료")


    elapsed_time = time.time() - start_time  # 걸린 시간 계산
    print(f"⏱ openAI api에서 뉴스 분석에 걸린 실행 시간: {elapsed_time:.2f}초")

    return analyzed_news


# 뉴스 데이터 분석 실행
if __name__ == "__main__":
    input_file = "./crawled_news.json"
    output_file = "./analyzed_news.json"
    
    # 뉴스 데이터 로드
    with open(input_file, "r", encoding="utf-8") as file:
        news_list = json.load(file)
    
    # 뉴스 데이터 분석
    analyzed_news = analyze_news(news_list)
    
    # 분석 결과 저장
    with open(output_file, "w", encoding="utf-8") as file:
        json.dump(analyzed_news, file, ensure_ascii=False, indent=4)
    
    print(f"분석 완료! 결과가 {output_file}에 저장되었습니다.")


