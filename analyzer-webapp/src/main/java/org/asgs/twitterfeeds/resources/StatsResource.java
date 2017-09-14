package org.asgs.twitterfeeds.resources;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.asgs.twitterfeeds.common.clients.DatabaseClient;
import org.asgs.twitterfeeds.model.CommonStats;
import org.asgs.twitterfeeds.model.CommonStats.CommonStatsBuilder;

@Path("/tw-stats")
@Produces(MediaType.APPLICATION_JSON)
public class StatsResource {

  private DatabaseClient databaseClient;

  public StatsResource() {
    HikariConfig config = new HikariConfig("/hikari.properties");
    databaseClient = new DatabaseClient(new HikariDataSource(config));
  }

  @GET
  public CommonStats getStats() {
    CommonStatsBuilder builder = CommonStats.builder();
    List<Long> totalTweets = databaseClient.query("select count(*) from tweet", Collections.emptyMap(), Long.class, DatabaseClient.ResultType.FIRST);
    System.out.println("totalTweets - " + totalTweets);
    builder.totalTweets(totalTweets.get(0));
    List<Long> totalTweeters = databaseClient.query("select count(*) from tweeter", Collections.emptyMap(), Long.class, DatabaseClient.ResultType.FIRST);
    builder.totalTweeters(totalTweeters.get(0));
    List<Long> topTenFollowerCounts = databaseClient.query("select followers_count from tweeter order by followers_count desc limit 10", Collections.emptyMap(), Long.class, DatabaseClient.ResultType.ALL);
    builder.topTenFollowerCounts(topTenFollowerCounts);
    List<Long> topTenStatusCounts = databaseClient.query("select statuses_count from tweeter order by statuses_count desc limit 10", Collections.emptyMap(), Long.class, DatabaseClient.ResultType.ALL);
    builder.topTenStatusCounts(topTenStatusCounts);
    List<String> topTenLanguages = databaseClient.query("select lang, count(*) as count from tweet group by lang order by count desc limit 10", Collections.emptyMap(), String.class, DatabaseClient.ResultType.ALL);
    builder.topTenLanguages(topTenLanguages);
    List<String> topTenLocations = databaseClient.query("select location, count(*) as count from tweeter group by location order by count desc limit 10", Collections.emptyMap(), String.class, DatabaseClient.ResultType.ALL);
    builder.topTenLocations(topTenLocations);
    return builder.build();
  }
}
