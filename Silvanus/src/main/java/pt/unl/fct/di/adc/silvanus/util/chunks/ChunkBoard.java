package pt.unl.fct.di.adc.silvanus.util.chunks;

import org.locationtech.jts.geom.Polygon;
import pt.unl.fct.di.adc.silvanus.data.parcel.LatLng;
import pt.unl.fct.di.adc.silvanus.util.PolygonUtils;
import pt.unl.fct.di.adc.silvanus.util.chunks.exceptions.OutOfChunkBounds;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkBoard<C> {

    private final double[] offset;
    private final double[] boardSize;
    private Chunk2<C>[][] chunk2s;
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
        this.chunk2s = new Chunk2[columns][lines];
        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < columns; x++) {
                this.chunk2s[x][y] = new Chunk2<>(x,y);
            }
        }
    }

    public Chunk2<C> get(int x, int y) {
        return this.chunk2s[x][y];
    }

    public Chunk2<C> get(double posX, double posY) throws OutOfChunkBounds {
        int[] pos = worldCoordsToChunk(posX, posY);
        return this.get(pos[0],pos[1]);
    }

    //TODO Testing
    public Chunk2<C>[][] getArea(double[] topLeft, double[] bottomRight) throws OutOfChunkBounds {
        int[] posTL = this.worldCoordsToChunk(
                Math.min(topLeft[0], bottomRight[0]),
                Math.max(topLeft[1], bottomRight[1]));
        int[] posBR = this.worldCoordsToChunk(
                Math.max(topLeft[0], bottomRight[0]),
                Math.min(topLeft[1], bottomRight[1]));

        return this.getArea(posTL, posBR);
    }

    @SuppressWarnings("unchecked")
    public Chunk2<C>[][] getArea(int[] topLeft, int[] bottomRight){
        int areaLength = Math.abs(bottomRight[0]-topLeft[0]);
        int areaHeight = Math.abs(bottomRight[1]-topLeft[1]);

        System.out.println("Length: " + areaLength + "\nHeight: " + areaHeight);
        Chunk2<C>[][] result = new Chunk2[areaLength][areaHeight];
        for (int y = 0 ; y < areaHeight ; y++){
            for (int x = 0 ; x < areaLength ; x++){
                //TODO Check if this chunk pos is in bounds
                result[x][y] = chunk2s[x+ topLeft[0]][y+ bottomRight[1]];
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

    public int[] worldCoordsToChunk(double posX, double posY) throws OutOfChunkBounds {
        /*if (!inBounds(posX,posY)) {
            return new int[]{0,0};
        }*/
        double[] boardPos = new double[]{ posX - offset[0], posY - offset[1]};

        int x = (int) Math.floor(boardPos[0] / chunkSize[0]);
        int y = (int) Math.floor(boardPos[1] / chunkSize[1]);

        if(!isInside(x,y)){
            throw new OutOfChunkBounds(String.format("(%s,%s)", x, y));
        }

        return new int[]{x,y};
    }

    private LatLng chunkToWorldCoords(int x, int y){

        LatLng result = new LatLng(
                (float) (x*chunkSize[0] + offset[0]),
                (float) (y*chunkSize[1] + offset[1])
        );
        System.out.printf("(%s,%s)->%s%n", x, y,result);
        return result;
    }

    @SafeVarargs
    public final void add(int x, int y, C... content) {
        this.get(x,y).addContent(content);
    }

    @SafeVarargs
    public final void add(double x, double y, C... content) throws OutOfChunkBounds {
        this.get(x,y).addContent(content);
    }

    @SafeVarargs
    public final void put(int x, int y, C... content){
        this.get(x,y).putContent(content);
    }

    @SafeVarargs
    public final void put(double x, double y, C... content) throws OutOfChunkBounds {
        this.get(x,y).putContent(content);
    }

    @SafeVarargs
    public final void remove(int x, int y, C... content){
        this.get(x,y).removeContent(content);
    }

    @SafeVarargs
    public final void remove(double x, double y, C... content) throws OutOfChunkBounds {
        this.get(x,y).removeContent(content);
    }

    public final void clear(int x, int y){
        this.get(x,y).clearContent();
    }

    public final void clear(double x, double y) throws OutOfChunkBounds {
        this.get(x,y).clearContent();
    }

    public List<Chunk2<C>> line(double x0, double y0, double x1, double y1) throws OutOfChunkBounds {
        int[] pos0 = worldCoordsToChunk(x0, y0);
        int[] pos1 = worldCoordsToChunk(x1, y1);
        return this.line(pos0[0], pos0[1], pos1[0], pos1[1]);
    }

    public List<Chunk2<C>> line(int x0, int y0, int x1, int y1){
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

    private List<Chunk2<C>> lineHigh(int x0, int y0, int x1, int y1) {
        List<Chunk2<C>> result = new ArrayList<>();

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
            Chunk2<C> chunk2 = this.get(x,y);
            result.add(chunk2);
            if (D > 0){
                x += xi;
                D += 2*(dx-dy);
            } else {
                D += 2*dx;
            }
        }
        return result;
    }

    private List<Chunk2<C>> lineLow(int x0, int y0, int x1, int y1) {
        List<Chunk2<C>> result = new ArrayList<>();

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
            Chunk2<C> chunk2 = this.get(x,y);
            result.add(chunk2);
            if (D > 0){
                y += yi;
                D += 2*(dy-dx);
            } else {
                D += 2*dy;
            }
        }
        return result;
    }

    public List<Chunk2<C>> polygon(LatLng[] points) throws OutOfChunkBounds {
        List<Chunk2<C>> result = new ArrayList<>();

        int top = Integer.MIN_VALUE;
        int bottom = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        int left = Integer.MAX_VALUE;

        for (int p = 0; p < points.length-1; p++) {
            LatLng current = points[p];
            LatLng next = points[p+1];

            int[] currentChunkCoord = worldCoordsToChunk(current.getLng(), current.getLat());
            System.out.println(Arrays.toString(currentChunkCoord));

            top = Math.max(top, currentChunkCoord[1]);
            bottom = Math.min(bottom, currentChunkCoord[1]);
            right = Math.max(right, currentChunkCoord[0]);
            left = Math.min(left, currentChunkCoord[0]);

            result.addAll(this.line(current.getLng(), current.getLat(), next.getLng(), next.getLat()));
        }
        //TODO Refatorizar codigo repetido
        int[] currentChunkCoord = worldCoordsToChunk(points[points.length-1].getLng(), points[points.length-1].getLat());

        top = Math.max(top, currentChunkCoord[1]);
        bottom = Math.min(bottom, currentChunkCoord[1]);
        right = Math.max(right, currentChunkCoord[0]);
        left = Math.min(left, currentChunkCoord[0]);

        result.addAll(this.line(points[0].getLng(), points[0].getLat(), points[points.length-1].getLng(), points[points.length-1].getLat()));

        System.out.println(top);
        System.out.println(bottom);
        System.out.println(right);
        System.out.println(left);


        Polygon terrainPolygon = PolygonUtils.polygon(points);
        for (int y = bottom; y <= top; y++) {
            for (int x = left; x <= right; x++) {
                LatLng currPoint = chunkToWorldCoords(x,y);

                Polygon chunkPolygon = PolygonUtils.box(
                        (float) (currPoint.getLat() + (chunkSize[1])),
                        currPoint.getLat(),
                        currPoint.getLng(),
                        (float) (currPoint.getLng() + (chunkSize[0])));

                if (chunkPolygon.intersects(terrainPolygon)){
                    Chunk2<C> target = get(x,y);
                    result.add(get(x,y));
                }
            }
        }
        return result;
    }

    //TODO Remove this later
    public void printChunks(){
        /*for (int x = 0; x < this.chunk2s.length; x++) {
            System.out.printf(" %s ", x);
        }
        System.out.println();*/

        for (int y = this.chunk2s[0].length - 1; y >= 0 ; y--) {
            for (int x = 0; x < this.chunk2s.length; x++) {
                String tag = this.chunk2s[x][y].getTag();

                System.out.print( tag.equals("") ? "[_]" : String.format("[%s]", tag));

            }
            System.out.println();
        }
    }
}
