package org.asgs.twitterfeeds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.asgs.twitterfeeds.common.clients.DatabaseClient;
import org.asgs.twitterfeeds.common.model.TwitterFeed;

import javax.sql.DataSource;
import java.sql.SQLException;
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
    properties.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    properties.put(
        StreamsConfig.VALUE_SERDE_CLASS_CONFIG, "org.asgs.twitterfeeds.serdes.FeedSerde");

    DatabaseClient databasePublisher = new DatabaseClient(getDataSource());
    KStreamBuilder streamBuilder = new KStreamBuilder();
    streamBuilder
        .<String, TwitterFeed>stream("tweet-stream")
        .filter((k, v) -> v.getTweetId() != null)
        .through("processed-tweets")
        .foreach(
            (k, v) -> {
              System.out.println("Pushing tweet to DB; " + v);
              try {
                databasePublisher.saveTweet(v);
              } catch (Exception e) {
                e.printStackTrace();
              }
            });

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
