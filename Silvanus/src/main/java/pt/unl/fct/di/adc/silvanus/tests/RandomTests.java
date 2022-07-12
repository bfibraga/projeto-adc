package pt.unl.fct.di.adc.silvanus.tests;

import pt.unl.fct.di.adc.silvanus.util.Random;

public class RandomTests {
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            String result = Random.code();
            System.out.println(i + " -> " + result + " " + (result.length() == 8));
        }
    }
}
