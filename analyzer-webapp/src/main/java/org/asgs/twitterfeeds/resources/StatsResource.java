package org.asgs.twitterfeeds.resources;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.asgs.twitterfeeds.common.clients.RdbmsClient;
import org.asgs.twitterfeeds.model.CommonStats;
import org.asgs.twitterfeeds.model.CommonStats.CommonStatsBuilder;
import org.glassfish.jersey.server.ChunkedOutput;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.asgs.twitterfeeds.common.clients.ResultType.ALL;
import static org.asgs.twitterfeeds.common.clients.ResultType.FIRST;

@Path("/tw-stats")
@Produces(MediaType.APPLICATION_JSON)
public class StatsResource {

  private static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();
  private RdbmsClient rdbmsClient;

  public StatsResource() {
    HikariConfig config = new HikariConfig("/hikari.properties");
    rdbmsClient = new RdbmsClient(new HikariDataSource(config));
  }

  @GET
  public CommonStats getStats(@QueryParam("from") Long fromEpoch, @QueryParam("to") Long toEpoch) {
    CommonStatsBuilder builder = CommonStats.builder();

    gatherTotalTweets(builder, fromEpoch, toEpoch);

    gatherTotalTweeters(builder);

    gatherTopTenFollowerCounts(builder);

    gatherTopTenStatusCounts(builder);

    gatherTopTenLanguages(builder, fromEpoch, toEpoch);

    gatherTopTenLocations(builder);

    return builder.build();
  }

  @GET
  @Path("/chunks")
  public ChunkedOutput<CommonStats> getChunkedStats(
      @QueryParam("from") Long fromEpoch, @QueryParam("to") Long toEpoch) {
    final ChunkedOutput<CommonStats> chunkedOutput = new ChunkedOutput<>(CommonStats.class);
    AtomicInteger counter = new AtomicInteger();
    Runnable queryStats =
        () -> {
          while (true) {
            System.out.println("Querying stats from DB.");
            CommonStats commonStats = getStats(fromEpoch, toEpoch);
            try {
              System.out.println("Writing stats to chunkedOutput.");
              chunkedOutput.write(commonStats);
              if (counter.incrementAndGet() >= 4) {
                chunkedOutput.close();
                break;
              }
            } catch (IOException e) {
              e.printStackTrace();
            }

            try {
              Thread.sleep(5000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        };
    new Thread(queryStats).start();
    return chunkedOutput;
  }

  private void gatherTotalTweets(CommonStatsBuilder builder, Long fromEpoch, Long toEpoch) {
    List<Long> totalTweets =
        rdbmsClient.query(
            "select count(*) from tweet where epoch_ms between :from and :to",
            getFromToBindingMap(fromEpoch, toEpoch),
            Long.class,
            FIRST);
    builder.totalTweets(totalTweets.get(0));
  }

  private void gatherTotalTweeters(CommonStatsBuilder builder) {
    List<Long> totalTweeters =
        rdbmsClient.query("select count(*) from tweeter", EMPTY_MAP, Long.class, FIRST);
    builder.totalTweeters(totalTweeters.get(0));
  }

  private void gatherTopTenFollowerCounts(CommonStatsBuilder builder) {
    List<Long> topTenFollowerCounts =
        rdbmsClient.query(
            "select followers_count from tweeter order by followers_count desc limit 10",
            EMPTY_MAP,
            Long.class,
            ALL);
    builder.topTenFollowerCounts(topTenFollowerCounts);
  }

  private void gatherTopTenStatusCounts(CommonStatsBuilder builder) {
    List<Long> topTenStatusCounts =
        rdbmsClient.query(
            "select statuses_count from tweeter order by statuses_count desc limit 10",
            EMPTY_MAP,
            Long.class,
            ALL);
    builder.topTenStatusCounts(topTenStatusCounts);
  }

  private void gatherTopTenLanguages(CommonStatsBuilder builder, Long fromEpoch, Long toEpoch) {
    List<String> topTenLanguages =
        rdbmsClient.query(
            "select lang, count(*) as count from tweet where epoch_ms between :from and :to group by lang order by count desc limit 10",
            getFromToBindingMap(fromEpoch, toEpoch),
            String.class,
            ALL);
    builder.topTenLanguages(topTenLanguages);
  }

  private void gatherTopTenLocations(CommonStatsBuilder builder) {
    List<String> topTenLocations =
        rdbmsClient.query(
            "select location, count(*) as count from tweeter group by location order by count desc limit 10",
            EMPTY_MAP,
            String.class,
            ALL);
    builder.topTenLocations(topTenLocations);
  }

  private Map<String, Object> getFromToBindingMap(Long fromEpoch, Long toEpoch) {
    Map<String, Object> map = new HashMap<>();
    map.put("from", fromEpoch == null ? Instant.EPOCH.getEpochSecond() * 1000 : fromEpoch);
    map.put("to", toEpoch == null ? Instant.now().getEpochSecond() * 1000 : toEpoch);
    return map;
  }
}
