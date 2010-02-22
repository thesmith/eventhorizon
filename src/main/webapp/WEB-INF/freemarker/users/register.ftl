<@layout.layout "Register" "Register <span class='decorator'>or</span> <a href='/users/login'>Login</a>" "${viewer!}">
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
    <input class="submit" type="submit" name="register" value="Register" />
  </div>
</form>
</@layout.layout>