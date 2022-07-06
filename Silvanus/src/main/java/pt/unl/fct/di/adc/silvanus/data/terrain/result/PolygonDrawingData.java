package pt.unl.fct.di.adc.silvanus.data.terrain.result;

import pt.unl.fct.di.adc.silvanus.data.terrain.LatLng;
import pt.unl.fct.di.adc.silvanus.util.PolygonUtils;

public class PolygonDrawingData {

    private static final String PENDING_COLOR = "#222222";
    private LatLng[] points;
    private LatLng center;
    private String color;

    public PolygonDrawingData(LatLng[] points, String color){
        this.points = points;
        this.center = PolygonUtils.centroid(points);
        this.color = color;
    }

    public PolygonDrawingData(LatLng[] points, String color, boolean accepted){
        this.points = points;
        this.center = PolygonUtils.centroid(points);
        this.color = accepted ? color : PENDING_COLOR;
    }

    public PolygonDrawingData(){
        this(new LatLng[0], PENDING_COLOR);
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
