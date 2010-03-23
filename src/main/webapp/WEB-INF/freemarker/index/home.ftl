<@layout.layout "EventHorizon" "EventHorizon" "${viewer!}" "${secureHost!}" "${userHost!}">

<div id="users">
	<#list userLinks?keys as userLink>
		<a href='${userLink}'><img src='${userLinks[userLink]}' /></a>
	</#list>
</div>

<div id="cta" class="centered">
  <a href="${secureHost}/users/login">Login</a> <span class="decorator">or</span> <a href="${secureHost}/users/register">Register</a>
</div>

</@layout.layout>