var stompClient = null;

$(document).ready(function () {

    $("#settings").hide();
    $("#loader").show();

    $("#apply").click(applySettings);

    stompClient = Stomp.over(new SockJS("/twitter"));//endpoint
    stompClient.connect({}, function (frame) {
        stompClient.debug = null;
        console.log("Connected");
        $("#settings").show();
        $("#loader").hide();
    });
});



function applySettings() {

    var query = $("#query").val();
    var processor = $("#processor").val();
    var level = $("#level").val();

    var values = {query: query, processor: processor, level: level};

    stompClient.send("/app/settings", values, "configure");
    console.log(JSON.stringify(values));
}
