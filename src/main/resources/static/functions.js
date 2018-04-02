var stompClient = null;
var mustacheTemplate = "-unloaded-";

$(document).ready(function () {
	if (window.location.hash == '#_=_'){
	      history.replaceState 
	          ? history.replaceState(null, null, window.location.href.split('#')[0])
	          : window.location.hash = '';
	  }
    $("#loader").show();
    $("#bdsearch").click(openBD);
    $("#admin").click(openAdmin);
    connect();
    
    $.get('template', function (template) {
        Mustache.parse(template);
        mustacheTemplate = template;
    });


    
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

function connect(){
	$.get('/user', function (data) {
		$('#user').html(data.userAuthentication.details.name)
		$(".unauthenticated").hide()
		$(".authenticated").show()
	});
	
	stompClient = Stomp.over(new SockJS("/twitter"));//endpoint
    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/topic/search", onTweetReceived);
        stompClient.debug = null;
        console.log("Connected and subscribed");
    });
}

var logout = function() {
    $.post("/logout", function() {
      $("#user").html('');
      $(".unauthenticated").show();
      $(".authenticated").hide();
    });
    return true;
  }

function openAdmin() {
    window.open('/admin', '_blank');
}

function openBD() {
    window.open('/bdsearch', '_blank')
}