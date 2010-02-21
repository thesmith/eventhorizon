<@layout.layout "Your Accounts" "Your Accounts &raquo; <a href='/status'>Events</a>" "${viewer!}">
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
</@layout.layout>