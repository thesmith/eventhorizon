function updatePage(urlAppend, from, direction) {
  $.getJSON('/' + urlAppend + '?from=' + from, function(data) {
    var host = window.location.host
    if (host.match(".eventhorizon.me")) {
      var user = host.replace(".eventhorizon.me", "");
    } else {
      var user = getUser(document.location.toString().split("#")[0]);
    }
    
    $(".qtip").remove();
    
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
        $("#" + status.domain + " .status_holder").hide();
        $("#" + status.domain + " .status").html(status.status).removeClass('yonks month week today').addClass(status.period);
        
        var position = ((created.getTime() - first.getTime()) * scale) + 40;
        var tooltip = 'bottomLeft';
        if (position > 260) {
          tooltip = 'bottomMiddle';
        }
        
        var style = 'lightgrey';
        if (from.getTime() == created.getTime()) {
          style = 'darkgrey';
        }
        
        $("#" + status.domain).qtip({
          content: $("#" + status.domain + " .status_holder").html(),
          position: {
             corner: {
                tooltip: tooltip,
                target: 'bottomLeft'
             },
             adjust: {x: position}
          },
          show: {
             when: false, // Don't specify a show event
             ready: true // Show the tooltip when ready
          },
          hide: false, // Don't specify a hide event
          style: {
             border: {
               width: 2,
               radius: 2,
               color: style
             },
             padding: 1, 
             width: { max: 650 },
             textAlign: 'center',
             tip: true,
             classes: {
               content: 'status'
             },
             name: 'light'
          }
        });
        
        var api = $("#" + status.domain).qtip("api");
        var dimensions = api.getDimensions();
        var wrapper = api.elements.wrapper;
        var wrapperWidth = dimensions.width;
        var outerPoint = position + (wrapperWidth / 2);
        if (outerPoint > (width-40)) {
          wrapper.css('left', (0-(outerPoint-width+40)));
        }
        
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
    
    $.each(data.emptyDomains, function(i, domain) {
      $('#' + domain + " .status").html('');
    });
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