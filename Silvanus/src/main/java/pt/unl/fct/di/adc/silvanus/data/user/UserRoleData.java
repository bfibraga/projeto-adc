package pt.unl.fct.di.adc.silvanus.data.user;

import pt.unl.fct.di.adc.silvanus.data.user.UserRole;

public class UserRoleData {

    private String name;
    private String color;

    public UserRoleData(UserRole role){
        this.name = role.getRoleName().trim();
        this.color = role.getRoleColor().trim();
    }

    public UserRoleData(String name, String color){
        this.name = name;
        this.color = color;
    }

    public UserRoleData(){
        this(UserRole.USER);
    }
}
