package pt.unl.fct.di.adc.silvanus.data.parcel;

public class Coordinate {

    private float lat;
    private float lon;

    public Coordinate() {

    }

    public Coordinate(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
