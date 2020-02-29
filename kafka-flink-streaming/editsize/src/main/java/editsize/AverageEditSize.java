package editsize;


import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.formats.json.JsonNodeDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Properties;

/**
 * This is the main class for our Flink job, it reads streaming data from a
 * Kafka topic that is producing wikipedia edit information and calculates the
 * average edit size in MB over the last 10 seconds.
 */
public class AverageEditSize  {
  public static void main(String[] args) throws Exception {
    // Set Kafka bootstrap server location
    Properties properties = new Properties();
    properties.setProperty("bootstrap.servers", "kafka:9092");

    // Get execution environment
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    // Set up the Kafka consumer reading json as input
    FlinkKafkaConsumer<ObjectNode> wikieditsKafkaTopic
        = new FlinkKafkaConsumer<>("wikiedits", new JsonNodeDeserializationSchema(), properties);
    wikieditsKafkaTopic.setStartFromLatest();

    // Set the Kafka source as input to our DataStream
    DataStream<ObjectNode> wikiedits = env.addSource(wikieditsKafkaTopic);

    // Here is where the real processing happens. We read from the source
    // topic, pull out the "revision" information (map). Remove nulls from
    // the stream (filter). Get the abs difference in the new/old edits in
    // bytes (map). Set the key by information and specify our window.
    // Provide custom aggregate function to generate averages of the change
    // in edits. We output the final data as average edit size in MB over
    // the given window
    SingleOutputStreamOperator<Tuple2<String, Double>> averageEdits = wikiedits
      .map(edit -> edit.get("revision"))
      .filter(reversion -> reversion != null)
      .filter(reversion -> reversion.get("new") != null && reversion.get("old") != null)
      .map(new MapFunction<JsonNode, Tuple2<String, Long>>() {
        @Override
        public Tuple2<String, Long> map(JsonNode input) throws Exception {
          JsonNode newBytes = input.get("new");
          JsonNode oldBytes = input.get("old");
          return new Tuple2<>("abs_edit_difference", Math.abs(newBytes.asLong() - oldBytes.asLong()));
        }
      })
      .returns(TypeInformation.of(new TypeHint<Tuple2<String, Long>>() {}))
      .keyBy(0)
      // Use a tumbling 10 second window, we could have done a countWindow
      // which would've only processed 10 records at a time which was useful
      // for verification of the aggregations
      .timeWindow(Time.seconds(10))
      .aggregate(new Average());

    // Create a producer for the output average_edits_mb topic
    FlinkKafkaProducer<String> averageEditsTopic = new FlinkKafkaProducer<String>(
        "kafka:9092",
        "average_edits_mb",
        new SimpleStringSchema());

    // Output string records in json format
    SingleOutputStreamOperator<String> averageEditsJson = averageEdits
        .map(average -> String.format("{\"%s\": %.2f}", average.getField(0), average.getField(1)))
        .returns(TypeInformation.of(new TypeHint<String>() {}));

    // Add the Kafka output topic as our sink
    averageEditsJson.addSink(averageEditsTopic);

    // Execute the Flink job
    env.execute();
  }
}
