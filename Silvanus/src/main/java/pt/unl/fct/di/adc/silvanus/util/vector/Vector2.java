package pt.unl.fct.di.adc.silvanus.util.vector;

public class Vector2<V> extends Vector<V>{

    public Vector2(V x, V y) {
        super(x, y);
    }

    public V getX(){
        return this.get(0);
    }

    public V getY(){
        return this.get(1);
    }
}
