<@layout.layout "${personId} at ${from?string('kk:mm:ss')} on ${from?string('MMM d, yyyy')}" "<img src='${gravatar}' /> <span id='title_username'>${personId}</span> <span class='decorator'>at</span> <span class='title_time'>${from?string('kk:mm:ss')}</span> <span class='decorator'>on</span> <span class='title_date'>${from?string('MMM d, yyyy')}</span>" "${viewer!}" "${secureHost!}" "${userHost!}">

<#if refresh??>
  <noscript>
    <meta http-equiv="refresh" content="0; URL=/${personId}/${(from)?string("yyyy/MM/dd/HH/mm/ss")}/" />
  </noscript>
</#if>

<div id="content">
<script type="text/javascript">
  eventhorizonFromDate = new Date('${from?datetime}');
  eventhorizonDates = new Object();
</script>
<script type="text/javascript" src="/js/index.js"></script>
<ul>
  <#list statuses as status>
    <script type="text/javascript">
      $(document).ready(function() {
        $("#${status.domain}").hover(
          function () {
            if ($("#${status.domain} .status").html()) {
              $("#${status.domain} .previous").css("opacity", "0.9");
              $("#${status.domain} .next").css("opacity", "0.9");
            }
          },
          function () {
            if ($("#${status.domain} .status").html()) {
              $("#${status.domain} .previous").css("opacity", "0");
              $("#${status.domain} .next").css("opacity", "0");
            }
          }
        );
        
        eventhorizonDates['${status.domain}'] = '${(status.created)?string("yyyy/MM/dd/HH/mm/ss")}';
        $("#${status.domain} .previous a").click(function(){ 
          updatePage("${status.personId}/${status.domain}/previous.json", eventhorizonDates['${status.domain}'], 'previous');
          return false;
        });
        $("#${status.domain} .next a").click(function(){ 
          updatePage("${status.personId}/${status.domain}/next.json", eventhorizonDates['${status.domain}'], 'next');
          return false;
        });
      });
    </script>
    <li id="${status.domain}">
      <div class="previous"><a href="/${status.personId}/${(status.created)?string("yyyy/MM/dd/HH/mm/ss")}/${status.domain}/previous" class="image"><img src="/gfx/previous.png" title="previous" /></a></div>
      <div class="status_holder"><span class="status ${status.domain} ${status.period}">${status.status}</span></div>
      <div class="next"><a href="/${status.personId}/${(status.created)?string("yyyy/MM/dd/HH/mm/ss")}/${status.domain}/next" class="image"><img src="/gfx/next.png" title="next" /></a></div>
    </li>
  </#list>
  <li id="dotdot">And so on...</li>
</ul>
<div id="horizon">
  <div id="blank-horizon" />
</div>
</div>
</@layout.layout>

