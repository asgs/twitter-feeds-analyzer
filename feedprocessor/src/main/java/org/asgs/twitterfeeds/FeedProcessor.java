package org.asgs.twitterfeeds;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;


import org.asgs.twitterfeeds.common.clients.TwitterKafkaClient;
import org.asgs.twitterfeeds.common.model.TwitterFeed;

public class FeedProcessor {

  public void processTweets() {
    TwitterKafkaClient<String, TwitterFeed> consumer = new TwitterKafkaClient<>(getKafkaClusterPropsForIngestion());

    TwitterKafkaClient<String, TwitterFeed> publisher = new TwitterKafkaClient<>(getKafkaClusterPropsForIngestion());
    consumer.subscribe().stream().forEach(e -> publisher.publish(e.getTweetId(), e));
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

  private Properties getKafkaClusterPropsForProduction() {
    Properties properties = new Properties();
    properties.put("bootstrap.servers", "localhost:9092");
    properties.put("acks", "all");
    properties.put("retries", 0);
    properties.put("batch.size", 16384);
    properties.put("linger.ms", 1);
    properties.put("buffer.memory", 33554432);
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("value.serializer", "org.asgs.twitterfeeds.processor.serdes.TwitterFeedSerializer");
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put("value.deserializer", "org.asgs.twitterfeeds.processor.serdes.TwitterFeedDeserializer");
    properties.put("producer-topic", "tweet-stream");
    properties.put("consumer-topic", "processed-tweets");
    return properties;
  }
}
