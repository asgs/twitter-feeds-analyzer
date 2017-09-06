package org.asgs.twitterfeeds;

/**
 * Hello world!
 *
 */
 import com.twitter.hbc.ClientBuilder;
 import com.twitter.hbc.core.Constants;
 import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
 import com.twitter.hbc.core.processor.StringDelimitedProcessor;
 import com.twitter.hbc.httpclient.BasicClient;
 import com.twitter.hbc.httpclient.auth.Authentication;
 import com.twitter.hbc.httpclient.auth.OAuth1;

 import java.util.concurrent.BlockingQueue;
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.TimeUnit;

 import org.apache.http.HttpHeaders;
 import org.apache.http.client.methods.HttpUriRequest;

 public class FeedReader {

   private static class LoggingOAuth1 extends OAuth1 {
     public LoggingOAuth1(String consumerKey, String consumerSecret, String token, String secret) {
       super(consumerKey, consumerSecret, token, secret);
     }
     @Override
     public void signRequest(HttpUriRequest request, String postParams) {
       super.signRequest(request, postParams);
       System.out.println("URL invoked is " + request.getURI());
       System.out.println("Authorization header value is " + request.getFirstHeader(HttpHeaders.AUTHORIZATION));
     }
   }

   public static void run(String consumerKey, String consumerSecret, String token, String secret) throws InterruptedException {
     // Create an appropriately sized blocking queue
     BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

     // Define our endpoint: By default, delimited=length is set (we need this for our processor)
     // and stall warnings are on.
     StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
     endpoint.stallWarnings(false);

     Authentication auth = new LoggingOAuth1(consumerKey, consumerSecret, token, secret);

     // Create a new BasicClient. By default gzip is enabled.
     BasicClient client = new ClientBuilder()
             .name("sampleExampleClient")
             .hosts(Constants.STREAM_HOST)
             .endpoint(endpoint)
             .authentication(auth)
             .processor(new StringDelimitedProcessor(queue))
             .build();

     // Establish a connection
     client.connect();

     // Do whatever needs to be done with messages
     for (int msgRead = 0; msgRead < 1000; msgRead++) {
       if (client.isDone()) {
         System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
         break;
       }

       String msg = queue.poll(5, TimeUnit.SECONDS);
       if (msg == null) {
         System.out.println("Did not receive a message in 5 seconds");
       } else {
         System.out.println("Twitter feed received - " + msg);
       }
     }

     client.stop();

     // Print some stats
     System.out.printf("The client read %d messages!\n", client.getStatsTracker().getNumMessages());
     // Timezone, Tweet Language, followers_count
   }

   public static void main(String[] args) {
     if (args.length != 4) {
       System.err.println("Incorrect Usage: Pass the consumerKey, consumerSecret, token, secret as arguments.");
       return;
     }
     try {
       FeedReader.run(args[0], args[1], args[2], args[3]);
     } catch (InterruptedException e) {
       System.out.println(e);
     }
   }
 }
