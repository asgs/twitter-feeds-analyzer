package org.asgs.twitterfeeds.common.clients;

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
public class KafkaClient<K, V> {

  private Producer<K, V> producer;
  private Consumer<K, V> consumer;
  private String kafkaProducerTopic;
  private String kafkaConsumerTopic;

  public KafkaClient(Properties properties) {
    producer = new KafkaProducer<>(properties);
    consumer = new KafkaConsumer<>(properties);
    kafkaProducerTopic = (String) properties.get("producer-topic");
    kafkaConsumerTopic = (String) properties.get("consumer-topic");
    if (kafkaConsumerTopic != null) {
      consumer.subscribe(Arrays.asList(kafkaConsumerTopic));
      System.out.println("subscribed to topic " + kafkaConsumerTopic);
    }
  }

  public void publish(K key, V value) {
    try {
      producer.send(createRecord(key, value));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Collection<V> subscribe() {
    Stream.Builder<V> builder = Stream.builder();
    while (true) {
      ConsumerRecords<K, V> records = consumer.poll(5000);
      if (records.isEmpty()) {
        break;
      }
      for (ConsumerRecord<K, V> record : records) {
        builder.add(record.value());
        consumer.commitAsync();
      }
    }

    return builder.build().collect(Collectors.toList());
  }

  private ProducerRecord<K, V> createRecord(K key, V value) {
    return new ProducerRecord<>(kafkaProducerTopic, key, value);
  }
}
