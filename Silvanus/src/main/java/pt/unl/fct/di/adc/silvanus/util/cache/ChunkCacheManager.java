package pt.unl.fct.di.adc.silvanus.util.cache;

import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;

public class ChunkCacheManager<K> extends CacheManager<K> {

    public ChunkCacheManager(){
        super();
    }

    public ChunkCacheManager(long expiritation_time){
        super(expiritation_time);
    }
}
