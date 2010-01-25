<@layout.layout "${personId} at ${from?string('kk:mm:ss')} on ${from?string('MMM d, yyyy')}" "${personId} <span class='decorator'>at</span> ${from?string('kk:mm:ss')} <span class='decorator'>on</span> ${from?string('MMM d, yyyy')}">
<ul>
  <#list statuses as status>
    <script type="text/javascript">
      $(document).ready(function() {
        $("#${status.domain}").hover(
          function () {
            $("#${status.domain} > div.previous").css("opacity", "0.9");
            $("#${status.domain} > div.next").css("opacity", "0.9");
          },
          function () {
            $("#${status.domain} > div.previous").css("opacity", "0");
            $("#${status.domain} > div.next").css("opacity", "0");
          }
        );
      });
    </script>
    <li id="${status.domain}">
      <div class="previous"><a href="/${status.personId}/${status.created?string("yyyy/MM/dd/kk/mm/ss")}/${status.domain}/previous" class="image"><img src="/gfx/previous.png" title="previous" /></a></div>
      <div class="status ${status.domain} ${status.period}">${status.status}</div>
      <div class="next"><a href="/${status.personId}/${status.created?string("yyyy/MM/dd/kk/mm/ss")}/${status.domain}/next" class="image"><img src="/gfx/next.png" title="next" /></a></div>
    </li>
  </#list>
  <li>And so on..</li>
</ul>
</@layout.layout>

