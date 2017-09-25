package org.asgs.twitterfeeds;

/** Code adapted from https://github.com/twitter/hbc/tree/master/hbc-example. */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.asgs.twitterfeeds.common.clients.KafkaClient;
import org.asgs.twitterfeeds.common.clients.MongoDbClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class FeedReader {

  public static void main(String[] args) throws InterruptedException, IOException {
    if (args.length != 4) {
      System.err.println(
          "Incorrect Usage: Pass the Consumer Key, Consumer Secret, Access Token, and Access Token Secret as arguments.");
      return;
    }

    FeedReader reader = new FeedReader();
    reader.readTweets(args[0], args[1], args[2], args[3]);
  }

  public void readTweets(String consumerKey, String consumerSecret, String token, String secret)
      throws InterruptedException, IOException {
    // Create an appropriately sized blocking queue
    BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

    // Define our endpoint: By default, delimited=length is set (we need this for our processor)
    // and stall warnings are on.
    StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
    endpoint.stallWarnings(false);

    Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);

    // Create a new BasicClient. By default gzip is enabled.
    BasicClient client =
        new ClientBuilder()
            .name("sampleExampleClient")
            .hosts(Constants.STREAM_HOST)
            .endpoint(endpoint)
            .authentication(auth)
            .processor(new StringDelimitedProcessor(queue))
            .build();

    // Establish a connection
    client.connect();

    KafkaClient<String, String> kafkaClient = new KafkaClient<>(getKafkaClusterProps());
    MongoDbClient mongoDbClient = buildMongoClient();

    ObjectMapper mapper = new ObjectMapper();

    for (int msgRead = 0; msgRead < 1000; msgRead++) {
      if (client.isDone()) {
        System.err.println(
            "Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
        break;
      }

      String msg = queue.poll(5, TimeUnit.SECONDS);
      if (msg == null) {
        System.err.println("Did not receive a message in 5 seconds");
      } else {
        System.out.println("Twitter feed received - " + msg);
        Map<?, ?> tree = mapper.readValue(msg, Map.class);
        kafkaClient.publish((String) tree.get("id_str"), msg);
      }
    }

    client.stop();

    System.out.println(
        "Successfully read %d messages!" + client.getStatsTracker().getNumMessages());
  }

  private Properties getKafkaClusterProps() {
    Properties properties = new Properties();
    properties.put("bootstrap.servers", "localhost:9092");
    properties.put("acks", "all");
    properties.put("retries", 0);
    properties.put("batch.size", 16384);
    properties.put("linger.ms", 1);
    properties.put("buffer.memory", 33554432);
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put(
        "value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put("producer-topic", "tweet-stream");
    return properties;
  }

  private MongoDbClient buildMongoClient() throws IOException {
    Properties mongoDbProperties = new Properties();
    mongoDbProperties.load(this.getClass().getResourceAsStream("/mongodb.properties"));
    ClusterSettings clusterSettings =
        ClusterSettings.builder()
            .hosts(
                Arrays.asList(
                    new ServerAddress(
                        mongoDbProperties.getProperty("mongodb.host"),
                        Integer.parseInt(mongoDbProperties.getProperty("mongodb.port")))))
            .build();
    MongoClientSettings mongoClientSettings =
        MongoClientSettings.builder().clusterSettings(clusterSettings).build();
    return new MongoDbClient(mongoClientSettings);
  }
}
