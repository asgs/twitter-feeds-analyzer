package org.asgs.twitterfeeds.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
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

  public String getTweetId() {
    return tweetId;
  }

  @Override
  public String toString() {
    return "[id_str=" + tweetId + ";tweet="+ tweet + ";tweetLanguage=" + tweetLanguage + "timestamp=" + timestamp + "user=" + user + "]";
  }

}
