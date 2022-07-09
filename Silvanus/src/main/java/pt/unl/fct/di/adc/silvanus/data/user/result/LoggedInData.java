package pt.unl.fct.di.adc.silvanus.data.user.result;

import pt.unl.fct.di.adc.silvanus.data.user.UserStateData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoggedInData {

    private static final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String token;
    private UserStateData stateData;
    private String time;

    public LoggedInData(String token){
        this.token = token;
        this.stateData = new UserStateData();
        this.time = fmt.format(new Date());
    }

    public LoggedInData(String token, UserStateData userStateData){
        this.token = token;
        this.stateData = userStateData;
        this.time = fmt.format(new Date());
    }

    public LoggedInData(String token, UserStateData userStateData, Date date){
        this.token = token;
        this.stateData = userStateData;
        this.time = fmt.format(date);
    }

    public LoggedInData(String token, UserStateData userStateData, long time){
        this.token = token;
        this.stateData = userStateData;
        this.time = fmt.format(new Date(time));
    }

    public LoggedInData(){
        this("", new UserStateData());
    }

    public String getToken() {
        return token;
    }

    public UserStateData getStateData() {
        return stateData;
    }

    public String getTime() {
        return time;
    }
}
