FROM openjdk:21-jdk-slim AS builder

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN chmod +x ./gradlew

RUN ./gradlew dependencies

COPY src ./src

RUN ./gradlew build --no-daemon -x test

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]