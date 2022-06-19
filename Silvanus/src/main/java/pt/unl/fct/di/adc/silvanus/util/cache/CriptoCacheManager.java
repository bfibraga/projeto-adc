package pt.unl.fct.di.adc.silvanus.util.cache;

import pt.unl.fct.di.adc.silvanus.util.cripto.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

enum CriptoProperties {
    ALL;
}

public class CriptoCacheManager<K> extends CacheManager<K> {

    private final Map<String, Integer> criptoMapper;
    public CriptoCacheManager() {
        super();
        this.criptoMapper = new ConcurrentHashMap<>();
        for (int i = 0; i < available_cripto.length; i++) {
            String result = available_cripto[i].name();
            this.criptoMapper.put(result,i);
        }
    }

    private static final CRIPTO[] available_cripto = {
            new SHA256(),
            new SHA256HEX(),
            new SHA512(),
            new SHA512HEX(),
    };

    public void put(K key, String cripto_name) {
        this.put(key, CriptoProperties.ALL.name(), cripto_name);
    }

    public CRIPTO get(K key) {
        String result_name = this.get(key, CriptoProperties.ALL.name(), String.class);
        return result_name == null ? this.newCripto(key) : this.map(result_name);
    }

    public CRIPTO newCripto(K key){
        int random = (int) ((available_cripto.length) * Math.random());
        CRIPTO result = available_cripto[Math.max(0, Math.min(available_cripto.length-1, random))];
        this.put(key, result.name());
        return result;
    }

    public CRIPTO map(String cripto_name){
        int index = this.criptoMapper.get(cripto_name);
        return available_cripto[index];
    }
}
