<@layout.layout "Event Horizon | ${personId}">
<p>This was ${personId} at ${from?datetime}</p>
<ul>
  <#list statuses as status>
    <li class="${status.domain}">${status.status}</li>
  </#list>
  <li>And so on..</li>
</ul>
</@layout.layout>

