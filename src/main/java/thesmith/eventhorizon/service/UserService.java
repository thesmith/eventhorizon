package thesmith.eventhorizon.service;

import javax.persistence.NoResultException;

import thesmith.eventhorizon.model.User;

/**
 * UserService manages really simple users
 * @author bens
 */
public interface UserService {
  /**
   * Create a user
   * @param user
   */
  public void create(User user);
  
  /**
   * Delete a user
   * @param username
   */
  public void delete(String username);
  
  /**
   * Find a user by their username
   * @param username
   * @return
   */
  public User find(String username);
  
  /**
   * Authenticate a user by username and password
   * @param username
   * @param password
   * @return
   */
  public User authn(String username, String password) throws NoResultException, SecurityException;
  
  /**
   * Generate hashed version of password
   * @param password
   * @return
   * @throws SecurityException
   */
  public String hashPassword(String password);
}
