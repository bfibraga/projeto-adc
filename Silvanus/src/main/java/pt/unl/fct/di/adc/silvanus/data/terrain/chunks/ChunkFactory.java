package pt.unl.fct.di.adc.silvanus.data.terrain.chunks;

public class ChunkFactory<K> {
    //TODO Contains all ChunkManagers and gives the right object based on user's LatLng

    public ChunkFactory(){

    }

    @SafeVarargs
    public ChunkFactory(ChunkBoard<K>... chunkBoards ){

    }
}
