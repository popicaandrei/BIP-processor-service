FROM openjdk:17
ADD target/processor-service.jar processor-service.jar
ENTRYPOINT ["java", "-jar","processor-service.jar"]
EXPOSE 8080