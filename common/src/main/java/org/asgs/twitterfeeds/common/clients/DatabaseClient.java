package org.asgs.twitterfeeds.common.clients;

import javax.sql.DataSource;
import java.sql.Connection;

import org.asgs.twitterfeeds.common.model.TwitterFeed;
import org.asgs.twitterfeeds.common.model.TwitterUser;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.StringMapper;
/**
 * A Database client to query and manipulate the Tweets on the Database.
 */
public class DatabaseClient {

  private DataSource dataSource;
  private DBI dbi;

  public DatabaseClient(DataSource dataSource) {
    this.dataSource = dataSource;
    this.dbi = new DBI(dataSource);
  }

  public void saveTweet(TwitterFeed tweet) {
    Handle h = dbi.open();
    // create insert query and execute it.
    TwitterUser user = tweet.getUser();
    // Check if user exists in DB, else insert a user first.
    String id = h.createQuery("select id from tweeter where id = :id")
    .bind("id", user.getId())
    .map(StringMapper.FIRST)
    .first();
    if (id == null) { // User is not added to the DB yet.
      h.execute("insert into tweeter values (?, ?, ?, ?, ?)", user.getId(), user.getLocation(), user.getFollowersCount(), user.getFriendsCount(), user.getStatusesCount());
    }
    // Now insert the tweet coupling it to the said user.
    h.execute("insert into tweet values (?, ?, ?, ?, ?)", tweet.getTweetId(), tweet.getTweet(), tweet.getTweetLanguage(), tweet.getTimestamp(), user.getId());
    h.close();
  }

}
