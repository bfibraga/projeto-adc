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
 * @author GreenTeam
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
        String data_json = cache_result.get(this.encodeProperty(property));

        if (data_json == null){
            return null;
        }

        return JSON.decode(data_json, class_object);
    }

    /**
     * Adds a new object to given key and property
     * @param key - Given key to insert the object
     * @param property - Property to insert the object
     * @param value - Object to insert
     */
    @SuppressWarnings("unchecked")
    public void put(K key, String property, V value){
        //TODO Testing

        Map<String, String> cache_result = this.verifyEntry(key);
        cache_result.put(this.encodeProperty(property), JSON.encode(value));
        this.cache.put(key.toString(), cache_result);
    }

    /**
     * Removes an object from given key and property
     * @param key - Given key to remove a object from a property
     * @param property - Given property to remove
     */
    @SuppressWarnings("unchecked")
    public void remove(K key, String property){
        Map<String, String> cache_result = this.verifyEntry(key);
        cache_result.remove(this.encodeProperty(property));
        this.cache.put(key.toString(), cache_result);
    }

    /**
     * Verify if given key has a Collection
     * @param key - Given key to check
     * @return Collection of given key
     */
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

    /**
     * Encode given property
     * @param property - Given property in String format
     * @return Encoded property in String format
     */
    private String encodeProperty(String property){
         return String.valueOf(property.hashCode());
    }

}
