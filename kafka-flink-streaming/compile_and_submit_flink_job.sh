#!/usr/bin/env bash

dir=`dirname ${BASH_SOURCE[0]}`
repo_root=`cd $dir && pwd`

docker run --rm --network kafka-flink-streaming_default -v ${repo_root}/_m2_cache:/opt/flink/jobs/editsize/_m2_cache kafka-flink-streaming-fd bash -c \
    "cd /opt/flink/jobs/editsize && \
    mvn -Dmaven.repo.local=_m2_cache package && \
    ../../bin/flink run -d -m jobmanager:8081 target/editsize-1.0-SNAPSHOT.jar"

