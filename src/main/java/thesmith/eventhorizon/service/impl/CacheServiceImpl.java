package thesmith.eventhorizon.service.impl;

import java.util.Collections;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;
import thesmith.eventhorizon.service.CacheService;

/**
 * Implementation sets up Cache through appengine's CacheFactory
 * 
 * @author bens
 */
public class CacheServiceImpl implements CacheService {
  protected final Cache cache;

  public CacheServiceImpl() {
    try {
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      cache = cacheFactory.createCache(Collections.emptyMap());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Object get(String key) {
    return cache.get(key);
  }

  public void put(String key, Object value) {
    cache.put(key, value);
  }
}
