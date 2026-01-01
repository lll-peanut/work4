FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="lll-peanut<peanut_l@qq.com>"

ENV JAR_NAME=work4-0.0.1-SNAPSHOT.jar
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
ENV TOMCAT_BASEDIR=/usr/local/tomcat-work
ENV LOG_BASEDIR=/home/peanut/myApp/log
ENV MYPATH=/usr/local/springboot
WORKDIR $MYPATH

COPY ${JAR_NAME} ./
EXPOSE 80

CMD ["sh", "-c", "java $JAVA_OPTS -jar $JAR_NAME"]
