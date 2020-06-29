package editsize;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;

// The accumulator class, which holds the state of the in-flight aggregates
class AverageAccumulator {
  public Long count;
  public Long sum;
  public AverageAccumulator() {
    count = 0L;
    sum = 0L;
  }
}

// Implementation of an aggregation function for averages
public class Average implements AggregateFunction<Tuple2<String, Long>, AverageAccumulator, Tuple2<String, Double>> {

  public AverageAccumulator createAccumulator() {
    return new AverageAccumulator();
  }

  public AverageAccumulator merge(AverageAccumulator a, AverageAccumulator b) {
    a.count += b.count;
    a.sum += b.sum;
    return a;
  }

  public AverageAccumulator add(Tuple2<String, Long> value, AverageAccumulator acc) {
    acc.sum += (Long) value.getField(1);
    acc.count++;
    return acc;
  }

  public Tuple2<String, Double> getResult(AverageAccumulator acc) {
    if (acc.count == 0) {
      return new Tuple2<String, Double>("average_edit_size_mb", 0.0);
    }
    // Since we get bytes from the input data we divide by 1M to get output MB
    return new Tuple2<String, Double>("average_edit_size_mb", acc.sum / (double) acc.count / 1000000);
  }
}
