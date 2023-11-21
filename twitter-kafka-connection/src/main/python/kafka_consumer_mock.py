#!/usr/bin/python3

# imports
from kafka import KafkaConsumer # pip install kafka-python

# set up the consumer
consumer = KafkaConsumer("twitter-topic", bootstrap_servers='localhost:9092')

count = 1

# until ^C
while True:
    for message in consumer:
        message = message.value
        print('Got: {}'.format(message))
