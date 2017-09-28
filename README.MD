# Purpose
The intent of this project is to read real-time Twitter feeds via their Streaming API, process them and display some (interesting) stats around them.
This is only to understand some techniques related to data processing, especially when they are live.

<b>Components</b>
<ul>
<li>

<i>Feed Reader</i> - This connects to the Twitter API and reads the feeds based on some criteria (like hashtags, people's names, etc., which don't really matter). Those feeds will be put on a Kafka topic. (It also pushes the raw feeds into a Mongodb store for processing at a later time.) This serves as the "raw" data that will be further processed as necessary.

</li>

<li>

<i>Feed Processor</i> - This will process the feeds from the Kafkat topic and transform into some concrete structure and push to another topic; This could be subscribed by various datastores for persistent storage.

</li>

<li>

<li>

<i>Stream Processor</i> - This will process the feeds as and when they are streamed in by the feedreader. This is just a stream-processor form of the feedprocessor above.

</li>

<li>

<i>Analyzer Webapp</i> - This is mostly a REST app to serve queries fired from a UI; It will in turn query the database and/or the topic (which contains the processed feeds data) and return JSON data.

</li>
</ul>

## Setup

This project currently requires [Apache Kafka](http://kafka.apache.org/quickstart) (for messaging communications), [Apache Zookeeper](https://zookeeper.apache.org) (for distributed co-ordination of services like Kafka). The host, port number, Kafka topic details are *currently* hardcoded in the respective project modules as a Java `Properties` instance. So if you would like to deviate from the defaults, feel free to do so.

Also, a Postgres DB instance is used to persist the processed data that would be queried by the REST API while retrieving the stats. Its details are configured in the `hikari.properties` file inside the `feedprocessor` and `analyzer-webapp` project modules.

It's also assumed you have a Twitter developer account with at least one app registered on their portal. Check out [OAuth Overview](https://dev.twitter.com/oauth/overview/introduction) documentation on usage of Twitter APIs.

## Build and Run

Make sure the Zookeeper and Kafka services are running and their details are configured accordingly.

1. To run the Feed Reader, run the `feedreader.sh` script passing the following four arguments one after the other in that particular order.

* `<consumer_key>`
* `<consumer_secret>`
* `<access_token>`
* `<access_token_secret>`

2. To run the Feed Processor, run the script `feedprocessor.sh`.

3. Feed Processor is now being **Superseded** by the Stream Processor which does a better job at real-time stream processing. It can be run by invoking the script `stream-processor.sh`.

4. To run the REST server, run the script `rest-server.sh` and hit the endpoint `http://localhost:8080/tw-stats` to see some basic common Twitter stats in the form of JSON.

5. To see the Web UI, open the URL `http://localhost:8080/stats`. You can choose to ignore the dates as sensible defaults are chosen (as explained below) and hit the button to see the streaming stats!

<b>Status</b>
-------------

The basic goal of the project which is, to ingest the twitter feeds, process them and generate some random statistics out of it, is achieved. However, there are a few important things I'd like to cover.

1. <del>It's NOT really a stream processor at the moment. The `feedprocessor` module just waits for each and every message that is available in a time window of 5 seconds. So, unless the stream is fully consumed or stops for some reason, the processing doesn't begin. So goal is to use the Kafka Streams library to process it in "real-time".</del> **StreamProcessor** does JUST that. Another option to explore is to use a Reactive programming library to just "react" to the events as and when they occur.

2. <del>The original stream of twitter feeds are pushed on to a Kafka topic, so the moment it's consumed by a subscriber it's gone. To be able to process the original "raw" data at a later point of time, it needs to be persisted in a datastore (preferrably MongoDB as it does support JSON out-of-the-box).</del> **FeedReader** now does JUST that.

3. <del>The stats that are generated are not specific to a time period. So showing stats like who the top Tweeters for the last week are or which language tweeters preferred to tweet the most in 2016 and 2017, etc., are better than showing the same stats without pinning it down to some timeline.</del> The UI now shows two Date controls to adjust the timeline. By default, from date is the standard UNIX epoch, while to date is today.

4. <del>Build a simple frontend to visualize the stats and perhaps stream the stats to reflect real-time data. NOT a priority at the moment, though.</del> Using Jersey's Server-sent Events, the client can now receive a stream of text events.

