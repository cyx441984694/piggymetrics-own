FROM java:11-jre

ADD ./target/turbine-stream-service.jar /app/
CMD ["java","-Xmx200m","-jar","/app/turbine-stream-service.jar"]

EXPOSE 8989