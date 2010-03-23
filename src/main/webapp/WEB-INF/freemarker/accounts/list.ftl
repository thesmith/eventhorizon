<@layout.layout "Your Accounts" "Your Accounts <span class='decorator'>or</span> <a href='/status'>Events</a>" "${viewer!}" "" "${userHost}">
<div class="centered" style="width: 630px;">
<ul>
  <#list domains as domain>
    <li>
      <form action="/accounts" method="post">
        <label class="image" for="${domain}UserId"><div class="${domain}_title account_title">${domain}</div></label>
        <div class="account_input">
          <@spring.formInput "account_${domain}.userId", "id='${domain}UserId'" />
          <@spring.formHiddenInput "account_${domain}.domain", "id='${domain}Domain'" />
          <input class="submit submit_inline" type="submit" name="submit" value="Submit" />
        </div>
      </form>
    </li>
  </#list>
</ul>
</div>
<div id="foot">
  <p><img src="${gravatar}" /> You could also <a href="/users/gravatar">update your Gravatar</a></p>
</div>
</@layout.layout>