package pt.unl.fct.di.adc.silvanus.data.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserStateData {
    private String set;
    private Set<String> confirmed;

    public UserStateData(){
        this("ACTIVE", new String[]{});
    }

    public UserStateData(String set, String[] confirmed_user){
        this.set = set.toUpperCase();
        this.confirmed = new HashSet<>();
        Collections.addAll(this.confirmed, confirmed_user);
    }

    public UserStateData(String set, Set<String> confirmed_user){
        this.set = set.toUpperCase();
        this.confirmed = confirmed_user;
    }

    public String getSet(){
        return this.set;
    }

    public Set<String> getConfirmed_user(){
        return this.confirmed;
    }

    private boolean validField(String keyword) {
        return keyword !=null && !keyword.trim().equals("");
    }

    private boolean validSet(String set) {
        return validField(set) && (set.trim().equalsIgnoreCase("ACTIVE") || set.trim().equalsIgnoreCase("INACTIVE"));
    }

    public boolean validation() {
        return validSet(this.getSet());
    }

    @Override
    public String toString() {
        return String.format("UserStateData:\n\tSet:%s\n\tConfirmed:%s", this.getSet(), this.getConfirmed_user());
    }

    public boolean isActive(){
        return this.getSet().equals("ACTIVE");
    }
}
