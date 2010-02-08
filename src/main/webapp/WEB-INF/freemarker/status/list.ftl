<@layout.layout "Your Events">
<ul>
  <script type="text/javascript">
  $(function() {
    $(".datepicker").datepicker({
      inline: true,
      changeMonth: true,
      changeYear: true,
      dateFormat: 'yy/mm/dd',
      yearRange: '1950:2010'
    });
  });
  </script>
  <li>
    <form action="/status" method="post">
      <label for="birthTitle">Where were you born?</label>
      <@spring.formInput "status_birth.title", "id='birthTitle'" />
      <label for="birthCreated">And when was that?</label>
      <input type="text" id="birthCreated" name="created" value="${status_birth.created?string("yyyy/MM/dd")}" class="datepicker" />
      <@spring.formHiddenInput "status_birth.domain", "id='birth'" />
      <input type="submit" name="submit" value="Submit" />
    </form>
  </li>
  
  <li>
    <form action="/status" method="post">
      <label for="livesTitle">Where do you live now?</label>
      <@spring.formInput "status_lives.title", "id='livesTitle'" />
      <label for="livesCreated">And when did you move there?</label>
      <input type="text" id="livesCreated" name="created" value="${status_lives.created?string("yyyy/MM/dd")}" class="datepicker" />
      <@spring.formHiddenInput "status_lives.domain", "id='lives'" />
      <input type="submit" name="submit" value="Submit" />
    </form>
  </li>
</ul>
</@layout.layout>