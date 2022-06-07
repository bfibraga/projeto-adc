package pt.unl.fct.di.adc.silvanus.util;

public class Pair<T> {

    private T value1;
    private T value2;

    public Pair() {

    }

    public Pair(T value1, T value2){
        this.value1 = value1;
        this.value2 = value2;
    }

    public T getValue1(){
        return this.value1;
    }

    public T getValue2(){
        return this.value2;
    }

    @Override
    public String toString() {
        return "(" + value1.toString() + ", " + value2.toString() + ")";
    }
}
