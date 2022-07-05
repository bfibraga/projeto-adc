package pt.unl.fct.di.adc.silvanus.util;

public class Random {
    public static String color(){
        java.util.Random random = new java.util.Random();
        return String.format("#%06x", random.nextInt(0xffffff + 1));
    }
}
