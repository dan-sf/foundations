FROM ubuntu:bionic

RUN set -ex \
    && apt-get update -yq \
    && apt-get upgrade -yq \
    && apt-get install -yq openjdk-8-jdk \
    && apt-get install -yq build-essential \
    && apt-get install -yq vim \
    && apt-get install -yq git \
    && apt-get install -yq wget \
    && apt-get install -yq curl \
    && apt-get install -yq netcat \
    && apt-get install -yq maven \
    && apt-get install -yq sudo

RUN mkdir /tmp/downloads
WORKDIR /tmp/downloads

# Download keys and asc files for verification
RUN wget -q https://archive.apache.org/dist/zookeeper/KEYS \
    && mv KEYS zookeeper_KEYS \
    && wget -q https://archive.apache.org/dist/kafka/KEYS \
    && mv KEYS kafka_KEYS \
    && wget -q https://archive.apache.org/dist/flink/KEYS \
    && mv KEYS flink_KEYS

RUN wget -q https://archive.apache.org/dist/zookeeper/zookeeper-3.5.6/apache-zookeeper-3.5.6-bin.tar.gz.asc \
    https://archive.apache.org/dist/kafka/2.4.0/kafka_2.13-2.4.0.tgz.asc \
    https://archive.apache.org/dist/flink/flink-1.9.2/flink-1.9.2-bin-scala_2.12.tgz.asc

# Download resources. To speed up the docker build, we could use Apache mirrors
# that are closer. Currently we just download straight from Apache
RUN wget -q https://archive.apache.org/dist/zookeeper/zookeeper-3.5.6/apache-zookeeper-3.5.6-bin.tar.gz \
    https://archive.apache.org/dist/kafka/2.4.0/kafka_2.13-2.4.0.tgz \
    https://archive.apache.org/dist/flink/flink-1.9.2/flink-1.9.2-bin-scala_2.12.tgz

# Verify downloads
RUN gpg --import zookeeper_KEYS \
    && gpg --verify apache-zookeeper-3.5.6-bin.tar.gz.asc apache-zookeeper-3.5.6-bin.tar.gz \
    && gpg --import kafka_KEYS \
    && gpg --verify kafka_2.13-2.4.0.tgz.asc kafka_2.13-2.4.0.tgz \
    && gpg --import flink_KEYS \
    && gpg --verify flink-1.9.2-bin-scala_2.12.tgz.asc flink-1.9.2-bin-scala_2.12.tgz

# Decompress and mv source files
RUN tar -xf apache-zookeeper-3.5.6-bin.tar.gz \
    && tar -xf kafka_2.13-2.4.0.tgz \
    && tar -xf flink-1.9.2-bin-scala_2.12.tgz

RUN mv apache-zookeeper-3.5.6-bin /opt/zookeeper
RUN mv kafka_2.13-2.4.0 /opt/kafka/
RUN mv flink-1.9.2 /opt/flink/

# Setup conf files
WORKDIR /opt/
COPY zoo.cfg zookeeper/conf/
COPY flink-conf.yaml flink/conf/
RUN sed -i 's/zookeeper.connect=localhost:2181/zookeeper.connect=zookeeper:2181/' kafka/config/server.properties

# Copy source
COPY create_topics.sh kafka/
COPY editsize flink/jobs/editsize

EXPOSE 2181 9092 8081
RUN rm -r /tmp/downloads

