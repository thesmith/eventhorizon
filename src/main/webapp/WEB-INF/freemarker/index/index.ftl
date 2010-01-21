<@layout.layout "Event Horizon | ${personId}">
<p>This was ${personId} at ${from?datetime}</p>
<ul>
  <#list statuses as status>
    <li class="${status.domain}">
      <span class="previous"><a href="/${status.personId}/${status.created?string("yyyy/MM/dd/kk/mm/ss")}/${status.domain}/previous">previous</a></span>
      <span class="status">${status.status}</span>
      <span class="next"><a href="/${status.personId}/${status.created?string("yyyy/MM/dd/kk/mm/ss")}/${status.domain}/next">next</a></span>
    </li>
  </#list>
  <li>And so on..</li>
</ul>
</@layout.layout>

