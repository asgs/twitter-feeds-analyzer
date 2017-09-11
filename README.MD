# Purpose
The intent of this project is to read real-time Twitter feeds via their Streaming API, process them and display some (interesting) stats around them.
This is only to understand some techniques related to data processing, especially when they are live.

<b>Components</b>
<ul>
<li>

<i>Feed Reader</i> - This connects to the Twitter API and reads the feeds based on some criteria (like hashtags, people's names, etc., which don't really matter). Those feeds will be put on a Kafka topic. This serves as the "raw" data that will be further processed as necessary.

</li>

<li>

<i>Feed Processor</i> - This will process the feeds from the Kafkat topic and transform into some concrete structure and push to another topic; This could be subscribed by various datastores for persistent storage.

</li>

<li>

<i>Feed Querier</i> - This will most likely be a REST API to serve queries fired from a UI; It will in turn query the database and/or the topic (which contains the processed feeds data) and return results

</li>
</ul>

<b>Status</b>
This is a work-in-progress. The components mentioned above may not be retained (or more components could be added as well). 

