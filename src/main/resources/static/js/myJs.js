$("tr.clickable-row").click(function () {
    window.location = $(this).data("url");
});

function openDetail(id) {
    window.open("http://localhost:8080/api/v1/tasks/" + id, "_self");
}