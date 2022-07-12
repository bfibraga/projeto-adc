package pt.unl.fct.di.adc.silvanus.util.cache;

public class ChunkCacheManager<K> extends CacheManager<K> {

    public ChunkCacheManager(){
        super();
    }

    public ChunkCacheManager(long expiritation_time){
        super(expiritation_time);
    }
}
