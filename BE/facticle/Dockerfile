# Step 1: JDK 17 이미지 사용
FROM openjdk:17-jdk-slim AS builder

# Step 2: 작업 디렉토리 생성
WORKDIR /app

# Step 3: Gradle 빌드 파일 복사 (캐시 활용을 위해 순서 조정)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Step 4: Gradle 빌드 실행
RUN chmod +x ./gradlew && ./gradlew build -x test

# Step 5: 실행용 이미지 생성
FROM openjdk:17-jdk-slim
WORKDIR /app

# Step 6: 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/facticle-0.0.1-SNAPSHOT.jar app.jar

# Step 7: 컨테이너에서 실행할 명령어 설정
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-jar", "app.jar"]
