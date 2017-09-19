package org.asgs.twitterfeeds;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;

/**
 * Hello world!
 *
 */
public class StreamProcessor
{
    public static void main( String[] args )
    {
      Properties properties = new Properties();
      properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
      properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      properties.put("value.serializer", "org.asgs.twitterfeeds.processor.serdes.TwitterFeedSerializer");
      properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      properties.put("value.deserializer", "org.asgs.twitterfeeds.processor.serdes.TwitterFeedDeserializer");

      KStreamBuilder streamBuilder = new KStreamBuilder();
      streamBuilder.stream("tweet-stream").to("processed-tweets");

      KafkaStreams stream = new KafkaStreams(streamBuilder, properties);
      CountDownLatch latch = new CountDownLatch(1);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {stream.close(); latch.countDown();}, "Shutdown-hook"));

      try {
        stream.start();
        latch.await();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
      System.exit(0);
    }
}
