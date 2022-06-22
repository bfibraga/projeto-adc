package pt.unl.fct.di.adc.silvanus.data.parcel;

import java.util.*;

public class TerrainInfoData {
    private String description;
    private String type_of_soil_coverage;
    private String current_use; // TODO verificar se pode ser um ENUM {vazio, habitacao, cultivo, ... }
    private String previous_use; // TODO verificar se pode ser um ENUM {vazio, habitacao, cultivo, ... }
    private Set<String> images;
    private List<LatLng> route;

    public TerrainInfoData(
            String description,
            String type_of_soil_coverage,
            String current_use,
            String previous_use,
            String[] images,
            LatLng[] route
    ){
        this.description = description;
        this.type_of_soil_coverage = type_of_soil_coverage;
        this.current_use = current_use;
        this.previous_use = previous_use;
        this.images = new HashSet<>();
        Collections.addAll(this.images, images);
        this.route = new ArrayList<>();
        Collections.addAll(this.route, route);
    }

    public TerrainInfoData(){
        this("","","","",new String[]{}, new LatLng[]{});
    }

    public String getDescription() {
        return description;
    }

    public String getType_of_soil_coverage() {
        return type_of_soil_coverage;
    }

    public String getCurrent_use() {
        return current_use;
    }

    public String getPrevious_use() {
        return previous_use;
    }

    public Set<String> getImages() {
        return images;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    @Override
    public String toString() {
        return String.format("%s:\n\tDescription:%s\n\tType of Soil:%s\n\tCurrent Use:%s\n\tPrevious Use:%s\n\tImages:%s\n\tRoute:%s\n", this.getClass().getName(), getDescription(), getType_of_soil_coverage(), getCurrent_use(), getPrevious_use(),getImages(),getRoute());
    }
}
