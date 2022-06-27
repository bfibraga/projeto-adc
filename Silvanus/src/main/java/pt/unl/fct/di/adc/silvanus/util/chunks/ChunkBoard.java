package pt.unl.fct.di.adc.silvanus.util.chunks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChunkBoard<C> {

    private final double[] offset;
    private final double[] boardSize;
    private Chunk<C>[][] chunks;
    private final double[] chunkSize;
    private final double[] size;

    public ChunkBoard(int lines, int columns, double length, double height, double offsetX, double offsetY){
        buildChunkBoard(lines, columns);
        this.size = new double[]{columns, lines};
        this.boardSize = new double[]{length, height};
        double sizeX = length/columns;
        double sizeY = height/lines;
        this.chunkSize = new double[]{ sizeX, sizeY };
        this.offset = new double[]{ offsetX, offsetY };
        System.out.println(Arrays.toString(chunkSize));
    }

    @SuppressWarnings("unchecked")
    private void buildChunkBoard(int lines, int columns){
        this.chunks = new Chunk[columns][lines];
        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < columns; x++) {
                this.chunks[x][y] = new Chunk<>(x,y);
            }
        }
    }

    public Chunk<C> get(int x, int y) {
        return this.chunks[x][y];
    }

    public Chunk<C> get(double posX, double posY) {
        int[] pos = worldCoordsToChunk(posX, posY);
        return this.get(pos[0],pos[1]);
    }

    //TODO Testing
    public Chunk<C>[][] getArea(double[] topLeft, double[] bottomRight){
        int[] posTL = this.worldCoordsToChunk(
                Math.min(topLeft[0], bottomRight[0]),
                Math.max(topLeft[1], bottomRight[1]));
        int[] posBR = this.worldCoordsToChunk(
                Math.max(topLeft[0], bottomRight[0]),
                Math.min(topLeft[1], bottomRight[1]));

        return this.getArea(posTL, posBR);
    }

    @SuppressWarnings("unchecked")
    public Chunk<C>[][] getArea(int[] topLeft, int[] bottomRight){
        int areaLength = Math.abs(bottomRight[0]-topLeft[0]);
        int areaHeight = Math.abs(bottomRight[1]-topLeft[1]);

        System.out.println("Length: " + areaLength + "\nHeight: " + areaHeight);
        Chunk<C>[][] result = new Chunk[areaLength][areaHeight];
        for (int y = 0 ; y < areaHeight ; y++){
            for (int x = 0 ; x < areaLength ; x++){
                //TODO Check if this chunk pos is in bounds
                result[x][y] = chunks[x+ topLeft[0]][y+ bottomRight[1]];
            }
        }

        return result;
    }

    //TODO Testing
    public boolean isInside(double x, double y){
        return x >= offset[0] && x < offset[0]+boardSize[0] && y >= offset[1] && y < offset[1]+boardSize[1];
    }

    //TODO Testing
    public boolean isInside(int x, int y){
        return x >= 0 && x < size[0] && y >= 0 && y < size[1];
    }

    private int[] worldCoordsToChunk(double posX, double posY){
        /*if (!inBounds(posX,posY)) {
            return new int[]{0,0};
        }*/
        double[] boardPos = new double[]{ posX - offset[0], posY - offset[1]};

        int x = (int) Math.floor(boardPos[0] / chunkSize[0]);
        int y = (int) Math.floor(boardPos[1] / chunkSize[1]);

        if(!isInside(x,y)){
            return new int[]{};
        }

        return new int[]{x,y};
    }

    @SafeVarargs
    public final void add(int x, int y, C... content) {
        this.get(x,y).addContent(content);
    }

    @SafeVarargs
    public final void add(double x, double y, C... content){
        this.get(x,y).addContent(content);
    }

    @SafeVarargs
    public final void put(int x, int y, C... content){
        this.get(x,y).putContent(content);
    }

    @SafeVarargs
    public final void put(double x, double y, C... content){
        this.get(x,y).putContent(content);
    }

    @SafeVarargs
    public final void remove(int x, int y, C... content){
        this.get(x,y).removeContent(content);
    }

    @SafeVarargs
    public final void remove(double x, double y, C... content){
        this.get(x,y).removeContent(content);
    }

    public final void clear(int x, int y){
        this.get(x,y).clearContent();
    }

    public final void clear(double x, double y){
        this.get(x,y).clearContent();
    }

    public List<Chunk<C>> line(int x0, int y0, int x1, int y1){
        if (Math.abs(y1-y0) < Math.abs(x1-x0)){
            if (x0 > x1){
                return lineLow(x1,y1,x0,y0);
            } else {
                return lineLow(x0,y0,x1,y1);
            }
        } else {
            if (y0 > y1){
                return lineHigh(x1,y1,x0,y0);
            } else {
                return lineHigh(x0,y0,x1,y1);
            }
        }
    }

    private List<Chunk<C>> lineHigh(int x0, int y0, int x1, int y1) {
        List<Chunk<C>> result = new ArrayList<>();

        int dx = x1-x0;
        int dy = y1-y0;
        int xi = 1;
        if (dx < 0){
            xi = -1;
            dx = -dx;
        }
        int D = 2*dx - dy;
        int x = x0;

        for (int y = y0 ; y <= y1 ; y++){
            Chunk<C> chunk = this.get(x,y);
            chunk.setTag("PolygonBounds");
            result.add(chunk);
            if (D > 0){
                x += xi;
                D += 2*(dx-dy);
            } else {
                D += 2*dx;
            }
        }
        return result;
    }

    private List<Chunk<C>> lineLow(int x0, int y0, int x1, int y1) {
        List<Chunk<C>> result = new ArrayList<>();

        int dx = x1-x0;
        int dy = y1-y0;
        int yi = 1;
        if (dy < 0){
            yi = -1;
            dy = -dy;
        }
        int D = 2*dy - dx;
        int y = y0;

        for (int x = x0 ; x <= x1 ; x++){
            Chunk<C> chunk = this.get(x,y);
            chunk.setTag("PolygonBounds");
            result.add(chunk);
            if (D > 0){
                y += yi;
                D += 2*(dy-dx);
            } else {
                D += 2*dy;
            }
        }
        return result;
    }

    @SafeVarargs
    public final void fill(int x, int y, C... content){
        Chunk<C> chunk = this.get(x,y);
        System.out.println(chunk.getTag());
        if (!chunk.getTag().equals("PolygonBounds") && !chunk.getTag().equals("PolygonFill")){
            chunk.setTag("PolygonFill");
            chunk.addContent(content);

            if (isInside(x+1,y)){
                fill(x+1,y,content);
            }
            if (isInside(x,y+1)){
                fill(x,y+1,content);
            }
            if (isInside(x-1, y)){
                fill(x-1,y,content);
            }
            if(isInside(x, y-1)){
                fill(x,y-1,content);
            }
            /*if (isInside(x+1,y+1)){
                fill(x+1,y+1,content);
            }
            if (isInside(x-1,y+1)){
                fill(x-1,y+1,content);
            }
            if (isInside(x-1,y-1)){
                fill(x-1,y-1,content);
            }
            if(isInside(x+1,y-1)){
                fill(x+1,y-1,content);
            }*/
        }
    }

    public double DiamondAngle(double x, double y)
    {
        if (y >= 0)
            return (x >= 0 ? y/(x+y) : 1-x/(-x+y));
        else
            return (x < 0 ? 2-y/(-x-y) : 3+x/(x-y));
    }

    public double DiamondAngleToRadians(double dia)
    {
        double[] P = DiamondAngleToPoint(dia);
        return Math.atan2(P[1],P[0]);
    }

    public double DiamondAngleToDegree(double dia)
    {
        double rad = DiamondAngleToRadians(dia);
        return (rad*180)/Math.PI;
    }

    public double[] DiamondAngleToPoint(double dia)
    {
        return new double[]{(dia < 2 ? 1-dia : dia-3),
            (dia < 3 ? ((dia > 1) ? 2-dia : dia) : dia-4)};
    }

    public void printChunks(){
        for (int y = this.chunks[0].length - 1; y >= 0 ; y--) {
            for (int x = 0; x < this.chunks.length; x++) {
                System.out.print(!this.chunks[x][y].getTag().equals("") ? "[X]" : "[_]");
            }
            System.out.println();
        }
    }
}
