package org.asgs.twitterfeeds.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.Builder;
import lombok.Singular;

@Builder
public class CommonStats {

  @JsonProperty
  private long totalTweets;

  @JsonProperty
  private long totalTweeters;

  @Singular
  @JsonProperty
  private List<Long> topTenFollowerCounts;

  @Singular
  @JsonProperty
  private List<Long> topTenStatusCounts;

  @Singular
  @JsonProperty
  private List<String> topTenLanguages;

  @Singular
  @JsonProperty
  private List<String> topTenLocations;

}
