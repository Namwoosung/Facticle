#DB 연동 및 CRUD를 위한 모듈
from dotenv import load_dotenv #for .env load
import os
import pymysql

load_dotenv()

connection = pymysql.connect(
    host="localhost",         # 호스트 주소
    port=3306,                # 포트 번호
    user="facticle_user",     # 사용자명
    password=os.getenv("DB_PASSWORD"),     # 비밀번호
    database="facticle",      # 데이터베이스 이름
    charset="utf8",           # 문자 인코딩 (UTF-8)
    cursorclass=pymysql.cursors.DictCursor, #반환된 결과를 Dictionary 형태로 사용
)

# 추후 뉴스 저장 로직으로 변경
try:
    with connection.cursor() as cursor:
        # 예시 쿼리: user 정보 출력
        cursor.execute("SELECT * FROM users")
        result = cursor.fetchall()

        for user in result:
            print(user)
finally:
    connection.close()