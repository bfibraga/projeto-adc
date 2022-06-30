package pt.unl.fct.di.adc.silvanus.data.user.result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoggedInData {


    private String token;
    private LoggedInVisibleData visible;


    public LoggedInData(String token, List<MenuData> menus, Date date){
        this.token = token;
        this.visible = new LoggedInVisibleData(menus, date);
    }

    public LoggedInData(String token, List<MenuData> menus, long time){
        this.token = token;
        this.visible = new LoggedInVisibleData(menus, time);
    }

    public LoggedInData(){
        this("", new ArrayList<>(), new Date());
    }

    public String getToken() {
        return token;
    }

    public LoggedInVisibleData getVisible() {
        return visible;
    }
}
