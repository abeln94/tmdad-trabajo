var mustacheTemplate = "-unloaded-";

$(document).ready(function () {
    $("#loader").show();
    $.get('template/tweets', function (template) {
        Mustache.parse(template);
        mustacheTemplate = template;
    });

    loadSelectbar();
    $("#search").click(doSearch);

});

function loadSelectbar() {
    console.log("queries");
    $.get("database/queries", {})
            .done(function (data) {
                $.each(data, function (i, item) {
                    $('#queries').append($('<option>', {
                        value: item.trim(),
                        text: item.trim()
                    }));
                });
                $("#loader").hide();
            });
}

function doSearch() {
    var query = $("#queries").val();

    $("#resultsBlock").empty();

    if (query == '-') {
        return;
    }

    console.log(query);

    $("#loader").show();
    $.get("database/tweets", {q: query})
            .done(function (data) {
                $("#loader").hide();
                console.log(data);
                var rendered = Mustache.render(mustacheTemplate, data);
                $("#resultsBlock").prepend(rendered);
            });

}