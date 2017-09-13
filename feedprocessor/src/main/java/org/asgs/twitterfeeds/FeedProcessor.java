package org.asgs.twitterfeeds;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;


import org.asgs.twitterfeeds.common.clients.KafkaClient;
import org.asgs.twitterfeeds.common.model.TwitterFeed;

public class FeedProcessor {

  public void processTweets() {
    KafkaClient<String, TwitterFeed> kafkaConsumer = new KafkaClient<>(getKafkaClusterPropsForIngestion());
    KafkaClient<String, TwitterFeed> kafkaPublisher = new KafkaClient<>(getKafkaClusterPropsForIngestion());
    DatabaseClient databasePublisher = new DatabaseClient(); // Pass the dataSource;
    kafkaConsumer.subscribe().stream().forEach(e -> {
      System.out.println("Processed tweet with Id " + e.getTweetId());
      kafkaPublisher.publish(e.getTweetId(), e);
      databasePublisher.saveTweet(e);
    });
  }

  public static void main(String[] args) {
    FeedProcessor processor = new FeedProcessor();
    processor.processTweets();
  }

  private Properties getKafkaClusterPropsForIngestion() {
    Properties properties = new Properties();
    properties.put("bootstrap.servers", "localhost:9092");
    properties.put("acks", "all");
    properties.put("group.id", "twitter-feed-processors");
    properties.put("retries", 0);
    properties.put("batch.size", 16384);
    properties.put("linger.ms", 1);
    properties.put("buffer.memory", 33554432);
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("value.serializer", "org.asgs.twitterfeeds.processor.serdes.TwitterFeedSerializer");
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put("value.deserializer", "org.asgs.twitterfeeds.processor.serdes.TwitterFeedDeserializer");
    properties.put("consumer-topic", "tweet-stream");
    properties.put("producer-topic", "processed-tweets");
    return properties;
  }
}
