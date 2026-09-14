# user-service/Dockerfile (diğer servisler için de aynı)
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Maven build sonrası oluşan jar'ı image içine kopyalayacağız
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
