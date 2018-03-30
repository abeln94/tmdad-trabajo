$(document).ready(function () {
    loadSelectbar();
    $("#search").click(doSearch);

});

function loadSelectbar() {
    console.log("queries");
    $.get("queries", {})
            .done(function (data) {
                val = "<select id='query'>"
                val += "<option value='-'>-</option>"
                for (i = 0; i < data.length; i++) {
                    //console.log(data[i]);
                    val += "<option value=" + data[i] + ">" + data[i] + "</option>";
                }
                val += "</select>";

                $("#select").html(val);

            });
}

function doSearch() {
    var query = $("#query").val();
    console.log(query);
    if (query != '-') {
        $.get("bdtweets", {q: query})
                .done(function (data) {
                    $("#resultsBlock").empty();
                    console.log(data);
                    for (i = 0; i < data.length; i++) {
                        val = '<div> <div class="card p-3"><div class="card-body"><h5 class="card-title">';
                        val += '<a href="https://twitter.com/' + data[i].fromUser + '" target="_blank"><span class="fas fa-user"></span> <span>' + data[i].fromUser + '</span></a>';
                        val += 'dice en <a href="https://twitter.com/' + data[i].fromUser + '/status/' + data[i].id + '"';
                        val += 'target="_blank"><span class="fab fa-twitter-square"></span> <span>' + data[i].id + '</span></a></h5>';
                        val += '<p class="card-text">' + data[i].text + '</p> </div> </div> </div>';
                        $("#resultsBlock").prepend(val);
                    }
                });
    }

}