package org.asgs.twitterfeeds.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.asgs.twitterfeeds.common.model.TwitterFeed;
import org.asgs.twitterfeeds.processor.serdes.TwitterFeedDeserializer;
import org.asgs.twitterfeeds.processor.serdes.TwitterFeedSerializer;

import java.util.Map;

public class FeedSerde implements Serde<TwitterFeed> {

  @Override
  public void close() {}

  @Override
  public void configure(Map<String, ?> map, boolean key) {}

  public Deserializer<TwitterFeed> deserializer() {
    return new TwitterFeedDeserializer();
  }

  public Serializer<TwitterFeed> serializer() {
    return new TwitterFeedSerializer();
  }
}
