package thesmith.eventhorizon.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.SocialGraphApiService;
import twitter4j.org.json.JSONException;
import twitter4j.org.json.JSONObject;

import com.google.appengine.repackaged.com.google.common.base.StringUtil;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class SocialGraphApiServiceImpl implements SocialGraphApiService {
  private static final String URL = "http://socialgraph.apis.google.com/otherme?q=%s&sgn=1";
  @SuppressWarnings("deprecation")
  private static final List<String> domainSearches = Lists.immutableList(AccountService.DOMAIN_SEARCH.keySet());

  @Autowired
  private AccountService accountService;

  @SuppressWarnings("deprecation")
  public List<Account> getAccounts(String personId, List<String> urls) {
    try {
      URL url = new URL(String.format(URL, StringUtil.join(urls, ",")));
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer json = new StringBuffer();
      String line;
      while ((line = reader.readLine()) != null) {
        json.append(line);
      }
      reader.close();

      return processAccounts(personId, json.toString());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected List<Account> processAccounts(String personId, String json) throws JSONException {
    List<Account> accounts = Lists.newArrayList();
    JSONObject graph = new JSONObject(json);
    String[] names = JSONObject.getNames(graph);
    if (null != names && names.length > 0) {
      for (String name: names) {
        String domain = findDomain(name);
        if (null != domain) {
          Account account = accountService.account(personId, domain);
          Pattern p = AccountService.DOMAIN_MATCHERS.get(domain);
          Matcher m = p.matcher(name);
          if (m.find()) {
            account.setUserId(m.group(1));
            accounts.add(account);
          }
        }
      }
    }
    return accounts;
  }

  protected String findDomain(String domain) {
    for (String search: domainSearches) {
      if (domain.contains(search))
        return AccountService.DOMAIN_SEARCH.get(search);
    }
    return null;
  }
}
