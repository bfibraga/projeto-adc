package pt.unl.fct.di.adc.silvanus.data.terrain;

import pt.unl.fct.di.adc.silvanus.util.PolygonUtils;

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

    private float[] edgesTerrain; //leftmost, rightmost, topmost, bottom-most point

    private float leftMost;
    private float rightMost;
    private float topMost;
    private float bottomMost;

    /*
    // --- Informacao do utilizador ou que este insere ---
    private String id_of_owner;
    private String name_of_terrain;}
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
        this.edgesTerrain = new float[4];
        edgesTerrain[0] = Float.NEGATIVE_INFINITY; // The more a point is to the left, the more negative it is
        edgesTerrain[1] = Float.POSITIVE_INFINITY; // The more a point is to the right, the more positive it is
        edgesTerrain[2] = Float.POSITIVE_INFINITY; // The upper a point is, the more positive it is
        edgesTerrain[3] = Float.NEGATIVE_INFINITY; // The lower a point is, the more negative it is

        this.center = PolygonUtils.centroid(parcela);
    }

    public void createEdges() {
        for (LatLng point : parcela) {
            if (point.getLng() > edgesTerrain[0])
                edgesTerrain[0] = point.getLng();

            if (point.getLng() < edgesTerrain[1])
                edgesTerrain[1] = point.getLng();

            if (point.getLat() < edgesTerrain[2])
                edgesTerrain[2] = point.getLat();

            if (point.getLat() > edgesTerrain[3])
                edgesTerrain[3] = point.getLat();
        }
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

    public String getID() {
        return String.format("%s:%s", this.credentials.getID(), this.owner.getNif());
    }

    public LatLng getCenter() {
        return center;
    }

    public float[] getEdgesTerrain() {
        return edgesTerrain;
    }

    public boolean validation(){
        boolean pointsValid = this.getParcela().length > 0;
        boolean credentialsValid = this.getCredentials().validation();
        boolean ownerValid = this.owner.validation();

        return pointsValid && credentialsValid && ownerValid;
    }

    @Override
    public String toString() {
        return String.format("TerrainData:\n\tParcela:%s\n\tCredentials:%s\n\tOwner:%s\n\tInfo:%s\n", Arrays.toString(parcela), credentials, owner, info);
    }
}
