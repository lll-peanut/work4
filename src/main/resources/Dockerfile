FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="lll-peanut<peanut_l@qq.com>"

ENV JAR_NAME=work4-0.0.1-SNAPSHOT.jar
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
ENV MYPATH /usr/local/springboot
WORKDIR $MYPATH

COPY ${JAR_NAME} ./
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar ${JAR_NAME}"]