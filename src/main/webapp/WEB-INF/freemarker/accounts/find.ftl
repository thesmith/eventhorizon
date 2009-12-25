<#import "/spring.ftl" as spring />
<form action="" method="POST">
      <p>UserId:
      <@spring.formInput "account.userId", ""/>
      </p>
</form>