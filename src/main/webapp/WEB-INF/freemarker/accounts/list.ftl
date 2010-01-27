<@layout.layout "Your Accounts">
<ul>
  <#list domains as domain>
    <li>
      <form action="/accounts" method="post">
        <label for="${domain}UserId"><div class="${domain}_title account_title">${domain}</div></label>
        <div class="account_input">
          <@spring.formInput "account_${domain}.userId", "id='${domain}UserId'" />
          <@spring.formHiddenInput "account_${domain}.domain", "id='${domain}Domain'" />
          <input type="submit" name="submit" value="Submit" />
        </div>
      </form>
    </li>
  </#list>
</ul>
</@layout.layout>