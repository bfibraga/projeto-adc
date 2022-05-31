package pt.unl.fct.di.adc.silvanus.data.parcel;

public class Coordenada {

    private float lat;
    private float lon;

    public Coordenada() {

    }

    public Coordenada(float lat, float lon) {
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
