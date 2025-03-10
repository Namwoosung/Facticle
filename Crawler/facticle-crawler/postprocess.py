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

    return max(new_min, min(new_max, round(normalized_score, 2)))


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


def get_reasoning(title, content, hs_score, fs_score, prompt):
    """HS 및 FS 점수에 대한 판단 근거를 제공"""
    cur_prompt = (prompt.replace("{{제목}}", title)
                         .replace("{{본문}}", content)
                         .replace("{{hs}}", str(hs_score))
                         .replace("{{fs}}", str(fs_score)))

    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {"role": "system", "content": "당신은 뉴스 분석 전문가입니다."},
            {"role": "user", "content": cur_prompt}
        ],
        max_tokens=500,
        temperature=0.2,
        top_p=0.9
    )

    return json.loads(response.choices[0].message.content)  


def analyze_news(news_data):
    """
    단일 뉴스 데이터를 분석하여 요약 생성, 카테고리 분류, HS/FS 점수 계산 후 반환.
    """
    title = news_data["title"]
    content = news_data["content"]
    
    # 프롬프트 로드
    prompt_summary = load_prompt("./prompts/prompt_summary.txt")
    prompt_hs = load_prompt("./prompts/prompt_hs.txt")
    prompt_fs = load_prompt("./prompts/prompt_fs.txt")
    prompt_reason = load_prompt("./prompts/prompt_reason.txt")


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

    # 판단 근거 요청
    reasoning_result = get_reasoning(title, content, normalized_hs_score, normalized_fs_score, prompt_reason)
    news_data["hs_reason"] = reasoning_result["hs_reason"]
    news_data["fs_reason"] = reasoning_result["fs_reason"]      

    print(f"\n✅ 분석 완료: {news_data['title']}")

    return news_data


# 뉴스 데이터 분석 실행
if __name__ == "__main__":
    input_files = ["./news.json", "./enter.json", "./sport.json"]  # 3개의 입력 파일
    output_file = "./analyzed_news.json"
    

    news_list = []

    # 3개의 JSON 파일을 로드하여 news_list에 추가
    for file_path in input_files:
        try:
            with open(file_path, "r", encoding="utf-8") as file:
                data = json.load(file)
                news_list.extend(data)  # 리스트에 추가
            print(f"📥 {file_path}에서 {len(data)}개의 뉴스 로드 완료")
        except FileNotFoundError:
            print(f"⚠️ {file_path} 파일을 찾을 수 없습니다. 건너뜁니다.")
        except json.JSONDecodeError:
            print(f"❌ {file_path} 파일의 JSON 형식이 올바르지 않습니다. 건너뜁니다.")
    
    analyzed_news = []

    start_time = time.time()  # 시작 시간 측정

    # 뉴스 데이터 하나씩 분석
    for news in news_list:
        analyzed_news.append(analyze_news(news))

    elapsed_time = time.time() - start_time  # 걸린 시간 계산
    print(f"\n✅ 분석 완료: {len(analyzed_news)}개의 뉴스 기사 수집 완료")
    print(f"⏱ 분석에 걸린 시간: {elapsed_time:.2f}초")

    # 분석 결과 저장
    with open(output_file, "w", encoding="utf-8") as file:
        json.dump(analyzed_news, file, ensure_ascii=False, indent=4)
    
    print(f"분석 완료! 결과가 {output_file}에 저장되었습니다.")

