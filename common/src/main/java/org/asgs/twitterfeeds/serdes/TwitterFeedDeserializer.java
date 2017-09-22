package org.asgs.twitterfeeds.processor.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.asgs.twitterfeeds.common.model.TwitterFeed;

import java.io.IOException;
import java.util.Map;

public class TwitterFeedDeserializer implements Deserializer<TwitterFeed> {

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public TwitterFeed deserialize(String topic, byte[] data) {
    try {
      return mapper.readValue(data, TwitterFeed.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {}

  @Override
  public void configure(Map<String, ?> map, boolean key) {}
}
