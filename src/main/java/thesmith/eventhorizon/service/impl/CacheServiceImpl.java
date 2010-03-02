package thesmith.eventhorizon.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.service.CacheService;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.appengine.repackaged.com.google.common.collect.Maps;

/**
 * Implementation sets up Cache through appengine's CacheFactory
 * 
 * @author bens
 */
public class CacheServiceImpl<T> implements CacheService<T> {
  private static final int EXPIREY = 3600;
  protected final Log logger = LogFactory.getLog(this.getClass());
  
  @Autowired
  private CacheFactory cacheFactory;
  private Cache cache;

  @SuppressWarnings("unchecked")
  public T get(String key) {
    return (T) getCache().get(key);
  }

  public void put(String key, T value) {
    getCache().put(key, value);
  }
  
  @SuppressWarnings("unchecked")
  public Map<String, T> getAll(Collection<String> keys) {
    final Map<String, T> values = Maps.newHashMap();
    try {
      Map<String, T> cached = getCache().getAll(keys);
      for (Entry<String, T> entry: cached.entrySet()) {
        if (null != entry.getValue())
          values.put(entry.getKey(), entry.getValue());
      }
    } catch (CacheException e) {
      if (logger.isWarnEnabled())
        logger.warn(e);
    }
    return values;
  }
  
  public void putAll(Map<String, T> objects) {
    getCache().putAll(objects);
  }

  private Cache getCache() {
    if (cache == null) {
      final Map<Integer, Integer> props = Maps.newHashMap();
      //props.put(GCacheFactory.EXPIRATION_DELTA, EXPIREY);
      try {
        cache = cacheFactory.createCache(props);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return cache;
  }
}
