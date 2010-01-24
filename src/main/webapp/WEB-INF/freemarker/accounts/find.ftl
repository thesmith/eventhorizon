<@layout.layout "${domain}">
<form action="" method="POST">
      <p>UserId:
      <@spring.formInput "account.userId", ""/>
      </p>
      <input type="submit" name="submit" value="Submit" />
</form>
</@layout.layout>