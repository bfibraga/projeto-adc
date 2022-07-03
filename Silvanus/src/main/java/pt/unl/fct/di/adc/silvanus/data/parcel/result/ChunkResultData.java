package pt.unl.fct.di.adc.silvanus.data.parcel.result;

import pt.unl.fct.di.adc.silvanus.data.parcel.LatLng;
import pt.unl.fct.di.adc.silvanus.util.chunks.Chunk2;

import java.util.HashSet;
import java.util.Set;

public class ChunkResultData {
    private int[] chunk;
    private Set<PolygonDrawingData> data;

    public ChunkResultData(int[] chunk, Set<PolygonDrawingData> data){
        this.chunk = chunk;
        this.data = data;
    }

    public ChunkResultData(){
        this(new int[]{0,0}, new HashSet<>());
    }

    public int[] getChunk() {
        return chunk;
    }

    public Set<PolygonDrawingData> getData() {
        return data;
    }
}
