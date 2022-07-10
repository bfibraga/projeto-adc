package pt.unl.fct.di.adc.silvanus.util.cache;

public class CommunityCacheManager<K> extends CacheManager<K>{
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public CommunityCacheManager(long time){
        super(time);
    }

    public CommunityCacheManager(){
        super(EXPIRATION_TIME);
    }

}
