package org.asgs.twitterfeeds.common.clients;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.stream.Collectors;

/**
 * A Kafka client which will manage publish, subscribe mechanisms relating to
 * Tweets. It's intended to be instantiated on a per-topic basis. The topic
 * information along with all other Kafka cluster-specific information will
 * need to be made available within the properties instance provided during
 * the construction of the client.
 */
public class TwitterKafkaClient {

  private Producer<String, String> producer;
  private Consumer<String, String> consumer;
  private ObjectMapper mapper = new ObjectMapper();
  private String kafkaProducerTopic;
  private String KafkaConsumerTopic;

  public TwitterKafkaClient(Properties properties) {
    producer = new KafkaProducer<>(properties);
    consumer = new KafkaConsumer<>(properties);
    kafkaProducerTopic = (String) properties.get("producer-topic");
    KafkaConsumerTopic = (String) properties.get("consumer-topic");
    consumer.subscribe(Arrays.asList(KafkaConsumerTopic));
  }

  public void publish(String tweet) throws IOException {
    Map<?, ?> tree = mapper.readValue(tweet, Map.class);
    String key = (String) tree.get("id_str");
    String value = tweet;
    producer.send(createRecord(key, value));
  }

  public Collection<String> subscribe() {
    ConsumerRecords<String, String> records = consumer.poll(5000);
    Stream.Builder<String> builder = null;
    for (ConsumerRecord<String, String> record : records) {
      builder = builder.add(record.value());
    }
    return builder.build().collect(Collectors.toList());
  }

  private ProducerRecord<String, String> createRecord(String key, String value) {
    return new ProducerRecord<>(kafkaProducerTopic, key, value);
  }
}
