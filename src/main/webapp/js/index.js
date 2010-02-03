$(document).ready(function() {
  $(".previous").css("opacity", "0");
  $(".next").css("opacity", "0");

  var url = document.location.toString();
  if (url.match("#")) {
    var urlArray = url.split("#");
    var user = getUser(urlArray[0]);
    var anchor = urlArray[1];
    updatePage(user + '/now', anchor, 'start');
  } else {
    var pathArray = window.location.pathname.split("/").clean("");
    var host = window.location.host;
    var protocol = window.location.protocol;
    var user = pathArray[0];

    if (pathArray.length == 7) {
      var currentUrl = urlBase(protocol, host, user) + "/#/" + (pathArray.slice(1, pathArray.length).join("/"));
      window.location.replace(currentUrl);
    } else if (pathArray.length == 1) {
      var dateAsUrl = urlDate(new Date());
      var currentUrl = urlBase(protocol, host, user) + "/#/" + dateAsUrl;
      window.location.replace(currentUrl);
      updatePage(user + '/now', dateAsUrl, 'start');
    }
  }
});