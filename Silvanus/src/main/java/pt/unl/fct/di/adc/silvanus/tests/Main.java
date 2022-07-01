package pt.unl.fct.di.adc.silvanus.tests;

import com.beust.ah.A;
import pt.unl.fct.di.adc.silvanus.util.chunks.Chunk2;
import pt.unl.fct.di.adc.silvanus.util.chunks.ChunkBoard;

import java.util.ArrayList;
import java.util.Arrays;
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

        portugal.put(0,0, "Top Left");
        System.out.println(portugal.get(0,0));
        System.out.println(portugal.get(-9.55, 36.85));

        portugal.printChunks();

        portugal.put(-9.0, 42.0, "A point");

        portugal.add(-6.2, 36.9, "Another point");

        portugal.printChunks();

        Chunk2<String>[][] selectedArea = portugal.getArea(new double[]{ -9.0, 42}, new double[]{ -8, 40.0});
        for (int y = 0; y < selectedArea[0].length ; y++){
            for (int x = 0 ; x < selectedArea.length ; x++){
                selectedArea[x][y].addContent("(" + x + ", " + y + ")");
            }
        }

        portugal.printChunks();

        System.out.println(portugal.isInside(0,0));
        System.out.println(portugal.isInside(26,0));
        System.out.println(portugal.isInside(-9.55,36.85));
        System.out.println(madeira.isInside(-9.55,36.85));

        int[][] points = {
                {10,10},
                {15,20},
                {20,17},
                {14,16},
                {12,10}
        };

        int[] vec1 = new int[]{points[1][0] - points[0][0], points[1][1] - points[0][1]};
        int[] vec2 = new int[]{points[2][0] - points[1][0], points[2][1] - points[1][1]};
        int [] result = new int[]{vec1[0]+vec2[0], vec1[1]+vec2[1]};
        System.out.println(Arrays.toString(result));
        double dia1 = portugal.DiamondAngle(vec1[0], vec1[1]);
        double dia2 = portugal.DiamondAngle(vec2[0], vec2[1]);
        System.out.println((dia1-dia2));
        System.out.println(portugal.DiamondAngleToDegree((dia1-dia2)));


        List<Chunk2<String>> line = new ArrayList<>();
        for (int i = 0 ; i < points.length-1 ; i++) {
            line.addAll(portugal.line(points[i][0], points[i][1], points[i+1][0], points[i+1][1]));
        }
        line.addAll(portugal.line(points[points.length-1][0], points[points.length-1][1], points[0][0], points[0][1]));
        for (Chunk2<String> chunk: line) {
            chunk.addContent("Line");
        }

        //TODO Maybe apply Fill Area algorithm
        //portugal.fill(12,12,"Fill");

        portugal.printChunks();
    }
}
