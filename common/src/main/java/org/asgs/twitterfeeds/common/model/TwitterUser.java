package org.asgs.twitterfeeds.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TwitterUser {

  private String location;

  @JsonProperty(value="followers_count")
  private int followersCount;

  @JsonProperty(value="friends_count")
  private int friendsCount;

  @JsonProperty(value="statuses_count")
  private int statusesCount;

  @JsonProperty(value="lang")
  private String language;

  @Override
  public String toString() {
    return "[location=" + location + ";followers_count="+ followersCount + ";friends_count=" + friendsCount + "statuses_count=" + statusesCount + "lang=" + language + "]";
  }

}
