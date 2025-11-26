FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
RUN ls /app/target

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/scrum-planning-be-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "scrum-planning-be-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080