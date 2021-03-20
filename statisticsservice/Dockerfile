FROM java:11-jre

ADD ./target/statistics-service.jar /app/
CMD ["java","-Xmx200m","-jar","/app/statistics-service.jar"]

EXPOSE 7000