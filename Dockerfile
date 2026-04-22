FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app 

COPY pom.xml .
COPY src ./src 
COPY mvnw .
COPY .mvn .mvn 
COPY .env .  
COPY load-env.sh .  

RUN chmod 777 mvnw && chmod +x load-env.sh  

RUN source ./load-env.sh && ./mvnw package  

CMD ["java","-jar","target/Millesime-0.0.1-SNAPSHOT.jar"]
