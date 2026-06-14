FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
