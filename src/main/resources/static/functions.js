var stompClient = null;
var mustacheTemplate = "-unloaded-";

$(document).ready(function () {
    if (window.location.hash == '#_=_') {
        history.replaceState
                ? history.replaceState(null, null, window.location.href.split('#')[0])
                : window.location.hash = '';
    }
    $("#loader").show();
    console.log("Conectando stomp");
    stompClient = Stomp.over(new SockJS("/twitter"));//endpoint
    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/topic/search", onTweetReceived);
        stompClient.debug = null;
        console.log("Connected and subscribed");
    });

    $.get('template/tweet', function (template) {
        Mustache.parse(template);
        mustacheTemplate = template;
    });


    console.log("Conectado");
});

function onTweetReceived(tweet) {
    console.log("received tweet");
    var rendered = Mustache.render(mustacheTemplate, JSON.parse(tweet.body));

    $("#loader").hide();
    $("#resultsBlock").prepend(rendered);
    if ($("#resultsBlock").get(0).childElementCount > 10) {
        $("#resultsBlock").children().last().remove();
    }
}