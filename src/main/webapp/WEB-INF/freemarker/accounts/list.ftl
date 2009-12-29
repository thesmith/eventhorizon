<@layout.layout>
<ul>
  <#list accounts as account>
    <li><a href="/accounts/${account.domain}/">${account.domain}</a></li>
  </#list>
</ul>
</@layout.layout>