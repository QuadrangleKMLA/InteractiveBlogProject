FROM openjdk:17
LABEL authors="jaykim"

ADD target/blog-0.0.1-SNAPSHOT.jar quadrangle-blog-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "quadrangle-blog-api.jar"]