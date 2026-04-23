FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app 

COPY pom.xml .
COPY src ./src 
COPY mvnw .
COPY .mvn .mvn 

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

EXPOSE 8081

CMD ["java", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT:8081}", "-jar", "target/Millesime-0.0.1-SNAPSHOT.jar"]
