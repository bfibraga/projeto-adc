package pt.unl.fct.di.adc.silvanus.util.vector;

public class Vector3<V> extends Vector<V>{

    public Vector3(V x, V y, V z){
        super(x, y, z);
    }

    public V getX(){
        return this.get(0);
    }

    public V getY(){
        return this.get(1);
    }

    public V getZ(){
        return this.get(2);
    }
}
