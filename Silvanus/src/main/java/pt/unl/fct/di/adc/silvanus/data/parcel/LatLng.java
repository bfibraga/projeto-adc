package pt.unl.fct.di.adc.silvanus.data.parcel;

public class LatLng {

    private float lat;
    private float lng;

    public LatLng() {

    }

    public LatLng(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }
}
