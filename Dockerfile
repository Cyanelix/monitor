FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD /target/monitor.jar monitor.jar
EXPOSE 8080
RUN sh -c 'touch /monitor.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /monitor.jar" ]
