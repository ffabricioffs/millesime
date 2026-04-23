FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app 

COPY pom.xml .
COPY src ./src 
COPY mvnw .
COPY .mvn .mvn 
COPY run-app.sh .

RUN chmod +x mvnw run-app.sh

RUN ./mvnw clean package -DskipTests

CMD ["./run-app.sh"]
