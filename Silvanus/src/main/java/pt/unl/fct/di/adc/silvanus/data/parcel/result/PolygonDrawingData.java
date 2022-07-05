package pt.unl.fct.di.adc.silvanus.data.parcel.result;

import pt.unl.fct.di.adc.silvanus.data.parcel.LatLng;
import pt.unl.fct.di.adc.silvanus.util.Random;

public class PolygonDrawingData {
    private LatLng[] points;
    private LatLng center;
    private String color;

    public PolygonDrawingData(LatLng[] points, String color){
        this.points = points;
        this.center = new LatLng();
        this.color = color;
    }

    public PolygonDrawingData(LatLng[] points, String color, boolean accepted){
        this.points = points;
        this.center = new LatLng();
        if (accepted){
            this.color = color;
        } else {
            this.color = "#222222";
        }
    }

    public PolygonDrawingData(){
        this(new LatLng[0], "#ff0000");
    }

    public LatLng[] getPoints() {
        return points;
    }

    public LatLng getCenter() {
        return center;
    }

    public String getColor() {
        return color;
    }
}
