#!/usr/bin/env bash

docker run -it --rm --network kafka-flink-streaming_default kafka-flink-streaming-fd bash -c \
    "/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic wikiedits"

