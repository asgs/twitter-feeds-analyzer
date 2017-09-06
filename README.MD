# Purpose
The intent of this project is to read real-time Twitter feeds via their Streaming API, process them and display some (interesting) stats around them.
This is only to understand some techniques related to data processing, especially when they are live.

<b>Components</b>
<ul>
<li>

<i>Feed Reader</i> - This connects to the Twitter API and reads the feeds based on some criteria (like hashtags, people's names, etc., which don't really matter). Those feeds will be put on a message queuing system like Kafka and persisted into a Database (like PostGresql).

</li>

<li>

<i>Feed Stats</i> - This will process the feeds from the Database and generate the stats; which will ultimately be fed to the front-end

</li>

<b>Status</b>
This is a work-in-progress. The components mentioned above may not be retained (or more components could be added as well). 

