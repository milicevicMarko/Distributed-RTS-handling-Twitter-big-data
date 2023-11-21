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

## Mock flow

### Consumer:

```shell
cd src/main/python 
python kafka_consumer_mock.py
```

### Producer:

```shell
cd src/main/python 
python kafka_producer_mock.py
```


## Local scrips

> Running on localhost:9092

```shell

```

## Start Zookeeper

```sh
cd c:/apache/kafka
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
```

## Start Server

```sh
cd c:/apache/kafka
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

## Create topic

```sh
cd c:/apache/kafka
.\bin\windows\kafka-topics.bat --create --topic twitter_topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

## List topic

```sh
cd c:/apache/kafka
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

## Create Producer

```sh
cd c:/apache/kafka
.\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic twitter_topic
```

## Create Consumer

```sh
cd c:/apache/kafka
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic twitter_topic --from-beginning
```

## Delete topic
```shell
cd c:/apache/kafka
.\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --delete --topic twitter_topic
```