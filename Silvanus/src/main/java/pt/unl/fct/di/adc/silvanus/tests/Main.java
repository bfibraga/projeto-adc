package pt.unl.fct.di.adc.silvanus.tests;

import com.beust.ah.A;
import pt.unl.fct.di.adc.silvanus.data.parcel.LatLng;
import pt.unl.fct.di.adc.silvanus.util.chunks.Chunk2;
import pt.unl.fct.di.adc.silvanus.util.chunks.ChunkBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    private static final double RIGHT_MOST_LONGITUDE_CONTINENTE = -6.17;
    private static final double TOP_MOST_LATITUDE_CONTINENTE = 42.17;
    private static final double LEFT_MOST_LONGITUDE_CONTINENTE = -9.55;
    private static final double BOTTOM_MOST_LATITUDE_CONTINENTE = 36.85;
    public static final double PORTUGAL_SIZE_X = Math.abs(RIGHT_MOST_LONGITUDE_CONTINENTE - LEFT_MOST_LONGITUDE_CONTINENTE);
    public static final double PORTUGAL_SIZE_Y = Math.abs(TOP_MOST_LATITUDE_CONTINENTE - BOTTOM_MOST_LATITUDE_CONTINENTE);


    private static final double RIGHT_MOST_LONGITUDE_MADEIRA = -16.64;
    private static final double TOP_MOST_LATITUDE_MADEIRA = 32.88;
    private static final double LEFT_MOST_LONGITUDE_MADEIRA = -17.27;
    private static final double BOTTOM_MOST_LATITUDE_MADEIRA = 32.62;
    public static final double MADEIRA_SIZE_X = Math.abs(RIGHT_MOST_LONGITUDE_MADEIRA - LEFT_MOST_LONGITUDE_MADEIRA);
    public static final double MADEIRA_SIZE_Y = Math.abs(TOP_MOST_LATITUDE_MADEIRA - BOTTOM_MOST_LATITUDE_MADEIRA);


    public static void main(String[] args) {
        ChunkBoard<String> portugal = new ChunkBoard<>(38, 26, PORTUGAL_SIZE_X, PORTUGAL_SIZE_Y, LEFT_MOST_LONGITUDE_CONTINENTE, BOTTOM_MOST_LATITUDE_CONTINENTE);
        ChunkBoard<String> madeira = new ChunkBoard<>(4, 6, MADEIRA_SIZE_X, MADEIRA_SIZE_Y, LEFT_MOST_LONGITUDE_MADEIRA, BOTTOM_MOST_LATITUDE_MADEIRA);

        System.out.println(portugal.isInside(0,0));
        System.out.println(portugal.isInside(26,0));
        System.out.println(portugal.isInside(-9.55,36.85));
        System.out.println(madeira.isInside(-9.55,36.85));

        LatLng[] points = new LatLng[4];

        /*{
            "lat": 42.03,
            "lng": -9.55
        },
        {
            "lat": 42.17,
            "lng": -9.55
        },
        {
            "lat": 42.17,
            "lng": -9.42
        },
        {
            "lat": 42.03,
            "lng": -9.42
        }*/

        points[0] = new LatLng((float) 42.03, (float) -9.54);
        points[1] = new LatLng((float) 42.169, (float) -9.54);
        points[2] = new LatLng((float) 42.169, (float) -9.42);
        points[3] = new LatLng((float) 42.03, (float) -9.42);


        /*points[0] = new LatLng((float) 39.37902, (float) -8.85768);
        points[1] = new LatLng((float) 39.85455, (float) -8.51985);
        points[2] = new LatLng((float) 39.67721, (float) -8.3578);
        points[3] = new LatLng((float) 40.13441, (float) -7.98427);
        points[4] = new LatLng((float) 39.19581, (float) -8.04469);*/

        List<Chunk2<String>> polygonResult = portugal.polygon(points);

        for (Chunk2<String> chunk: polygonResult) {
            chunk.setTag("P");
        }

        portugal.printChunks();
    }
}
