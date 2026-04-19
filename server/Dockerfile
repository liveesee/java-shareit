FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY ../pom.xml ./pom.xml
COPY server/pom.xml ./server/pom.xml
COPY gateway/pom.xml ./gateway/pom.xml
COPY server/src ./server/src
COPY gateway/src ./gateway/src
RUN mvn clean package -DskipTests -pl server -am

FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
COPY --from=build /app/server/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]