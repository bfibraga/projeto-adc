package pt.unl.fct.di.adc.silvanus.data.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserStateData {
    private String state;
    private Set<String> confirmed_action;

    public UserStateData(){
    }

    public UserStateData(String state){
        this.state = state;
        this.confirmed_action = new HashSet<>();
    }

    public UserStateData(String state, Set<String> confirmed_action){
        this.state = state;
        this.confirmed_action = confirmed_action;
    }
}
