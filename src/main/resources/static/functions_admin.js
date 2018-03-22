var stompClient = null;

$(document).ready(function() {
    
    
    $("#search").submit(streamOnClick);
    
    stompClient = Stomp.over(new SockJS("/twitter"));//endpoint
    stompClient.connect({}, function(frame) {
        stompClient.debug = null;
        console.log("Connected");
        $("#navBar").show();
        $("#loader").hide();
    });
});



function streamOnClick(event){
    event.preventDefault();
    //$("#resultsBlock").empty();
    
    var query = $("#q").val();
    var processor = $("#qP").val();
    
    stompClient.send("/app/query",{},query);
    stompClient.send("/app/processor",{},processor);
    console.log("changed query to >"+query+"< and processor "+processor);
}
