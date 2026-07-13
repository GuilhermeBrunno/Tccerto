FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache tzdata
ENV TZ=America/Sao_Paulo
WORKDIR /app
RUN mkdir -p data uploads
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV SERVER_PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]
