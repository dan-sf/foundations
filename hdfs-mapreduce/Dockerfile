
FROM ubuntu:bionic

RUN set -ex \
    && apt-get update -yq \
    && apt-get upgrade -yq \
    && apt-get install -yq openjdk-8-jre \
    && apt-get install -yq build-essential \
    && apt-get install -yq vim \
    && apt-get install -yq git \
    && apt-get install -yq wget

# @Question: Should this actually be JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/ ???
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
ENV HADOOP_HOME=/usr/local/hadoop

WORKDIR /tmp/install

# We are downloading the hadoop package from a mirror site, feel free to
# change this to a mirror that makes more sense for your location.
# Alternatively, download directly from Apache:
# https://archive.apache.org/dist/hadoop/common/hadoop-3.1.1/hadoop-3.1.1.tar.gz
# We also verify the download here
RUN wget http://mirror.cc.columbia.edu/pub/software/apache/hadoop/common/hadoop-3.1.1/hadoop-3.1.1.tar.gz \
         https://archive.apache.org/dist/hadoop/common/KEYS \
         https://archive.apache.org/dist/hadoop/common/hadoop-3.1.1/hadoop-3.1.1.tar.gz.asc \
    && gpg --import KEYS \
    && gpg --verify hadoop-3.1.1.tar.gz.asc hadoop-3.1.1.tar.gz

# ---------


RUN tar xf hadoop-3.1.1.tar.gz \
    && mv hadoop-3.1.1 /usr/local \
    && useradd -ms /bin/bash -d ${HADOOP_HOME} hadoop \
    && cd /usr/local \
    && ln -s ./hadoop-3.1.1 hadoop

# @Question: Do we actually need the hadoop user?

# Cleanup install dir
WORKDIR /
RUN rm -rf /tmp/install

USER hadoop

