var stompClient = null;

$(document).ready(function () {

    $("#settings").hide();
    $("#loader").show();

    $("#apply").click(applySettings);
    connect();
    
    
});

function connect(){
	$.get('/user', function (data) {
		console.log(data);
		if(data !="no access"){
			$('#user').html(data.userAuthentication.details.name)
			$(".unauthenticated").hide()
			$(".authenticated").show()
			stompClient = Stomp.over(new SockJS("/twitter"));//endpoint
		    stompClient.connect({}, function (frame) {
		        stompClient.debug = null;
		        console.log("Connected");
		        $("#settings").show();
		        $("#loader").hide();
		    });
		}else{
			$(".unauthenticated").hide()
			$(".denied").show()
		}
		
	});
}

function applySettings() {

    var query = $("#query").val();
    var processor = $("#processor").val();
    var level = $("#level").val();

    var values = {query: query, processor: processor, level: level};

    stompClient.send("/app/settings", values, "1234");
    console.log(JSON.stringify(values));
}
