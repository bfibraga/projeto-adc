package pt.unl.fct.di.adc.silvanus.util.chunks;

import pt.unl.fct.di.adc.silvanus.data.terrain.Chunk;

public class ChunkManager<C> {

    private Chunk[][] chunks;
    private final double[] chunkSize;
    private final double[] size;

    public ChunkManager(int lines, int columns, double length, double height){
        buildChunkBoard(lines, columns);
        this.size = new double[]{length, columns};
        double sizeX = length/columns;
        double sizeY = height/lines;
        this.chunkSize = new double[]{sizeX, sizeY};
    }

    @SuppressWarnings("unchecked")
    public void buildChunkBoard(int lines, int columns){
        this.chunks = new Chunk[columns][lines];
        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < columns; x++) {
                //this.chunks[x][y] = new Chunk(x,y);
            }
        }
    }

    public Chunk get(int x, int y) {
        return this.chunks[x][y];
    }

    public Chunk get(double posX, double posY) {
        int[] pos = worldToChunk(posX, posY);
        return this.get(pos[0],pos[1]);
    }

    private int[] worldToChunk(double posX, double posY){
        int x = (int) Math.floor(posX / chunkSize[0]);
        int y = (int) Math.floor(posY / chunkSize[1]);
        return new int[]{x,y};
    }

    public static int[] worldCoordToChunk(double posX, double posY, double chunkLength, double chunkHeight){
        int x = (int) Math.round(posX / chunkLength);
        int y = (int) Math.round(posY / chunkHeight);
        return new int[]{x,y};
    }

    public void add(int x, int y, C... content) {
        //this.get(x,y).addContent(content);
    }

}
