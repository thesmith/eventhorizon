<@layout.layout "Login">
<form action="" method="POST">
      <p>Username:
      <@spring.formInput "user.username", ""/>
      <@spring.showErrors "<br>"/>
      </p>
      <p>Password:
      <@spring.formPasswordInput "user.password", ""/>
      <@spring.showErrors "<br>"/>
      </p>
      <input type="submit" name="submit" value="Submit" />
</form>
</@layout.layout>