// Global state :-/
var eventSource;
var previousStats;

var baseUrl = "http://localhost:8080/tw-stats/stream";

function getEvents() {
    eventSource = new EventSource(constructUrl(), { withCredentials: false});

    handleServerEvents(eventSource);
    handleEventError(eventSource);

    setOnConnectVisibility();

    var closeButton = document.getElementById('closeConnection');
    attachCloseConnectionButtonHandler(closeButton);
}

function handleServerEvents(eventSource) {
    eventSource.onmessage = function (e) {
        var currentStats = e.data;
        //console.log("message from server - " + currentStats);
        var obj = JSON.parse(currentStats);
        if (previousStats == null || currentStats != previousStats) {
            updateDom(obj);
            previousStats = currentStats;
        } else {
            console.log("No updates to show.");
        }
    }
}

function handleEventError(eventSource) {
    eventSource.onerror = function (e) {
        // this is invoked no matter what :-/ and there's not enough info to understand the actual error.
        console.log("Error from server - " + e.type + " " + e.target + " " + e.detail + " " + e.view);
    }
}

function attachCloseConnectionButtonHandler(closeConnection) {
    closeConnection.onclick = function() {
        console.log("Terminating connection to the server.");
        setOnDisconnectVisibility();
        eventSource.close();
    }
}

function updateDom(obj)
{
    document.getElementById('totalTweets').textContent = obj.totalTweets;
    document.getElementById('totalTweeters').textContent = obj.totalTweeters;

    createAndUpdateListItemsFor('topTenFollowerCounts', obj.topTenFollowerCounts);
    createAndUpdateListItemsFor('topTenStatusCounts', obj.topTenStatusCounts);
    createAndUpdateListItemsFor('topTenLanguages', obj.topTenLanguages);
    createAndUpdateListItemsFor('topTenLocations', obj.topTenLocations);
}

function createAndUpdateListItemsFor(divElementId, listValues) {
    var mainDiv = document.getElementById(divElementId);
    mainDiv.innerHTML = ""; // clear the existing stats, if any, before updating.
    var ul = document.createElement('ul');
    mainDiv.appendChild(ul);
    for (var count of listValues)
    {
        var countLi = document.createElement('li');
        countLi.textContent = count == null ? "unknown" : count;
        ul.appendChild(countLi);
    }
}

function constructUrl() {
    var fromDate = document.getElementById('fromDate').value;
    var toDate = document.getElementById('toDate').value;

    var fullUrl = baseUrl;

    if (fromDate.trim().length > 0) {
        fullUrl += "?from=" + Date.parse(fromDate); // extract epoch millis.
    }

    if (toDate.trim().length > 0) {
        if (fromDate.trim().length > 0) {
            fullUrl += "&";
        } else {
            fullUrl += "?";
        }
        fullUrl += "to=" + Date.parse(toDate);
    }
    return fullUrl;
}

function setOnConnectVisibility() {
    document.getElementById('globalStatsDiv').style = "visibility:visible";
    document.getElementById('closeConnectionDiv').style = "visibility:visible";
    document.getElementById('getStatsDiv').style = "visibility:hidden";
}

function setOnDisconnectVisibility() {
    document.getElementById('closeConnectionDiv').style = "visibility:hidden";
    document.getElementById('getStatsDiv').style = "visibility:visible";
}