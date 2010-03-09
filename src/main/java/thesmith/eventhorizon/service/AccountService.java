package thesmith.eventhorizon.service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import thesmith.eventhorizon.model.Account;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Maps;

/**
 * Defines the interface to Account objects
 * @author bens
 */
@SuppressWarnings("deprecation")
public interface AccountService {
  /** All available domains */
  public static enum DOMAIN {
    twitter, lastfm, flickr, birth, lives, wordr, github;
  }
  
  public static final Map<String, String> DOMAIN_SEARCH = Maps.immutableMap(
      "twitter.com", DOMAIN.twitter.toString(),
      "last.fm", DOMAIN.lastfm.toString(),
      "flickr.com", DOMAIN.flickr.toString(),
      "wordr.com", DOMAIN.wordr.toString(),
      "github.com", DOMAIN.github.toString());
  
  public static final Map<String, Pattern> DOMAIN_MATCHERS = Maps.immutableMap(
      DOMAIN.twitter.toString(), Pattern.compile("^http:\\/\\/twitter.com\\/(.+?)\\/?$"),
      DOMAIN.lastfm.toString(), Pattern.compile("^http:\\/\\/www.last.fm\\/user\\/(.+?)\\/?$"),
      DOMAIN.flickr.toString(), Pattern.compile("^http:\\/\\/www.flickr.com\\/photos\\/(.+?)\\/$"),
      DOMAIN.wordr.toString(), Pattern.compile("^http:\\/\\/wordr.com\\/(.+?)\\/?$"),
      DOMAIN.github.toString(), Pattern.compile("^http:\\/\\/github.com\\/(.+?)\\/?$"));

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
