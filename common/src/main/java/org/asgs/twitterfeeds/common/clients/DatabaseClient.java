package org.asgs.twitterfeeds.common.clients;

import com.google.common.collect.ImmutableMap;
import org.asgs.twitterfeeds.common.model.TwitterFeed;
import org.asgs.twitterfeeds.common.model.TwitterUser;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.util.IntegerMapper;
import org.skife.jdbi.v2.util.LongMapper;
import org.skife.jdbi.v2.util.StringMapper;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.IntStream;

/**
 * A Database client to query and manipulate the Tweets on the Database using a simple
 * straight-forward Query Mapper JDBI.
 */
public class DatabaseClient {

  private DBI dbi;
  private Map<Class, ResultSetMapper<?>> typeMapper;

  public DatabaseClient(DataSource dataSource) {
    System.out.println("Initializing JDBI with dataSource " + dataSource);
    this.dbi = new DBI(dataSource);
    typeMapper = new HashMap<>();
    typeMapper.put(Integer.class, IntegerMapper.FIRST);
    typeMapper.put(Long.class, LongMapper.FIRST);
    typeMapper.put(String.class, StringMapper.FIRST);
  }

  public void saveTweet(TwitterFeed tweet) {
    TwitterUser user = tweet.getUser();
    // Check if user exists in DB.
    String id =
        query(
                "select id from tweeter where id = :id",
                ImmutableMap.of("id", user.getId()),
                String.class,
                ResultType.FIRST)
            .get(0);

    if (id == null) { // User is not added to the DB yet, so insert first.
      insertRow(
          "insert into tweeter",
          user.getId(),
          user.getLocation(),
          user.getFollowersCount(),
          user.getFriendsCount(),
          user.getStatusesCount());
      System.out.println("Inserted new user with id " + user.getId());
    }
    // Now insert the tweet coupling it to the said user.
    insertRow(
        "insert into tweet",
        tweet.getTweetId(),
        tweet.getTweet(),
        tweet.getTweetLanguage(),
        tweet.getTimestamp(),
        user.getId());
    System.out.println("Inserted new tweet with id " + tweet.getTweetId());
  }

  public void insertRow(String sql, Object... bindingParameters) {
    Handle h = dbi.open();
    // The sql string mustn't contain the values () part. It will be auto-constructed.
    try {
      h.execute(generateValuesClause(sql, bindingParameters.length), bindingParameters);
    } finally {
      h.close();
    }
  }

  public <T> List<T> query(
      String sql, Map<String, Object> bindingMap, Class<T> klass, ResultType type) {
    Handle h = dbi.open();
    Query query = h.createQuery(sql);

    for (Map.Entry<String, Object> bindingEntry : bindingMap.entrySet()) {
      query = (Query) query.bind(bindingEntry.getKey(), bindingEntry.getValue());
    }

    Query<T> typedQuery = query.map(typeMapper.get(klass));
    List<T> results;

    try {
      if (type == ResultType.ALL) {
        results = typedQuery.list();
      } else {
        results = Arrays.asList((T) typedQuery.first());
      }
    } finally {
      h.close();
    }

    return results;
  }

  private String generateValuesClause(String sql, int numberOfParameters) {
    StringBuilder builder = new StringBuilder(sql);
    builder.append(" values ");
    StringJoiner joiner = new StringJoiner(",", "(", ")");
    IntStream.range(0, numberOfParameters).forEach(x -> joiner.add("?"));
    builder.append(joiner.toString());
    return builder.toString();
  }
}
