<#import "/spring.ftl" as spring />
<ul>
  <#list accounts as account>
    <li><a href="/accounts/${account.personId}/${account.domain}/">${account.domain} - ${account.userId}</a></li>
  </#list>
</ul>