<@layout.layout "Your Events" "Your Events" "${viewer!}">
<script type="text/javascript">
$(function() {
  $(".datepicker").datepicker({
    changeMonth: true,
    changeYear: true,
    dateFormat: "yy/mm/dd",
    yearRange: '1950:2010'
  });
});
</script>
<div class="form">
  <form action="/status" method="post">
    <div class="row">
      <label for="birthTitle" class="long">Where were you born?</label>
      <@spring.formInput "status_birth.title", "id='birthTitle'" />
    </div>
    
    <div class="row">
      <label for="birthCreated" class="long">And when was that?</label>
      <input type="text" id="birthCreated" name="created_at" value="${status_birth.created?string("yyyy/MM/dd")}" class="datepicker" />
    </div>
    
    <div class="row">
      <@spring.formHiddenInput "status_birth.domain", "id='birth'" />
      <input class="submit_long" type="submit" name="submit" value="Submit" />
    </div>
  </form>
  </div>
  <div class="form">
  <form action="/status" method="post">
    <div class="row">
      <label for="livesTitle" class="long">Where do you live now?</label>
      <@spring.formInput "status_lives.title", "id='livesTitle'" />
    </div>
    
    <div class="row">
      <label for="livesCreated" class="long">And when did you move there?</label>
      <input type="text" id="livesCreated" name="created_at" value="${status_lives.created?string("yyyy/MM/dd")}" class="datepicker" />
    </div>
    
    <div class="row">
      <@spring.formHiddenInput "status_lives.domain", "id='lives'" />
      <input class="submit_long" type="submit" name="submit" value="Submit" />
    </div>
  </form>
</div>
</@layout.layout>