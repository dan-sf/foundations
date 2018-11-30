
FROM ubuntu:bionic

RUN set -ex \
    && apt-get update -yq \
    && apt-get upgrade -yq \
    && apt-get install -yq openjdk-8-jdk \
    && apt-get install -yq build-essential \
    && apt-get install -yq vim \
    && apt-get install -yq git \
    && apt-get install -yq wget \
    && apt-get install -yq sudo

# Export env vars
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
ENV HADOOP_HOME=/usr/local/hadoop
ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
ENV HADOOP_MAPRED_HOME=$HADOOP_HOME
ENV HADOOP_COMMON_HOME=$HADOOP_HOME
ENV HADOOP_HDFS_HOME=$HADOOP_HOME
ENV YARN_HOME=$HADOOP_HOME

WORKDIR /tmp/install

# We are downloading the hadoop package from a mirror site, feel free to
# change this to a mirror that makes more sense for your location.
# Alternatively, download directly from Apache:
# https://archive.apache.org/dist/hadoop/common/hadoop-3.1.1/hadoop-3.1.1.tar.gz
# We also verify the download here

# RUN wget http://mirror.cc.columbia.edu/pub/software/apache/hadoop/common/hadoop-3.1.1/hadoop-3.1.1.tar.gz \
#          https://archive.apache.org/dist/hadoop/common/KEYS \
#          https://archive.apache.org/dist/hadoop/common/hadoop-3.1.1/hadoop-3.1.1.tar.gz.asc \
#     && gpg --import KEYS \
#     && gpg --verify hadoop-3.1.1.tar.gz.asc hadoop-3.1.1.tar.gz

RUN pwd

# @Debug
ADD hadoop-3.1.1 /tmp/install/hadoop-3.1.1

RUN ls

# ---------

# @Note: this volume is specified at run time (should map to the mapreduce dir here)
#VOLUME /usr/local/mapreduce # @Note: this should really be in the home dir of the hadoop user
# @Question: will this dir be owned by root?
VOLUME /home/hadoop/mapreduce


# RUN tar xf hadoop-3.1.1.tar.gz \
#     && mv hadoop-3.1.1 /usr/local \
# @Debug: remove this line and uncomment the above
RUN mv hadoop-3.1.1 /usr/local \
    && useradd -ms /bin/bash -d /home/hadoop hadoop \
    && cd /usr/local \
    && ln -s ./hadoop-3.1.1 hadoop

# Cleanup install dir
WORKDIR /
RUN rm -rf /tmp/install

USER hadoop

