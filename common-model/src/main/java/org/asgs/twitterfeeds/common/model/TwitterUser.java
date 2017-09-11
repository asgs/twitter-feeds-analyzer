package org.asgs.twitterfeeds.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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

}
