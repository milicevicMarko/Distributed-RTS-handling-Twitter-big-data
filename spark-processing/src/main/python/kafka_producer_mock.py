#!/usr/bin/python3

# imports
import numpy as np              # pip install numpy
import random

from kafka import KafkaProducer  # pip install kafka-python
from time import time, sleep


def choose_message():
    messages = ['Some message', 'Any message or even a different message',
                'All messages', 'No messages', 'Who even knows anymore?']
    return random.choice(messages)


# set up the producer
producer = KafkaProducer(bootstrap_servers='localhost:9092')

count = 1
senders = ["The Other Guy", "The Other Gal", "The Other Person"]


def send_structure():
    producer.send('stream-structure-topic', key=bytes(
        f"{senders[0]}", encoding='utf8'), value=bytes(f"{'(some) OR (message)'}", encoding='utf8'))
    producer.send('stream-structure-topic', key=bytes(
        f"{senders[1]}", encoding='utf8'), value=bytes(f"{'(who)'}", encoding='utf8'))
    producer.send('stream-structure-topic', key=bytes(
        f"{senders[2]}", encoding='utf8'), value=bytes(f"{'(no) OR (messages)'}", encoding='utf8'))


print('sent stream structure data to kafka')

# until ^C
while True:
    for sessionId in senders:
        if (count % 100 == 0):
            send_structure()
        msg = choose_message()
        producer.send('twitter-topic', key=bytes(
            f"{sessionId}", encoding='utf8'), value=bytes(f"{msg}", encoding='utf8'))
        count += 1
        print(f'sending data to kafka, #{count}')

    sleep(5.0)
