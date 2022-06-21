package pt.unl.fct.di.adc.silvanus.data.parcel.result;

import pt.unl.fct.di.adc.silvanus.data.parcel.LatLng;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainIdentifierData;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainInfoData;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainOwner;

public class TerrainResultData {
    //TODO Add more attributes to give to response

    private LatLng[] points;
    private LatLng center;
    private TerrainIdentifierData credentials;
    private TerrainOwner owner;
    private TerrainInfoData info;
    private String color;

    public TerrainResultData(LatLng[] points, LatLng center, String color, TerrainIdentifierData credentials, TerrainOwner owner, TerrainInfoData info){
        this.points = points;
        this.center = center;
        this.credentials = credentials;
        this.owner = owner;
        this.info = info;
        this.color = color;
    }

    public TerrainResultData(){
        this(new LatLng[]{}, new LatLng(), "", new TerrainIdentifierData(), new TerrainOwner(), new TerrainInfoData());
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

    public TerrainIdentifierData getCredentials() {
        return credentials;
    }

    public TerrainOwner getOwner() {
        return owner;
    }

    public TerrainInfoData getInfo() {
        return info;
    }
}
