package pt.unl.fct.di.adc.silvanus.data.user;

import pt.unl.fct.di.adc.silvanus.data.parcel.LatLng;

public class LogoutData {

    private LatLng center;
    private double zoom;

    public LogoutData(LatLng center, double zoom){
        this.center = center;
        this.zoom = zoom;
    }

    public LogoutData(){
        this(new LatLng(), 10.0);
    }

    public LatLng getCenter() {
        return center;
    }

    public double getZoom() {
        return zoom;
    }
}
