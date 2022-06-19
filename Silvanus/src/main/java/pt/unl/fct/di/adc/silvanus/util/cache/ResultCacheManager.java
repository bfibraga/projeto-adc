package pt.unl.fct.di.adc.silvanus.util.cache;

import pt.unl.fct.di.adc.silvanus.data.notification.Notification;

import java.util.ArrayList;
import java.util.List;

enum ResultProperties{
    ALL
}

public class ResultCacheManager<K> extends CacheManager<K>{

    public ResultCacheManager(){
        super();
    }

    public ResultCacheManager(long expiritation_time){
        super(expiritation_time);
    }

    /**
     * Insert new Result Mapper to given key and property
     * @param key - Given key to insert the object
     * @param data - Notification to insert
     */
    public void put(K key, String data){
        List<String> result = this.get(key, ResultProperties.ALL.name(), List.class);
        if (result == null){
            result = new ArrayList<>();
        }
        result.add(data);
        this.put(key, ResultProperties.ALL.name(), result);
    }

    public List<String> get(K key){
        return this.get(key, ResultProperties.ALL.name(), List.class);
    }
}
