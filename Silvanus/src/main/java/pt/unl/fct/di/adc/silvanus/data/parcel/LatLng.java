package pt.unl.fct.di.adc.silvanus.data.parcel;

public class LatLng {

    private float lat;
    private float lng;

    public LatLng() {
        this((float) 38.659784, (float) -9.202765);
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

    @Override
    public String toString() {
        return String.format("(%s,%s)", this.getLat(), this.getLng());
    }
}
