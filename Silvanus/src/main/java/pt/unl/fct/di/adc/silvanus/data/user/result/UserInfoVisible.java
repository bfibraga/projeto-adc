package pt.unl.fct.di.adc.silvanus.data.user.result;

import pt.unl.fct.di.adc.silvanus.data.user.UserInfoData;
import pt.unl.fct.di.adc.silvanus.implementation.user.perms.UserRole;

public class UserInfoVisible {

    private static final String NOT_DEFINED = "UNDEFINED";
    private String username;
    private String email;
    private UserInfoData info;
    private String state;
    private String role_name;
    private String role_color;
    private LogoutData logoutData;
    private LoggedInVisibleData loggedinData;

    public UserInfoVisible(
            String username,
            String email,
            UserInfoData info,
            String state,
            String role_name,
            String role_color,
            LogoutData logoutData,
            LoggedInVisibleData loggedInData
    ){
        this.username = username;
        this.email = email;
        this.info = info;
        this.state = state;
        this.role_name = role_name;
        this.role_color = role_color;
        this.logoutData = logoutData;
        this.loggedinData = loggedInData;
    }

    public UserInfoVisible(){
        this(NOT_DEFINED, NOT_DEFINED, new UserInfoData(), NOT_DEFINED, UserRole.ENDUSER.getRoleName(), UserRole.ENDUSER.getRoleColor(), new LogoutData(), new LoggedInVisibleData());
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public UserInfoData getInfo() {
        return info;
    }

    public String getState() {
        return state;
    }

    public String getRole_name() {
        return role_name;
    }

    public String getRole_color() {
        return role_color;
    }

    public LogoutData getLogoutData() {
        return logoutData;
    }

    public LoggedInVisibleData getLoggedinData() {
        return loggedinData;
    }
}
