package pt.unl.fct.di.adc.silvanus.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Helper class to implement JSON operations
 * @author GreenTeam
 */
public class JSON {

    private final static Gson gson = new Gson();

    /**
     * Convert a given object to JSON in String format
     * @param object - Given object
     * @return JSON in String
     */
    public static String encode(Object object){
        return gson.toJson(object);
    }

    /**
     * Convert JSON in String format to a given Class
     * @param json - JSON in String format
     * @param given_class - Class of type T to convert into
     * @return An object of type T
     * @param <T> - Type of class to convert into
     */
    public static <T> T decode(String json, Class<T> given_class) {
        return gson.fromJson(json, given_class);
    }

    public static <T> T decode(String json){
        return gson.fromJson(json,  new TypeToken<T>(){}.getType());
    }
}
