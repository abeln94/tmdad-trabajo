var mustacheTemplate = "-unloaded-";

$(document).ready(function () {
    $("#loader").hide();
    $.get('template/tweets', function (template) {
        Mustache.parse(template);
        mustacheTemplate = template;
    });

    $("#search").click(doSearch);

});

function doSearch() {

    $("#resultsBlock").empty();

    $("#loader").show();
    $.get("database/tweets", {})
            .done(function (data) {
                $("#loader").hide();
                console.log(data);
                var rendered = Mustache.render(mustacheTemplate, data);
                $("#resultsBlock").prepend(rendered);
            });

}