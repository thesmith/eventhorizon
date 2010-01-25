<@layout.layout "${personId} at ${from?string('kk:mm:ss')} on ${from?string('MMM d, yyyy')}" "${personId} <span class='decorator'>at</span> <span class='title_time'>${from?string('kk:mm:ss')}</span> <span class='decorator'>on</span> <span class='title_date'>${from?string('MMM d, yyyy')}</span>">

<script type="text/javascript">
  eventhorizonFromDate = new Date('${from?datetime}');
</script>

<ul>
  <#list statuses as status>
    <script type="text/javascript">
      $(document).ready(function() {
        $("#${status.domain}").hover(
          function () {
            $("#${status.domain} .previous").css("opacity", "0.9");
            $("#${status.domain} .next").css("opacity", "0.9");
          },
          function () {
            $("#${status.domain} .previous").css("opacity", "0");
            $("#${status.domain} .next").css("opacity", "0");
          }
        );
        
        $("#${status.domain} .previous a").click(function() {
          $.getJSON('/${status.personId}/${status.domain}/previous.json?from='+urlDate(eventhorizonFromDate),
          function(data) {
            $.each(data.statuses, function(i, status) {
              $("#${status.domain} .status").html(status.status).
                  removeClass('yonks month week today').addClass('${status.period}');
            });
            eventhorizonFromDate = new Date(data.from);
            $(".title_date").html(titleDate(eventhorizonFromDate));
            $(".title_time").html(titleTime(eventhorizonFromDate));
          });
          return false;
        });
      });
    </script>
    <li id="${status.domain}">
      <div class="previous"><a href="/${status.personId}/${status.created?string("yyyy/MM/dd/kk/mm/ss")}/${status.domain}/previous" class="image"><img src="/gfx/previous.png" title="previous" /></a></div>
      <div class="status ${status.domain} ${status.period}">${status.status}</div>
      <div class="next"><a href="/${status.personId}/${status.created?string("yyyy/MM/dd/kk/mm/ss")}/${status.domain}/next" class="image"><img src="/gfx/next.png" title="next" /></a></div>
    </li>
  </#list>
  <li>And so on..</li>
</ul>
</@layout.layout>

