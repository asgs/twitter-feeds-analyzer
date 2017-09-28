function getEvents() {
    var previousStats;

    var eventSource = new EventSource("http://localhost:8080/tw-stats/stream", { withCredentials: false});

    handleServerEvents(eventSource, previousStats);
    handleEventError(eventSource);

    var closeButton = document.getElementById("closeConnection");
    attachCloseConnectionButtonHandler(closeButton);
}

function handleServerEvents(eventSource, previousStats) {
    eventSource.onmessage = function (e) {
        var currentStats = e.data;
        console.log("message from server - " + currentStats);
        var obj = JSON.parse(currentStats);
        if (previousStats == null || currentStats != previousStats) {
            updateDom(obj);
            previousStats = currentStats;
        } else {
            console.log("No new updates to show.");
        }
    }
}

function handleEventError(eventSource) {
    eventSource.onerror = function (e) {
        console.log("Error from server - " + e.type + " " + e.target + " " + e.detail + " " + e.view);
    }
}

function attachCloseConnectionButtonHandler(closeConnection) {
    closeConnection.onclick = function() {
        console.log("Terminating connection to the server.");
        eventSource.close();
    }
}

function updateDom(obj)
{
    document.getElementById("totalTweets").textContent = obj.totalTweets;
    document.getElementById("totalTweeters").textContent = obj.totalTweeters;

    createAndUpdateListItemsFor("topTenFollowerCounts", obj.topTenFollowerCounts);
    createAndUpdateListItemsFor("topTenStatusCounts", obj.topTenStatusCounts);
    createAndUpdateListItemsFor("topTenLanguages", obj.topTenLanguages);
    createAndUpdateListItemsFor("topTenLocations", obj.topTenLocations);
}

function createAndUpdateListItemsFor(divElementId, listValues) {
    var mainDiv = document.getElementById(divElementId);
    mainDiv.innerHTML = "";
    var ul = document.createElement("ul");
    mainDiv.appendChild(ul);
    for (var count of listValues)
    {
        var countLi = document.createElement('li');
        countLi.textContent = count == null ? "unknown": count;
        ul.appendChild(countLi);
    }
}