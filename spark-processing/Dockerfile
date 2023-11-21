FROM bde2020/spark-scala-template:3.1.1-hadoop3.2

COPY build.sbt /app/
COPY project /app/project

WORKDIR /app

COPY src /app/src

RUN sbt compile

CMD ["sbt", "run"]
