<@layout.layout "Event Horizon | ${personId} | ${from?datetime}">
<ul>
  <#list statuses as status>
    <li>
      <div class="previous"><a href="/${status.personId}/${status.created?string("yyyy/MM/dd/kk/mm/ss")}/${status.domain}/previous" class="image"><img src="/gfx/previous.png" title="previous" /></a></div>
      <div class="status ${status.domain}">${status.status}</div>
      <div class="next"><a href="/${status.personId}/${status.created?string("yyyy/MM/dd/kk/mm/ss")}/${status.domain}/next" class="image"><img src="/gfx/next.png" title="next" /></a></div>
    </li>
  </#list>
  <li>And so on..</li>
</ul>
</@layout.layout>

