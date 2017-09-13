package org.asgs.twitterfeeds.common.clients;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.asgs.twitterfeeds.common.model.TwitterFeed;
import org.asgs.twitterfeeds.common.model.TwitterUser;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.util.IntegerMapper;
import org.skife.jdbi.v2.util.LongMapper;
import org.skife.jdbi.v2.util.StringMapper;
/**
 * A Database client to query and manipulate the Tweets on the Database.
 */
public class DatabaseClient {

  private DataSource dataSource;
  private DBI dbi;
  private Map<Class, ResultSetMapper<?>> typeMapper;
  public DatabaseClient(DataSource dataSource) {
    this.dataSource = dataSource;
    System.out.println("Initializing JDBI with dataSource " + dataSource);
    this.dbi = new DBI(dataSource);
    typeMapper = new HashMap<>();
    typeMapper.put(Integer.class, IntegerMapper.FIRST);
    typeMapper.put(Long.class, LongMapper.FIRST);
    typeMapper.put(String.class, StringMapper.FIRST);
  }

  public void saveTweet(TwitterFeed tweet) {
    System.out.println("Request to Insert new tweet with id " + tweet.getTweetId());
    Handle h = dbi.open();
    TwitterUser user = tweet.getUser();
    // Check if user exists in DB, else insert the user first.
    String id = h.createQuery("select id from tweeter where id = :id")
                 .bind("id", user.getId())
                 .map(StringMapper.FIRST)
                 .first();
    if (id == null) { // User is not added to the DB yet.
      h.execute("insert into tweeter values (?, ?, ?, ?, ?)", user.getId(), user.getLocation(), user.getFollowersCount(), user.getFriendsCount(), user.getStatusesCount());
      System.out.println("Inserted new user with id " + user.getId());
    }
    // Now insert the tweet coupling it to the said user.
    h.execute("insert into tweet values (?, ?, ?, ?, ?)", tweet.getTweetId(), tweet.getTweet(), tweet.getTweetLanguage(), tweet.getTimestamp(), user.getId());
    System.out.println("Inserted new tweet with id " + tweet.getTweetId());
    h.close();
  }

  public <T> List<T> query(String sql, Map<String, Object> bindingMap, Class<T> klass, ResultType type) {
    Handle h = dbi.open();
    Query query = h.createQuery(sql);
    for (Map.Entry<String, Object> bindingEntry : bindingMap.entrySet()) {
      query.bind(bindingEntry.getKey(), bindingEntry.getValue());
    }

    query.map(typeMapper.get(klass));

    if (type == ResultType.ALL) {
      return query.list();
    } else {
      return Arrays.asList((T)query.first());
    }
  }

  public static enum ResultType {
    ALL, // Return all results.
    FIRST // Return the first result.
  }

}
