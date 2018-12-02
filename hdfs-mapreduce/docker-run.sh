#!/usr/bin/env bash

set -e

docker run -v `pwd`/mapreduce:/home/hadoop/mapreduce -it --rm hdfs-mapreduce bash /home/hadoop/mapreduce/basic-word-count/run.sh

