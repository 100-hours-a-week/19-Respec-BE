# ✅ Dockerfile (환경변수 기반 실행, 설정파일 포함하지 않음)
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
