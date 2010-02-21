<@layout.layout "Login" "Login &raquo; <a href='/users/register'>Register</a>" "${viewer!}">
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
</@layout.layout>