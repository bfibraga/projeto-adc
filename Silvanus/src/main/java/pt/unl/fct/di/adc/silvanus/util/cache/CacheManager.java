package pt.unl.fct.di.adc.silvanus.util.cache;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import pt.unl.fct.di.adc.silvanus.util.JSON;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Abstract Cache manager
 * @author GreenTeam
 * @param <K> Type of the key
 */
public abstract class CacheManager<K> {

    private static final int MINS_DURATION = 12*60;

    //TODO Maybe introduce "importance" levels of cache
    protected Cache cache;

    public CacheManager(){
        this(MINS_DURATION);
    }

    public CacheManager(long expiration_time) {
        try {
            this.createCache(expiration_time);
        } catch (CacheException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createCache(long expiration_time) throws CacheException {
        CacheFactory cacheFactory = javax.cache.CacheManager.getInstance().getCacheFactory();
        Map<Integer, Long> properties = new HashMap<>();
        properties.put(GCacheFactory.EXPIRATION_DELTA, TimeUnit.MINUTES.toHours(expiration_time));
        //TODO Test this part
        this.cache = cacheFactory.createCache(new ConcurrentHashMap<>());
    }

    /**
     * Returns in cache object of given key of a property
     * @param key - Given key to get object
     * @param property - Given property to get an object
     * @param class_object - Class of an object to get
     * @return An object in cache of given type
     */
    public <O> O get(K key, String property, Class<O> class_object) {
        Map<String, String> cache_result = this.verifyEntry(key);
        String data_json = cache_result.get(hashProperty(property));

        if (data_json == null){
            return null;
        }

        return JSON.decode(data_json, class_object);
    }

    /**
     * @param <O>
     * @param pattern
     * @param property
     * @param class_object
     * @return
     */
    @SuppressWarnings("unchecked")
    public <O> List<O> getAll(K pattern, String property, Class<O> class_object){
        if (pattern == null) {
            return new ArrayList<>();
        }
        List<O> result = new ArrayList<>();
        String regex = "(.*)" + pattern + "(.*)";
        for (Map.Entry<String, String> stringStringEntry : (Iterable<Map.Entry<String, String>>) this.cache.entrySet()) {
            String curr_key = stringStringEntry.getKey();
            if (curr_key.matches(regex)) {
                Map<String, String> available_data = (Map<String, String>) this.cache.get(curr_key);

                if (available_data == null) {
                    available_data = new HashMap<>();
                    this.cache.put(curr_key, available_data);
                }

                String data_json = available_data.get(hashProperty(property));

                if (data_json != null) {
                    result.add(JSON.decode(data_json, class_object));
                }
            }
        }
        return result;
    }

    //TODO Alter visibility of this method
    /**
     * Adds a new object to given key and property
     * @param key - Given key to insert the object
     * @param property - Property to insert the object
     * @param value - Object to insert
     */
    @SuppressWarnings("unchecked")
    public <V> void put(K key, String property, V value){
        //TODO Testing
        Map<String, String> cache_result = this.verifyEntry(key);
        cache_result.put(hashProperty(property), JSON.encode(value));
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
        cache_result.remove(hashProperty(property));
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
        Map<String, String> available_data_json = (Map<String, String>) this.cache.get(keyID);

        if (available_data_json == null){
            this.cache.put(keyID, new ConcurrentHashMap<>());
            return new ConcurrentHashMap<>();
        }

        return available_data_json;
    }

    /**
     * Encode given property
     * @param property - Given property in String format
     * @return Encoded property in String format
     */
    protected static String hashProperty(String property){
         return String.valueOf(property.hashCode());
    }

}
