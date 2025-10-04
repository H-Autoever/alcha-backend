FROM openjdk:21-jdk-slim AS builder

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN chmod +x ./gradlew

RUN ./gradlew dependencies

COPY src ./src

RUN ./gradlew build --no-daemon -x test

FROM openjdk:21-jdk-slim

WORKDIR /app

# 파이프라인 수정, SNAPSHOT.jar 로 끝나는 파일만 복사하려고.
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]