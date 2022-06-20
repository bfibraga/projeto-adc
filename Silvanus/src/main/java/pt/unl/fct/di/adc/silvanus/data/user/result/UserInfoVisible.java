package pt.unl.fct.di.adc.silvanus.data.user.result;

import org.checkerframework.checker.units.qual.N;
import pt.unl.fct.di.adc.silvanus.data.user.UserInfoData;
import pt.unl.fct.di.adc.silvanus.data.user.UserRole;

public class UserInfoVisible {

    private static final String NOT_DEFINED = "UNDEFINED";
    private String username;
    private String email;
    private String name;
    private String visibility;
    private String nif;
    private String address;
    private String telephone;
    private String smartphone;
    private String state;
    private String role_name;
    private String role_color;

    public UserInfoVisible(
            String username,
            String email,
            String name,
            String visibility,
            String nif,
            String address,
            String telephone,
            String smartphone,
            String state,
            String role_name,
            String role_color
    ){
        this.username = username;
        this.email = email;
        this.name = name;
        this.visibility = visibility;
        this.nif = nif;
        this.address = address;
        this.telephone = telephone;
        this.smartphone = smartphone;
        this.state = state;
        this.role_name = role_name;
        this.role_color = role_color;
    }

    public UserInfoVisible(){
        this(NOT_DEFINED, NOT_DEFINED, NOT_DEFINED, NOT_DEFINED, NOT_DEFINED, NOT_DEFINED, NOT_DEFINED, NOT_DEFINED, NOT_DEFINED, UserRole.USER.getRoleName(), UserRole.USER.getRoleColor());
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getNif() {
        return nif;
    }

    public String getAddress() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getSmartphone() {
        return smartphone;
    }

    public String getState() {
        return state;
    }

    public String getRole_color() {
        return role_color;
    }

    public String getRole_name() {
        return role_name;
    }
}
