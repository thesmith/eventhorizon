<@layout.layout "Login" "<span class='padded'>Login <span class='decorator'>or</span> <a href='/users/register'>Register</a></span>" "${viewer!}" "" "" "padded">
<div class="centered">
<form action="" method="POST">
  <div class="row">
    <label for="username">Username</label>
    <@spring.formInput "user.username", ""/>
    <@spring.showErrors ",", "error"/>
  </div>

  <div class="row">
    <label for="password">Password</label>
    <@spring.formPasswordInput "user.password", ""/>
    <@spring.showErrors ",", "error"/>
  </div>
  
  <div class="row">
    <input class="submit" type="submit" name="login" value="Login" />
  </div>
</form>
</div>
</@layout.layout>