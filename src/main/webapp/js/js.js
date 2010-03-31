function updatePage(urlAppend, from, direction) {
  $.getJSON('/' + urlAppend + '?from=' + from, function(data) {
    var host = window.location.host
    if (host.match(".eventhorizon.me")) {
      var user = host.replace(".eventhorizon.me", "");
    } else {
      var user = getUser(document.location.toString().split("#")[0]);
    }
    
    var protocol = window.location.protocol
    var first = new Date(data.first);
    var from = new Date(data.from);
    var span = from.getTime() - first.getTime();
    var width = $(window).width();
    var scale = (width-240) / span;

    if (data.statuses) {
      $.each(data.statuses, function(i, status) {
        var created = new Date(status.created);
        var currentDate = eventhorizonDates[status.domain];
        eventhorizonDates[status.domain] = urlDate(created);
        
        var targetStatus = $("#" + status.domain + " .status");
        targetStatus.html(status.status+"<span class='tip'>&nbsp</span>").removeClass('yonks month week today').addClass(status.period);
        var position = ((created.getTime() - first.getTime()) * scale) + 40;
        var targetWidth = targetStatus.width();
        var middle = targetWidth / 2;
        var center = position - middle;
        var outerPoint = position + middle;
        
        var statusShift = 0;
        if (center < 40) {
          statusShift = 40 - center;
          center = center + statusShift;
        } else if (outerPoint > (width-40)) {
          statusShift = 0 - outerPoint - width - 40;
          center = center + statusShift;
        }
        	
        targetStatus.children('.tip').css('left', (middle - statusShift) + 'px');
        targetStatus.css('left', center+'px');
        
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
      $("#dotdot").html("And so on..");
    }
  });
  $("#dotdot").html("<img src='/gfx/ajax-loader.gif' />");
}

function periodSpeed(period) {
  if (period == "today") {
    return "fast";
  } else if (period == "week") {
    return 400;
  } else if (period == "month") {
    return "slow";
  } else {
    return 800;
  }
}

function urlBase(protocol, host, user) {
  if (host.match(".eventhorizon.me")) {
    return protocol + "//" + host;
  } else {
    return protocol + "//" + host + "/" + user;
  }
}

function urlDate(date) {
  return formatDate(date, "yyyy/MM/dd/HH/mm/ss");
}

function titleTime(date) {
  return formatDate(date, "HH:mm:ss");
}

function titleDate(date) {
  return formatDate(date, 'NNN dd, yyyy');
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