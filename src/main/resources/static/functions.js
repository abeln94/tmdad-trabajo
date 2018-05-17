var stompClient = null;
var mustacheTemplateTweet = "-unloaded-";
var mustacheTemplateTweets = "-unloaded-";
var connection = null;

$(document).ready(function () {
    if (window.location.hash == '#_=_') {
        history.replaceState
                ? history.replaceState(null, null, window.location.href.split('#')[0])
                : window.location.hash = '';
    }

    $("#connect").html("Connecting...");
    console.log("Conectando stomp");
    stompClient = Stomp.over(new SockJS("/twitter"));//endpoint
    stompClient.connect({}, function (frame) {
        stompClient.debug = null;
        $("#connect").html("Connect");
    });


    $.get('template/tweet', function (template) {
        Mustache.parse(template);
        mustacheTemplateTweet = template;
    });
    $.get('template/tweets', function (template) {
        Mustache.parse(template);
        mustacheTemplateTweets = template;
    });

    $("#search").click(doSearch);
    $("#connect").click(function () {
        (connection == null ? connect : disconnect)();
    });

});

function connect() {
    if (connection == null) {
        $("#loader").show();
        $("#resultsBlock").empty();
        connection = stompClient.subscribe("/topic/search", onTweetReceived);
        console.log("Connected and subscribed");
        $("#connect").html("Disconnect");
    }
}

function disconnect() {
    if (connection != null) {
        $("#loader").hide();
        connection.unsubscribe();
        connection = null;
        $("#connect").html("Connect");
        console.log("Disconnected");
    }
}

function onTweetReceived(tweet) {
    console.log("received tweet");
    var rendered = Mustache.render(mustacheTemplateTweet, JSON.parse(tweet.body));

    $("#loader").hide();
    $("#resultsBlock").prepend(rendered);
    if ($("#resultsBlock").get(0).childElementCount > 10) {
        $("#resultsBlock").children().last().remove();
    }
}

function doSearch() {
    disconnect();

    $("#resultsBlock").empty();

    $("#loader").show();
    $.get("database/tweets", {})
            .done(function (data) {
                $("#loader").hide();
                console.log(data);
                var rendered = Mustache.render(mustacheTemplateTweets, data);
                $("#resultsBlock").prepend(rendered);
            });

}