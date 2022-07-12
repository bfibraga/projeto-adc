package pt.unl.fct.di.adc.silvanus.util.cache;

import pt.unl.fct.di.adc.silvanus.data.user.*;
import pt.unl.fct.di.adc.silvanus.implementation.user.perms.UserRole;



/**
 * User Cache manager
 * @author GreenTeam
 * @param <K> Type of the key
 */
public class UserCacheManager<K> extends CacheManager<K> {

    public UserCacheManager(){
        super();
    }

    public UserCacheManager(long expiritation_time){
        super(expiritation_time);
    }

    /**
     * Insert new LoginData to given key and property
     * @param key - Given key to insert the object
     * @param data - LoginData to insert
     */
    public void put(K key, LoginData data){
        this.put(key, UserProperties.CREDENTIALS.name(), data);
    }

    /**
     * Insert new UserData to given key and property
     * @param key - Given key to insert the object
     * @param data - UserData to insert
     */
    public void put(K key, UserData data){
        this.put(key, UserProperties.ALL.name(), data);
    }

    /**
     * Insert new UserInfoData to given key and property
     * @param key - Given key to insert the object
     * @param data - UserInfoData to insert
     */
    public void put(K key, UserInfoData data){
        this.put(key, UserProperties.INFO.name(), data);
    }

    /**
     * Insert new UserRole to given key and property
     * @param key - Given key to insert the object
     * @param data - UserRole to insert
     */
    public void put(K key, UserRole data){
        this.put(key, UserProperties.ROLE.name(), data);
    }

    /**
     * Insert new UserStateData to given key and property
     * @param key - Given key to insert the object
     * @param data - UserStateData to insert
     */
    public void put(K key, UserStateData data){
        this.put(key, UserProperties.STATE.name(), data);
    }

    /**
     * Get a LoginData of given key
     * @param key - Given key to get the object
     * @return LoginData of key
     */
    public LoginData getLoginData(K key){
        return this.get(key, UserProperties.CREDENTIALS.name(), LoginData.class);
    }

    public UserData getUserData(K key){
        return this.get(key, UserProperties.ALL.name(), UserData.class);
    }

    public UserInfoData getInfoData(K key){
        return this.get(key, UserProperties.INFO.name(), UserInfoData.class);
    }

    public UserRole getRoleData(K key){
        return this.get(key, UserProperties.ROLE.name(), UserRole.class);
    }

    public UserStateData getStateData(K key){
        return this.get(key, UserProperties.STATE.name(), UserStateData.class);
    }
}
