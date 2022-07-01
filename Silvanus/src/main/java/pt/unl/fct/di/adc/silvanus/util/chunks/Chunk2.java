package pt.unl.fct.di.adc.silvanus.util.chunks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chunk2<C> {

    private int x;
    private int y;

    private List<C> content;
    private String tag;

    public Chunk2(int x, int y){
        this.x = x;
        this.y = y;
        this.content = new ArrayList<>();
        this.tag = "";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<C> getContent() {
        return content;
    }

    @SafeVarargs
    public final void addContent(C... newContent){
        this.content.addAll(Arrays.asList(newContent));
    }

    @SafeVarargs
    public final void putContent(C... newContent){
        this.content.clear();
        this.addContent(newContent);
    }

    public boolean hasContent(){
        return this.content != null && !this.content.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("(%d, %d): %s", this.getX(), this.getY(), this.getContent());
    }

    @SafeVarargs
    public final void removeContent(C... content){
        this.content.removeAll(Arrays.asList(content));
    }

    public void clearContent() {
        this.content.clear();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag){
        this.tag = tag;
    }
}
