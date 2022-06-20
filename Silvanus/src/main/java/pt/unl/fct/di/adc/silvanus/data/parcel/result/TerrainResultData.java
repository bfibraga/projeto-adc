package pt.unl.fct.di.adc.silvanus.data.parcel.result;

import pt.unl.fct.di.adc.silvanus.data.parcel.LatLng;

public class TerrainResultData {
    //TODO Add more attributes to give to response

    private LatLng[] points;
    private LatLng center;
    private String color;

    public TerrainResultData(LatLng[] points, LatLng center, String color){
        this.points = points;
        this.center = center;
        this.color = color;
    }

    public TerrainResultData(){
        this(new LatLng[]{}, new LatLng(), "");
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
