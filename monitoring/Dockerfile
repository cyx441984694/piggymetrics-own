FROM java:11-jre

ADD ./target/monitoring.jar /app/
CMD ["java","-Xmx200m","-jar","/app/monitoring.jar"]

EXPOSE 8080