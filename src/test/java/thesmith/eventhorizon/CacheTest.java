package thesmith.eventhorizon;

import static org.junit.Assert.*;

import java.util.Collections;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.junit.Test;

public class CacheTest extends CacheBaseTest {
  @Test
  public void shouldCache() throws Exception {
    CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
    Cache cache = cacheFactory.createCache(Collections.emptyMap());
    String value = "thing";
    cache.put("key", value);
    assertEquals(value, cache.get("key"));
  }
}
