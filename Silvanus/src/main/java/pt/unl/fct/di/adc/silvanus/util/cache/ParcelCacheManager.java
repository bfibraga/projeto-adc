package pt.unl.fct.di.adc.silvanus.util.cache;

import pt.unl.fct.di.adc.silvanus.data.terrain.TerrainData;



/**
 * Parcel Cache manager
 * @author GreenTeam
 * @param <K> Type of the key
 */
public class ParcelCacheManager<K> extends CacheManager<K> {

    public ParcelCacheManager(){
        super();
    }

    public ParcelCacheManager(long expiritation_time){
        super(expiritation_time);
    }

    /**
     * Insert new ParcelaData to given key and property
     * @param key - Given key to insert the object
     * @param data - ParcelaData to insert
     */
    public void put(K key, TerrainData data){
        this.put(key, ParcelProperties.ALL.name(), data);
    }
}
