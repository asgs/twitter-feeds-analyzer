package org.asgs.twitterfeeds.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterUser {

  @JsonProperty(value = "id_str")
  private String id;

  private String location;

  @JsonProperty(value = "followers_count")
  private int followersCount;

  @JsonProperty(value = "friends_count")
  private int friendsCount;

  @JsonProperty(value = "statuses_count")
  private int statusesCount;

  @JsonProperty(value = "lang")
  private String language;

  public String getId() {
    return id;
  }

  public String getLocation() {
    return location;
  }

  public int getFollowersCount() {
    return followersCount;
  }

  public int getFriendsCount() {
    return friendsCount;
  }

  public int getStatusesCount() {
    return statusesCount;
  }

  public String getLanguage() {
    return language;
  }

  @Override
  public String toString() {
    return "[location="
        + location
        + ";followers_count="
        + followersCount
        + ";friends_count="
        + friendsCount
        + "statuses_count="
        + statusesCount
        + "lang="
        + language
        + "]";
  }
}
