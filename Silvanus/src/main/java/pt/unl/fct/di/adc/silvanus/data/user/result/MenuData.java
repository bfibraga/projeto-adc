package pt.unl.fct.di.adc.silvanus.data.user.result;

public class MenuData {

    private String id;
    private int order;

    public MenuData(){
        this("0", 0);
    }

    public MenuData(String id, int order){
        this.id = id;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }
}
