#!/usr/bin/python3

# imports
import numpy as np              # pip install numpy
import random

from kafka import KafkaProducer # pip install kafka-python
from time import time, sleep

# set up the producer
producer = KafkaProducer(bootstrap_servers='localhost:9092')

senders = "One and Only"
message = "The only message"
producer.send('twitter-topic', bytes(f"{senders}:{message}", encoding='utf8'))

print(f'sending data to kafka, #{1}')
