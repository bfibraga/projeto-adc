package pt.unl.fct.di.adc.silvanus.util.vector;

public abstract class Vector<V> {

    private V[] components;

    @SafeVarargs
    public Vector(V... components){
        this.components = components;
    }

    public V get(int component){
        return this.components[component];
    }

    public Vector<V> put(V element, int component){
        components[component] = element;
        return this;
    }
}
