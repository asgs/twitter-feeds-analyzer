package org.asgs.twitterfeeds.resources;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.asgs.twitterfeeds.common.clients.DatabaseClient;
import static org.asgs.twitterfeeds.common.clients.ResultType.ALL;
import static org.asgs.twitterfeeds.common.clients.ResultType.FIRST;
import org.asgs.twitterfeeds.model.CommonStats;
import org.asgs.twitterfeeds.model.CommonStats.CommonStatsBuilder;

@Path("/tw-stats")
@Produces(MediaType.APPLICATION_JSON)
public class StatsResource {

  private DatabaseClient databaseClient;
  private static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();

  public StatsResource() {
    HikariConfig config = new HikariConfig("/hikari.properties");
    databaseClient = new DatabaseClient(new HikariDataSource(config));
  }

  @GET
  public CommonStats getStats() {
    CommonStatsBuilder builder = CommonStats.builder();

    gatherTotalTweets(builder);

    gatherTotalTweeters(builder);

    gatherTopTenFollowerCounts(builder);

    gatherTopTenStatusCounts(builder);

    gatherTopTenLanguages(builder);

    gatherTopTenLocations(builder);

    return builder.build();
  }

  private void gatherTotalTweets(CommonStatsBuilder builder) {
    List<Long> totalTweets = databaseClient.query("select count(*) from tweet", EMPTY_MAP, Long.class, FIRST);
    builder.totalTweets(totalTweets.get(0));
  }

  private void gatherTotalTweeters(CommonStatsBuilder builder) {
    List<Long> totalTweeters = databaseClient.query("select count(*) from tweeter", EMPTY_MAP, Long.class, FIRST);
    builder.totalTweeters(totalTweeters.get(0));
  }

  private void gatherTopTenFollowerCounts(CommonStatsBuilder builder) {
    List<Long> topTenFollowerCounts = databaseClient.query("select followers_count from tweeter order by followers_count desc limit 10", EMPTY_MAP, Long.class, ALL);
    builder.topTenFollowerCounts(topTenFollowerCounts);
  }

  private void gatherTopTenStatusCounts(CommonStatsBuilder builder) {
    List<Long> topTenStatusCounts = databaseClient.query("select statuses_count from tweeter order by statuses_count desc limit 10", EMPTY_MAP, Long.class, ALL);
    builder.topTenStatusCounts(topTenStatusCounts);
  }

  private void gatherTopTenLanguages(CommonStatsBuilder builder) {
    List<String> topTenLanguages = databaseClient.query("select lang, count(*) as count from tweet group by lang order by count desc limit 10", EMPTY_MAP, String.class, ALL);
    builder.topTenLanguages(topTenLanguages);
  }

  private void gatherTopTenLocations(CommonStatsBuilder builder) {
    List<String> topTenLocations = databaseClient.query("select location, count(*) as count from tweeter group by location order by count desc limit 10", EMPTY_MAP, String.class, ALL);
    builder.topTenLocations(topTenLocations);
  }
}
