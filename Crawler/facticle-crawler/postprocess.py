# -*- coding: utf-8 -*-
# 후처리 모듈. GPT로 요약 생성, 카테고리 분류, 제목과 본문 유사도 측정 및 분석
#현재는 위의 3가지 작업 모두 GPT를 활용, 추후 각 task마다 적합한 AI model을 활용하도록 리팩토링 가능
import os
import json
import time
import math
from openai import OpenAI
from dotenv import load_dotenv

load_dotenv()
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
client = OpenAI(api_key=OPENAI_API_KEY)

def load_prompt(file_path):
    """지정된 파일에서 프롬프트를 로드"""
    with open(file_path, "r", encoding="utf-8") as file:
        return file.read()
    
def logprob_to_prob(logprob):
    """로그 확률을 실제 확률로 변환"""
    return math.exp(logprob)

def normalize_score(score, old_range=(1, 5), new_range=(0, 100)):
    """1~5 점수를 0~100 점으로 정규화"""
    old_min, old_max = old_range
    new_min, new_max = new_range
    normalized_score = ((score - old_min) / (old_max - old_min)) * (new_max - new_min) + new_min
    return round(normalized_score, 2)

def calculate_score(logprobs, token_range=(1, 5)):
    """로그 확률을 받아 실제 확률로 변환하고, 종합 점수를 계산"""
    score = 0
    token_probs = {str(i): 0 for i in range(token_range[0], token_range[1] + 1)}

    # logprobs에서 상위 토큰들의 확률 계산
    for logprob_item in logprobs:
        for top_logprob in logprob_item.top_logprobs:
            token_str = top_logprob.token
            if token_str in token_probs:
                prob = logprob_to_prob(top_logprob.logprob)
                if prob > token_probs[token_str]:
                    token_probs[token_str] = prob

    score = sum(int(token) * prob for token, prob in token_probs.items())
    
    return score, token_probs

def evaluate_score(title, content, prompt):
    """GEval 방식을 사용하여 점수 계산"""
    # 하나의 프롬프트에 대해 처리
    cur_prompt = prompt.replace("{{제목}}", title).replace("{{본문}}", content)

    # GPT API 호출
    completion = client.chat.completions.create(
        model="gpt-4o-mini",
        max_tokens=5,
        messages=[
            {
                "role": "system",
                "content": "당신은 뉴스 분석 전문가입니다."
            },
            {
                "role": "user",
                "content": cur_prompt
            }
        ],
        temperature=0,
        logprobs=True,
        top_logprobs=10,
    )

    logprobs_content = completion.choices[0].logprobs.content  # 각 토큰별 로그 확률 데이터
    score, token_probs = calculate_score(logprobs_content)

    return score, token_probs



def analyze_news(news_list):
    print(f"\n {len(news_list)}개의 뉴스 기사 분석 시작...")

    start_time = time.time()  # 시작 시간 측정

    analyzed_news = []
    
    # prompt_summary.txt, prompt_hs.txt, prompt_fs.txt 로드
    prompt_summary = load_prompt("./prompt_summary.txt")
    prompt_hs = load_prompt("./prompt_hs.txt")
    prompt_fs = load_prompt("./prompt_fs.txt")

    for news_data in news_list:
        title = news_data["title"]
        content = news_data["content"]

        # 요약 생성 및 카테고리 분류
        prompt = prompt_summary.replace("{{제목}}", title).replace("{{본문}}", content)
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
            temperature=0.2,
            top_p=0.9
        )
        
        result = json.loads(response.choices[0].message.content)
        
        # 요약과 카테고리 결과 추출
        news_data["summary"] = result["summary"]
        news_data["category"] = result["category"]
        
        # HS 점수 계산
        hs_score, hs_token_probs = evaluate_score(title, content, prompt_hs)
        normalized_hs_score = normalize_score(hs_score)

        news_data["headline_score"] = normalized_hs_score
        news_data["headline_score_origin"] = hs_score
        news_data["headline_score_probs"] = hs_token_probs
        
        # FS 점수 계산
        fs_score, fs_token_probs = evaluate_score(title, content, prompt_fs)
        normalized_fs_score = normalize_score(fs_score)

        news_data["fact_score"] = normalized_fs_score
        news_data["fact_score_origin"] = fs_score
        news_data["fact_score_probs"] = fs_token_probs

        
        analyzed_news.append(news_data)
        print(f"\n✅ 분석 완료: {news_data}")
    
    print(f"\n✅ 분석 완료: {len(analyzed_news)}개의 뉴스 기사 수집 완료")
    elapsed_time = time.time() - start_time  # 걸린 시간 계산
    print(f"⏱ 분석에 걸린 시간: {elapsed_time:.2f}초")

    return analyzed_news


# 뉴스 데이터 분석 실행
if __name__ == "__main__":
    input_file = "./crawled_news_forTest.json"
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


