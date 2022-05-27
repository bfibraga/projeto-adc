package pt.unl.fct.di.adc.silvanus.util.cache;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import pt.unl.fct.di.adc.silvanus.util.JSON;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Abstract Cache manager
 */
public abstract class CacheManager<K, V> {

    protected Cache cache;

    public CacheManager() {
        try {
            CacheFactory cacheFactory = javax.cache.CacheManager.getInstance().getCacheFactory();
            Map<Integer, Long> properties = new HashMap<>();
            properties.put(GCacheFactory.EXPIRATION_DELTA, TimeUnit.HOURS.toHours(12));
            this.cache = cacheFactory.createCache(Collections.emptyMap());
        } catch (CacheException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns in cache object of given key
     * @param key - Given key to get object
     * @param property - Given property to get an object
     * @param class_object - Class of an object to get
     * @return An object in cache of given type
     * @param <O> - Type of the object
     */
    public <O> O get(K key, String property, Class<O> class_object) {
        Map<String, String> cache_result = this.verifyEntry(key);
        String data_json = cache_result.get(String.valueOf(property.hashCode()));

        if (data_json == null){
            return null;
        }

        return JSON.decode(data_json, class_object);
    }

    @SuppressWarnings("unchecked")
    public void put(K key, String property, V value){
        //TODO Testing

        Map<String, String> cache_result = this.verifyEntry(key);
        cache_result.put(String.valueOf(property.hashCode()), JSON.encode(value));
        this.cache.put(key.toString(), cache_result);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> verifyEntry(K key){
        if (key == null) {
            return null;
        }

        String keyID = key.toString();
        String available_data_json = (String) this.cache.get(keyID);

        if (available_data_json == null){
            this.cache.put(keyID, new HashMap<>());
            return null;
        }

        return JSON.decode(available_data_json, Map.class);
    }

}
