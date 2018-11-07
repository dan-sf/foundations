
FROM ubuntu:bionic

RUN set -ex \
    && apt-get update -yq \
    && apt-get upgrade -yq \
    && apt-get install -yq openjdk-8-jre \
    && apt-get install -yq vim \

# @Todo: What version of hadoop do I want?
RUN wget https://archive.apache.org/dist/hadoop/core/hadoop-2.7.3/hadoop-2.7.3.tar.gz

# @Todo: Update with proper installs for what hdfs needs

ARG BUILD_DEPS='python3-dev \
        libkrb5-dev \
        libsasl2-dev \
        libssl-dev \
        libffi-dev \
        build-essential \
        libblas-dev \
        liblapack-dev \
        libpq-dev \
        git'

RUN set -ex \
    && apt-get update -yqq \
    && apt-get upgrade -yqq \
    && apt-get install -yqq --no-install-recommends \
        $BUILD_DEPS \
        python3-pip \
        python3-requests \
        libmysqlclient-dev \
        apt-utils \
        curl \
        rsync \
        netcat \
        locales \
        vim \
        less \
        sudo \

