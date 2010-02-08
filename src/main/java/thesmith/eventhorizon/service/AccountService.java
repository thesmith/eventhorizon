package thesmith.eventhorizon.service;

import java.util.List;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import thesmith.eventhorizon.model.Account;

/**
 * Defines the interface to Account objects
 * @author bens
 */
@SuppressWarnings("deprecation")
public interface AccountService {
  /** All available domains */
  public static enum DOMAIN {
    twitter, lastfm, flickr, birth, lives;
  }

  /** Domains that are freestyle */
  public static final List<String> FREESTYLE_DOMAINS = Lists.immutableList(
      DOMAIN.birth.toString(), DOMAIN.lives.toString());

  /**
   * Create an account
   * @param account
   */
  public void create(Account account);

  /**
   * Delete an account
   * @param account
   */
  public void delete(String personId, String domain);
  
  /**
   * Update an account
   * @param account
   */
  public void update(Account account);
  
  /**
   * Find an account
   * @param personId
   * @param domain
   * @return
   */
  public Account find(String personId, String domain);
  
  /**
   * Retrieve all of a person's accounts
   * @param personId
   * @return
   */
  public List<Account> list(String personId);
  
  /**
   * Retrieve all of a person's accounts and empty ones for accounts they don't have
   * @param personId
   * @return
   */
  public List<Account> listAll(String personId);
  
  /**
   * Retrieve a limited list of accounts that need processing
   * @param limit
   * @return
   */
  public List<Account> toProcess(int limit);
  
  /**
   * Retrieve a distinct list of domains that a person has registered with
   * @param personId
   * @return
   */
  public List<String> domains(String personId);
  
  /**
   * Create a new account object
   * @param personId
   * @param domain
   * @return
   */
  public Account account(String personId, String domain);
}
