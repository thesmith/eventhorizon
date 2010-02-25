package thesmith.eventhorizon.service;

/**
 * Interface to cache service for app
 * @author bens
 */
public interface CacheService {
  /**
   * Get a cache entry
   * @param key
   * @return
   */
  public Object get(String key);
  /**
   * Put a cache entry
   * @param key
   * @param value
   */
  public void put(String key, Object value);
}
