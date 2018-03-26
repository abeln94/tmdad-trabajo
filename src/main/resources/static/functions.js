var stompClient = null;
var mustacheTemplate = "-unloaded-";

$(document).ready(function() {
    
    $("#loader").show();
    
    $("#admin").click(openAdmin);
    
    $.get('template', function(template) {
        Mustache.parse(template);
        mustacheTemplate = template;
    });
    
    
    stompClient = Stomp.over(new SockJS("/twitter"));//endpoint
    stompClient.connect({}, function(frame) {
        stompClient.subscribe("/topic/search", onTweetReceived);
        stompClient.debug = null;
        console.log("Connected and subscribed");
    });
});

function onTweetReceived(tweet){
    console.log("received tweet");
    var rendered = Mustache.render(mustacheTemplate, JSON.parse(tweet.body));
    
    $("#loader").hide();
    $("#resultsBlock").prepend(rendered);
    if($("#resultsBlock").get(0).childElementCount > 10){
        $("#resultsBlock").children().last().remove();
    }
}

function openAdmin(){
    window.open('/admin','_blank');
}