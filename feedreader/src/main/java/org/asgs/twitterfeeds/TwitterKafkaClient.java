package org.asgs.twitterfeeds;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;

import java.util.Map;
import java.util.Properties;

public class TwitterKafkaClient {

  private Producer<String, String> producer;
  private ObjectMapper mapper = new ObjectMapper();
  private String kafkaTopic;

  public TwitterKafkaClient(Properties properties) {
    producer = new KafkaProducer<>(properties);
    kafkaTopic = (String) properties.get("topic");
  }

  public void publish(String tweet) throws IOException {
    Map<?, ?> tree = mapper.readValue(tweet, Map.class);
    String key = (String) tree.get("id_str");
    String value = tweet;
    producer.send(createRecord(key, value));
  }

  private ProducerRecord<String, String> createRecord(String key, String value) {
    return new ProducerRecord<>(kafkaTopic, key, value);
  }
}
