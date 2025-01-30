# Step 1: JDK 17 이미지 사용
FROM openjdk:17-jdk-slim

# Step 2: 작업 디렉토리 생성
WORKDIR /app

# Step 3: JAR 파일 복사
COPY build/libs/facticle-0.0.1-SNAPSHOT.jar app.jar

# Step 4: 컨테이너에서 실행할 명령어 설정
ENTRYPOINT ["java", "-jar", "app.jar"]
