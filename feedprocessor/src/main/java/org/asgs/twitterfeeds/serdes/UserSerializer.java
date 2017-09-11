package org.asgs.twitterfeeds.processor.serdes;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import org.asgs.twitterfeeds.common.model.TwitterUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

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
  public void close() {
  }

  @Override
  public void configure(Map<String, ?> map, boolean key) {

  }

}
