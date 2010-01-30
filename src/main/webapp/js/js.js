$(document).ready(function() {
  $(".previous").css("opacity", "0");
  $(".next").css("opacity", "0");

  var url = document.location.toString();
  if (url.match("#")) {
    var urlArray = url.split("#");
    var user = getUser(urlArray[0]);
    var anchor = urlArray[1];
    updatePage(user + '/now', anchor);
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
      updatePage(user + '/now', dateAsUrl);
    }
  }
});

function updatePage(urlAppend, from) {
  $.getJSON('/' + urlAppend + '?from=' + from, function(data) {
    var user = getUser(document.location.toString().split("#")[0]);
    var host = window.location.host
    var protocol = window.location.protocol

    if (data.statuses) {
      $.each(data.statuses, function(i, status) {
        eventhorizonDates[status.domain] = urlDate(new Date(status.created));
        $("#" + status.domain + " .status").html(status.status).removeClass('yonks month week today').addClass(
            status.period);
        $("#" + status.domain + " .previous a").attr("href",
            urlBase(protocol, host, user) + "/" + eventhorizonDates[status.domain] + "/" + status.domain
                + "/previous");
        $("#" + status.domain + " .next a").attr("href",
            urlBase(protocol, host, user) + "/" + eventhorizonDates[status.domain] + "/" + status.domain
                + "/next");
      });
      eventhorizonFromDate = new Date(data.from);
      $(".title_date").html(titleDate(eventhorizonFromDate));
      $(".title_time").html(titleTime(eventhorizonFromDate));

      var currentUrl = urlBase(protocol, host, user) + "/#/" + urlDate(eventhorizonFromDate);
      window.location.replace(currentUrl);
    }
  });
}

function urlBase(protocol, host, user) {
  return protocol + "//" + host + "/" + user;
}

function urlDate(date) {
  return formatDate(date, "yyyy/MM/dd/HH/mm/ss");
}

function titleTime(date) {
  return formatDate(date, "H:m:s");
}

function titleDate(date) {
  return formatDate(date, 'NNN d, yyyy');
}

function getUser(url) {
  var urlArray = url.split('/').clean('');
  return urlArray[urlArray.length - 1];
}

Array.prototype.clean = function(deleteValue) {
  for ( var i = 0; i < this.length; i++) {
    if (this[i] == deleteValue) {
      this.splice(i, 1);
      i--;
    }
  }
  return this;
};