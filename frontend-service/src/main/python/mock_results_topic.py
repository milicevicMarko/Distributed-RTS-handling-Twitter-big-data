#!/usr/bin/python3

# imports
import numpy as np              # pip install numpy
import random
import json

from kafka import KafkaProducer  # pip install kafka-python
from time import time, sleep


def choose_message():
    messages = [
        {
            'hello': np.random.randint(0, 400),
            'and': np.random.randint(500, 1000),
            'etf': np.random.randint(0, 10),
            'audience': np.random.randint(0, 200),
        }
    ]
    return random.choice(messages)


# set up the producer
producer = KafkaProducer(bootstrap_servers='localhost:9092')
topic = 'results-topic'
count = 1
senders = ["fd8521c5-45b3-4fa2-8b2e-94ab8edd9082",
           "The Other Gal", "The Other Person"]

# until ^C
while True:
    for sessionId in senders:
        msg = choose_message()
        producer.send(topic, key=bytes(f"{sessionId}", encoding='utf8'), value=bytes(
            f"{json.dumps(msg)}", encoding='utf8'))
        count += 1
        print(f'sending data to kafka, #{count}')

    sleep(1)
