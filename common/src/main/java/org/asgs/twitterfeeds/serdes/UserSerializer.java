package org.asgs.twitterfeeds.processor.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.asgs.twitterfeeds.common.model.TwitterUser;

import java.util.Map;

public class UserSerializer implements Serializer<TwitterUser> {

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public byte[] serialize(String topic, TwitterUser user) {
    try {
      return mapper.writeValueAsBytes(user);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {}

  @Override
  public void configure(Map<String, ?> map, boolean key) {}
}
