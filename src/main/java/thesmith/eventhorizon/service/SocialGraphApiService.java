package thesmith.eventhorizon.service;

import java.util.List;

import thesmith.eventhorizon.model.Account;

/**
 * Interface onto SocialGraphApi
 * http://code.google.com/apis/socialgraph/
 * @author bens
 */
public interface SocialGraphApiService {
  /**
   * Retrieve accounts populated through the social graph api
   * @param personId
   * @param urls
   * @return
   */
  public List<Account> getAccounts(String personId, List<String> urls);
}