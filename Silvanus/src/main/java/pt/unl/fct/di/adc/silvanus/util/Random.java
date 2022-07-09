package pt.unl.fct.di.adc.silvanus.util;

public class Random {
    public static String color(){
        java.util.Random random = new java.util.Random();
        return String.format("#%06x", random.nextInt(0xffffff + 1));
    }

    public static String code(){
        java.util.Random random = new java.util.Random();
        String result = String.format("CV%d", random.nextInt(1000000));
        while (result.length() < 8){
            result = result.concat(String.valueOf(random.nextInt(10)));
        }
        return result;
    }
}
