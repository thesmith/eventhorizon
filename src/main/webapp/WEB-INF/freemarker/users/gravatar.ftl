<@layout.layout "Gravatar" "<img src='${gravatar}' /> Gravatar" "${viewer!}" "" "${userHost}">
<div class="centered">
<form action="" method="POST">
  <label for="email" style="padding-bottom: 1em; padding-top: 20px; font-size: 1.6em; width: 8em;">Gravatar Email</label>
  <div class="account_input">
    <input name="email" type="text" value="" />
    <input class="submit submit_inline" type="submit" name="submit" value="Submit" />
  </div>
</form>
</div>
<div id="foot">
  <p>Why not go <a href="${userHost}">back home</a> or <a href="/accounts">edit your dashboard</a></p>
</div>
</@layout.layout>