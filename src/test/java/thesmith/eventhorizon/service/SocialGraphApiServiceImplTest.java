package thesmith.eventhorizon.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.service.AccountService.DOMAIN;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class SocialGraphApiServiceImplTest {// extends DataStoreBaseTest {
  @Autowired
  private SocialGraphApiService service;
  
  @Ignore
  public void shouldGetAccounts() throws Exception {
    List<String> urls = Lists.newArrayList("http://twitter.com/thesmith");
    List<Account> accounts = service.getAccounts("thesmith", urls);
    
    assertNotNull(accounts);
    assertTrue(accounts.size() > 1);
    
    boolean flickr = false;
    for (Account account : accounts) {
      assertEquals("thesmith", account.getUserId());
      if (DOMAIN.flickr.toString().equals(account.getDomain()))
        flickr = true;
    }
    assertTrue(flickr);
  }
  
  @Test
  public void shouldMatchFlickr() throws Exception {
    String url = "http://www.flickr.com/photos/thesmith/";
    Pattern p = AccountService.DOMAIN_MATCHERS.get("flickr");
    Matcher m = p.matcher(url);
    if (m.find()) {
      String userId = m.group(1);
      assertEquals("thesmith", userId);
    }
  }
  
  @Test
  public void shouldReplaceLink() throws Exception {
    String tweet = "Panic Status Board: http://bit.ly/aTs0uZ";
    Pattern p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    Matcher m = p.matcher(tweet);
    assertTrue(m.find());
    String url = m.group();
    int start = tweet.indexOf(url);
    String replacement = String.format("<a href='%s'>%s</a>", url, url);
    String beginning = tweet.substring(0, start);
    String end = tweet.substring(start+url.length());
    tweet = beginning+replacement+end;
    
    System.err.println(tweet);
    assertTrue(tweet.contains("<a href"));
  }
}
