<#macro layout title="" heading=title>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <link rel="stylesheet" type="text/css" media="all" href="/css/css.css" /> 
    <link rel="stylesheet" type="text/css" media="all" href="/css/icons.css" />
    <script type="text/javascript" src="/js/jquery-1.4.min.js"></script> 
    <script type="text/javascript" src="/js/js.js"></script> 
    <script type="text/javascript" src="/js/date.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta http-equiv="Content-Language" content="en-GB" />
    <meta name="author" content="Ben Smith" /> 
    <meta name="description" content="Event Horizon" /> 
    <meta name="keywords" content="Ben Smith, Ben, Smith, information, event horizon, event, horizon" /> 
    <link rel="shortcut icon" type="image/x-icon" href="/favicon.ico" /> 
    <link rel="icon" type="image/x-icon" href="/favicon.ico" />
    <title>Event Horizon | ${title}</title>
  </head>
  <body>
    <div id="container"> 
      <div id="primary" class="full"> 
        <div id="mast"> 
          <h1>${heading}</h1> 
        </div>
        <#nested/>
      </div>
    </div>
  </body>
</html>
</#macro>