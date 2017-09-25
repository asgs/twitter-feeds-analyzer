package org.asgs.twitterfeeds.common.clients;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import org.bson.Document;


public class MongoDbClient {

  private MongoCollection collection;
  private MongoClient client;
  private SingleResultCallback<Void> callback =
      (result, throwable) -> {
        if (throwable != null) {
          System.err.println("Error inserting tweet. " + throwable.getMessage());
        } else {
          System.out.println("Successfully inserted json document.");
        }
      };

  public MongoDbClient(MongoClientSettings settings) {
    client = MongoClients.create(settings);
    collection = client.getDatabase("test").getCollection("tweets");
  }

  public void insertJson(String json) {
    collection.insertOne(Document.parse(json), callback);
  }

  public void close() {
    client.close();
    System.out.println("MongoDb client closed.");
  }
}
