$(document).ready(function() {
  $(".previous").css({"opacity": "0"});
  $(".next").css({"opacity": "0"});
  $(".status_holder").hide();

  var host = window.location.host;
  var url = document.location.toString();
  if (url.match("#")) {
    var urlArray = url.split("#");
    if (host.match(".eventhorizon.me")) {
      var user = host.replace(".eventhorizon.me", "");
    } else {
      var user = getUser(urlArray[0]);
    }
    var anchor = urlArray[1];
    updatePage(user + '/now', anchor, 'start');
  } else {
    var pathArray = window.location.pathname.split("/").clean("");
    var protocol = window.location.protocol;
    if (host.match(".eventhorizon.me")) {
      var user = host.replace(".eventhorizon.me", "");
      if (pathArray.length == 6) {
        var currentUrl = urlBase(protocol, host, user) + "/#/" + (pathArray.slice(0, pathArray.length).join("/"));
        window.location.replace(currentUrl);
      } else if (pathArray.length == 0) {
        setPageToNow(protocol, host, user);
      }
    } else {
      var user = pathArray[0];
      if (pathArray.length == 7) {
        var currentUrl = urlBase(protocol, host, user) + "/#/" + (pathArray.slice(1, pathArray.length).join("/"));
        window.location.replace(currentUrl);
      } else if (pathArray.length == 1) {
        setPageToNow(protocol, host, user);
      }
    }
  }
});

function setPageToNow(protocol, host, user) {
  var dateAsUrl = urlDate(new Date());
  var currentUrl = urlBase(protocol, host, user) + "/#/" + dateAsUrl;
  window.location.replace(currentUrl);
  updatePage(user + '/now', dateAsUrl, 'start');
}

// Disabling for now..
//$(document).ready(function() {
//  $('#primary').append('<div id="svg"></div><div id="slider"></div>');
//  $('#svg').svg({onLoad: draw});
//  $("#slider").slider({max: 637, min: 0, step: 1, 
//    change: function(event, ui) {
//    var svg = $('#svg').svg('get');
//    svg.clear();
//    draw(svg);
//  }});
//  $('#svg').css({'position': 'absolute', 'top': '0px'});
//});

function draw(svg) {
  var title = $('#title_username');
  var pointX = $('#slider').slider('option', 'value');
  var pointY = 11;
  var offset = title.offset();
  var squareX = 0; //offset.left;
  var squareY = 35; //offset.top;
  var squareWidth = title.width();
  var squareHeight = title.height();
  drawHorizon(svg, pointX, pointY, squareX, squareY, squareWidth, squareHeight);
}

function drawHorizon(svg, pointX, pointY, squareX, squareY, squareWidth, squareHeight) {
  var g = svg.group({stroke: '#AAA', 'stroke-width': 1});
  svg.line(g, squareX, squareY, pointX, pointY);
  svg.line(g, squareX+squareWidth, squareY, pointX, pointY);
  svg.line(g, squareX, squareY+squareHeight, pointX, pointY);
  svg.line(g, squareX+squareWidth, squareY+squareHeight, pointX, pointY);
  svg.rect(squareX, squareY, squareWidth, squareHeight, {fill: 'none', stroke: 'black', 'stroke-width': 2});
}