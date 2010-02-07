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
