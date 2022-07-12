package pt.unl.fct.di.adc.silvanus.util.cache;

import pt.unl.fct.di.adc.silvanus.data.notification.Notification;

enum NotificationProperties {
    ALL
}

/**
 * Notification Cache manager
 * @author GreenTeam
 * @param <K> Type of the key
 */
public class NotificationCacheManager<K> extends CacheManager<K>{

    public NotificationCacheManager(){
        super();
    }

    public NotificationCacheManager(long expiritation_time){
        super(expiritation_time);
    }

    /**
     * Insert new Notification to given key and property
     * @param key - Given key to insert the object
     * @param data - Notification to insert
     */
    public void put(K key, Notification data){
        this.put(key, NotificationProperties.ALL.name(), data);
    }
}
