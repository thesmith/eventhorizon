<#import "/spring.ftl" as spring />
<h1>${personId} - ${from?datetime}</h1>

<ul>
  <#list statuses as status>
    <li>${status.status}</li>
  </#list>
</ul>

