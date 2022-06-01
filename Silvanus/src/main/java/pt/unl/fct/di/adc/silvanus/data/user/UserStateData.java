package pt.unl.fct.di.adc.silvanus.data.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserStateData {
    private String set;
    private Set<String> confirmed;

    public UserStateData(){
        this("INACTIVE", new String[]{});
    }

    public UserStateData(String set, String[] confirmed_user){
        this.set = set.toUpperCase();
        this.confirmed = new HashSet<>();
        Collections.addAll(this.confirmed, confirmed_user);
    }

    public String getSet(){
        return this.set;
    }

    public Set<String> getConfirmed_user(){
        return this.confirmed;
    }
}
