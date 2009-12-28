package thesmith.eventhorizon.service;

import java.util.List;

import thesmith.eventhorizon.model.Account;

/**
 * Defines the interface to Account objects
 * @author bens
 */
public interface AccountService {
  /**
   * Create an account
   * @param account
   */
  public void create(Account account);
  
  /**
   * If an account exists, create it
   * If not, update it
   * @param account
   */
  public void createOrUpdate(Account account);

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
   * Retrieve a distinct list of domains that a person has registered with
   * @param personId
   * @return
   */
  public List<String> domains(String personId);
}
