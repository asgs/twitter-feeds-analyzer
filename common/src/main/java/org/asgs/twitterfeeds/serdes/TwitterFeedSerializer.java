package org.asgs.twitterfeeds.processor.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.asgs.twitterfeeds.common.model.TwitterFeed;

import java.util.Map;

public class TwitterFeedSerializer implements Serializer<TwitterFeed> {

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public byte[] serialize(String topic, TwitterFeed feed) {
    try {
      return mapper.writeValueAsBytes(feed);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {}

  @Override
  public void configure(Map<String, ?> map, boolean key) {}
}
