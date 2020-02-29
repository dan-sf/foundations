Kafka Flink streaming
=====================

This project is a basic example of how to spin up
[Kafka](https://kafka.apache.org/) and [Flink](https://flink.apache.org/)
locally, and use those frameworks to process some streaming data. Here we will
make use of some open Wikipedia article edit data as a source stream of data to
do our processing. The Flink job processes Wikipedia edit records and
calculates the average edit size in MB over 10 second tumbling windows (check
out the Flink docs for more info on windowing). All the components are started
locally via `docker-compose`. I used [jghoman's](https://github.com/jghoman)
[botcount](https://github.com/jghoman/botcount) example as a starting point for
my Flink code. These examples are mostly for educational purposes, so we can
get a better feel for stream processing with Apache Flink.

Build Docker image
------------------

First we need to build the base docker image. For this project we just create
one large container which satisfies all the services that we will want to spin
up. Normally you'd want to break those out as separate images but for
simplicity we use one here. You can build the container from the base of this
repo with the following command:

```
docker build . -t kafka-flink-streaming-fd
```

The rest of the code assumes that this container is built with the
`kafka-flink-streaming-fd` tag.

This build installs some executables, downloads the Kafka/Zookeeper/Flink
binaries from Apache, and then moves a few configuration files and scripts into
the newly created base image.

Spin up locally
---------------

We are now able to run the docker compose:

```
docker-compose up -d
```

This will start Zookeeper, Kafka, and Flink's jobmanager and taskmanager
processes in the background. It also runs a short lived container to create the
Kafka topics we'll use to stream data with.

Once your containers are running, you should be able to view the Flink UI at
`localhost:8081`.

Compile and submit the Flink job
--------------------------------

You can now compile and submit the `editsize` Flink job:

```
./compile_and_submit_flink_job.sh
```

This will compile the `editsize` Flink job inside a container, and submit the
jar to the Flink job manager.

Once the job has been compiled/submitted you should see the Flink job running
in the dashboard UI.

We use a container to compile/submit, but this could easily be done from
outside of a container as well. Here we compile with a container to avoid
dependency issues. We also use a local cache volume so the maven dependencies
don't have to be re-downloaded every time we want to re-run.

Start stream processing
-----------------------

We should now be ready to process some streaming data.

Let's start the producing edit events to our `wikiedits` Kafka topic.

```
./produce_edits.sh
```

You'll see a stream of `>` characters as we produce data to the topic. If you
want to stop producing you can hit `Ctrl-C` to kill the container.

We can now view the Wikipedia edits that we are streaming in by listening to
the `wikiedits` topic:

```
./consume_edits.sh
```

You'll see a bunch of json text quickly filling your screen. You can learn more
about the source Wikipedia data
[here](https://wikitech.wikimedia.org/wiki/Event_Platform/EventStreams)
if you're interested.

The Flink job we submitted should already be processing this data. We can view
that processed data by consuming from the `average_edits_mb` topic:

```
./consume_averages.sh
```

Here you should see the average edit size in MB coming in at 10 second
intervals. For example:

```
./consume_averages.sh
{"average_edit_size_mb": 20.72}
{"average_edit_size_mb": 24.48}
{"average_edit_size_mb": 24.08}
{"average_edit_size_mb": 36.66}
{"average_edit_size_mb": 35.01}
{"average_edit_size_mb": 24.47}
{"average_edit_size_mb": 27.86}
```

Killing the producer container will cause the Flink job to fail. However, you
can re-submit if you want to start it back up again.

Shut it down
------------

You can close out all the producer and consumer containers via `Ctrl-C`. Once
those containers are no longer running you can shut down the rest of the
containers with docker compose:

```
docker-compose down
```

Final thoughts
--------------

This barely scratches the surface in terms of what can be done with these
frameworks but I hope it is a helpful start. We have spun up some
infrastructure (not really, but we can pretend), streamed some data, and
processed it in real time. Next steps would be to write more Flink jobs to
explore other capabilities that Flink has to offer.

