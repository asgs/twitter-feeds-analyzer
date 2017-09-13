package org.asgs.twitterfeeds.resources;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
    //databaseClient.
    return null;// totalTweets(100).totalTweeters(50).build();
  }

}
