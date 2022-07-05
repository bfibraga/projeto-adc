package pt.unl.fct.di.adc.silvanus.data.user.result;

import pt.unl.fct.di.adc.silvanus.data.terrain.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogoutData {
    public static final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private String time;
    private LatLng center;
    private double zoom;

    public LogoutData(String time, LatLng center, double zoom){
        this.time = time;
        this.center = center;
        this.zoom = zoom;
    }

    public LogoutData(Date date, LatLng center, double zoom){
        this.time = fmt.format(date);
        this.center = center;
        this.zoom = zoom;
    }

    public LogoutData(LatLng center, double zoom){
        this(new Date(), center, zoom);
    }

    public LogoutData(){
        this(new Date(), new LatLng(), 15.0);
    }

    public LatLng getCenter() {
        return center;
    }

    public double getZoom() {
        return zoom;
    }
}
