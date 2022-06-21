package pt.unl.fct.di.adc.silvanus.data.parcel;

import pt.unl.fct.di.adc.silvanus.data.user.UserInfoData;

import java.util.Arrays;

//TODO Rethink this part
public class TerrainData {

    // --- Pontos do mapa, area e o centro da parcela ---
    private LatLng[] parcela;
    // --- Pontos do mapa, area e o centro da parcela ---
    private LatLng center;
    private TerrainIdentifierData credentials;
    private TerrainOwner owner;
    private TerrainInfoData info;

    /*
    // --- Informacao do utilizador ou que este insere ---
    private String id_of_owner;
    private String name_of_terrain;
    private String description_of_terrain;
    // --- Informacao do utilizador ou que este insere ---

    // --- Identificacao do Cadastro ---
    private String conselho_of_terrain;
    private String distrito_of_terrain;
    private String section_of_terrain;
    private String number_article_terrain;
    // --- Identificacao do Cadastro ---

    // --- Informacoes Relacionadas com o solo ---
    private String type_of_soil_coverage;
    private String current_use_of_soil; //TODO verificar se pode ser um ENUM {vazio, habitacao, cultivo, ... }
    private String previous_use_of_soil; //TODO verificar se pode ser um ENUM {vazio, habitacao, cultivo, ... }
    // --- Informacoes Relacionadas com o solo ---

    */
    public TerrainData() {
        this(new LatLng[]{}, new TerrainIdentifierData(), new TerrainOwner(), new TerrainInfoData());
    }

    public TerrainData(LatLng[] parcela, TerrainIdentifierData credentials, TerrainOwner owner, TerrainInfoData info) {
        this.parcela = parcela;
        this.credentials = credentials;
        this.owner = owner;
        this.info = info;
        //TODO Calculate centroid of this terrain

        int points = parcela.length;
        float[] center_value = new float[2];
        for (LatLng point: parcela) {
            center_value[0] += point.getLat()/points;
            center_value[1] += point.getLng()/points;
        }
        this.center = new LatLng(center_value[0], center_value[1]);
    }

    public LatLng[] getParcela() {
        return parcela;
    }

    public TerrainIdentifierData getCredentials() {
        return credentials;
    }

    public TerrainOwner getOwner() {
        return owner;
    }

    public TerrainInfoData getInfo() {
        return info;
    }

    /*public String getId_of_owner() {
        return id_of_owner;
    }

    public String getName_of_terrain() {
        return name_of_terrain;
    }

    public String getDescription_of_terrain() {
        return description_of_terrain;
    }

    public String getConselho_of_terrain() {
        return conselho_of_terrain;
    }

    public String getDistrito_of_terrain() {
        return distrito_of_terrain;
    }

    public String getSection_of_terrain() {
        return section_of_terrain;
    }

    public String getNumber_article_terrain() {
        return number_article_terrain;
    }

    public String getType_of_soil_coverage() {
        return type_of_soil_coverage;
    }

    public String getCurrent_use_of_soil() {
        return current_use_of_soil;
    }

    public String getPrevious_use_of_soil() {
        return previous_use_of_soil;
    }*/

    public String getID(){
        return String.format("%s:%s", this.credentials.getID(), this.owner.getNif());
    }

    @Override
    public String toString() {
        return String.format("TerrainData:\n\tParcela:%s\n\tCredentials:%s\n\tOwner:%s\n\tInfo:%s\n", Arrays.toString(parcela), credentials, owner, info);
    }
}
