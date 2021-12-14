FROM gradle:7.3.1-jdk11-alpine as build
WORKDIR /app
COPY . .
RUN gradle build -x test

FROM openjdk
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/build/libs/rest-0.0.1-SNAPSHOT.jar .
CMD java -jar rest-0.0.1-SNAPSHOT.jar
