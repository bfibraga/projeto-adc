package pt.unl.fct.di.adc.silvanus.data.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class UserRoleData {

    private String name;
    private String color;

    public UserRoleData(String name, String color){
        this.name = name;
        this.color = color;
    }

    public UserRoleData(){
        this("User", "#6aa84f");
    }

    @Override
    public String toString() {
        return String.format("UserRoleData:\n\tName:%s\n\tColor:%s\n", this.name, this.color);
    }
}
