package thesmith.eventhorizon.service;

import java.util.Collection;
import java.util.Map;

/**
 * Interface to cache service for app
 * @author bens
 */
public interface CacheService<T> {
  /**
   * Get a cache entry
   * @param key
   * @return
   */
  public T get(String key);
  /**
   * Put a cache entry
   * @param key
   * @param value
   */
  public void put(String key, T value);
  /**
   * Get a map of all the available cached objects from a collection of keys
   * @param keys
   * @return
   */
  public Map<String, T> getAll(Collection<String> keys);
  /**
   * Cache a number of key-value pairs
   * @param objects
   */
  public void putAll(Map<String, T> objects);
}
