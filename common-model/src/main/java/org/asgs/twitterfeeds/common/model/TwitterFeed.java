package org.asgs.twitterfeeds.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterFeed {

  @JsonProperty(value="id_str")
  private String tweetId;

  @JsonProperty(value="text")
  private String tweet;

  @JsonProperty(value="lang")
  private String tweetLanguage;

  @JsonProperty(value="timestamp_ms")
  private long timestamp;

  private TwitterUser user;

}
