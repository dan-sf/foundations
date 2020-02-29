#!/usr/bin/env bash

docker run -it --rm --network kafka-flink-streaming_default kafka-flink-streaming-fd bash -c \
    "curl -s https://stream.wikimedia.org/v2/stream/recentchange \
    | grep data | sed 's/^data: //g' \
    | /opt/kafka/bin/kafka-console-producer.sh --broker-list kafka:9092 --topic wikiedits"

