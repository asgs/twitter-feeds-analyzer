package org.asgs.twitterfeeds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.asgs.twitterfeeds.common.clients.DatabaseClient;
import org.asgs.twitterfeeds.common.model.TwitterFeed;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Processes the stream of tweets as they come in and publishes the processed data to interested
 * endpoints. E.g., another Kafka topic and a Database for now.
 */
public class StreamProcessor {
  public static void main(String[] args) {
    Properties properties = new Properties();
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put(
        "value.serializer", "org.asgs.twitterfeeds.processor.serdes.TwitterFeedSerializer");
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put(
        "value.deserializer", "org.asgs.twitterfeeds.processor.serdes.TwitterFeedDeserializer");

    DatabaseClient databasePublisher = new DatabaseClient(getDataSource());
    KStreamBuilder streamBuilder = new KStreamBuilder();
    streamBuilder
        .<String, TwitterFeed>stream("tweet-stream")
        .through("processed-tweets")
        .foreach((k, v) -> databasePublisher.saveTweet(v));

    KafkaStreams stream = new KafkaStreams(streamBuilder, properties);
    CountDownLatch latch = new CountDownLatch(1);

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  stream.close();
                  latch.countDown();
                },
                "Shutdown-hook"));

    try {
      stream.start();
      latch.await();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.exit(0);
  }

  private static DataSource getDataSource() {
    HikariConfig config =
        new HikariConfig("/hikari.properties"); // Available at the root of classpath.
    return new HikariDataSource(config);
  }
}
