package pt.unl.fct.di.adc.silvanus.data.user.result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoggedInVisibleData {
    private static final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String time;
    private List<String> menus;


    public LoggedInVisibleData(List<String> menus, Date date) {
        this.time = fmt.format(date);
        this.menus = menus;
    }

    public LoggedInVisibleData(List<String> menus, long time) {
        this.time = fmt.format(new Date(time));
        this.menus = menus;
    }

    public LoggedInVisibleData(List<String> menus){
        this(menus, new Date());
    }

    public LoggedInVisibleData(){
        this(new ArrayList<>(), new Date());
    }

    public List<String> getMenus() {
        return menus;
    }

    public String getTime() {
        return time;
    }
}
