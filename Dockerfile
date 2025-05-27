# 1단계: 빌드 단계 - JDK 21 사용
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# 소스 복사
COPY . .

# Gradle 빌드 (테스트는 제외, 필요 시 -x test 제거)
RUN ./gradlew clean build -x test

# 2단계: 실행 단계 - JRE 21
FROM eclipse-temurin:21-jre

WORKDIR /app

# 빌드 결과물 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
