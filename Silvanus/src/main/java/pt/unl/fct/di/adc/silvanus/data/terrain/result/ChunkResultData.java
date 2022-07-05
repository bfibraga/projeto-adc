package pt.unl.fct.di.adc.silvanus.data.terrain.result;

import pt.unl.fct.di.adc.silvanus.data.terrain.LatLng;

import java.util.Set;

public class ChunkResultData {
    private String chunk;
    private LatLng top_right;
    private LatLng bottom_left;
    private Set<PolygonDrawingData> data;

    public ChunkResultData(String chunk, LatLng top_right, LatLng bottom_left, Set<PolygonDrawingData> data){
        this.chunk = chunk;
        this.top_right = top_right;
        this.bottom_left = bottom_left;
        this.data = data;
    }

    public String getChunk() {
        return chunk;
    }

    public Set<PolygonDrawingData> getData() {
        return data;
    }

    public LatLng getTop_right() {
        return top_right;
    }

    public LatLng getBottom_left() {
        return bottom_left;
    }
}
