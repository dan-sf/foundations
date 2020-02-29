#!/usr/bin/env bash

# Wait for kafka to become available
while ! nc -z kafka 9092
do
    sleep 0.2
done

# Create topic for sending wikipedia edits through
bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic wikiedits

# Create topic for sending wikipedia edits through
bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic average_edits_mb

