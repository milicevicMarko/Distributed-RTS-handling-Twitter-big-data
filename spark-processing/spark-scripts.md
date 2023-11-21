# Kafka

## Docker:

> Running on localhost:29092

Start:

```shell
docker-compose up -d
```

End:

```shell
docker-compose down
```

## Dockerized location:

```shell
cd /spark-app/target/scala-2.12
/spark/bin/spark-submit --class "spark.SparkStreamingExample" --master local[*] spark-processing_2.12-0.1.jar
/spark/bin/spark-submit --class "spark.SparkSessionExample" --packages "org.apache.spark:spark-sql-kafka-0-10_2.12:3.1.1" --master local[*] spark-processing_2.12-0.1.jar


cp conf/log4j.properties.template conf/log4j.properties
conf/log4j.properties
```

## Step by Step

1. Recompile the JAR
    
    ```shell
      sbt clean compile package
   ```

2. Run Docker
    
    ```shell
    docker-compose up -d
    ```

3. In the docker, run the spark-submit command
    
    ```shell
    . /run/run.sh
    ```
   
   **OR**

   ```shell
   docker exec spark-master . run.sh
   ```
   
3. In order to mock, do the following

    ```shell
   cd src\main\python
    venv\Scripts\activate
   python kafka_producer_mock.py
    ```
4. You should be seeing it in the docker console

5. ...but soon, you will see it in another kafka topic!


## Recompile

```shell
sbt clean compile package; echo "Compilation finished"; docker exec spark-master sh /run/run.sh
```
