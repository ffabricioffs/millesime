FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app 

COPY pom.xml .
COPY src ./src 
COPY mvnw .
COPY .mvn .mvn 

RUN chmod 777 mvnw 

ENV DB_URL=jdbc:postgresql://dpg-d7jph7vavr4c73chk1e0-a.ohio-postgres.render.com/millesime
ENV DB_USERNAME=admin
ENV DB_PASSWORD=MyylWWoU2uiPAqmcCbIa3l0PF9CqNuMP
ENV SERVER_PORT=8081
ENV HIBERNATE_DDL_AUTO=create-drop
ENV SQL_INIT_MODE=always

RUN ./mvnw package

CMD ["java","-jar","target/Millesime-0.0.1-SNAPSHOT.jar"]
