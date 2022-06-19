package pt.unl.fct.di.adc.silvanus.data.parcel;

public class TerrainData {

    // --- Pontos do mapa, area e o centro da parcela ---
    private LatLng[] parcela;
    // --- Pontos do mapa, area e o centro da parcela ---

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
    private String current_use_of_soil; // TODO verificar se pode ser um ENUM {vazio, habitacao, cultivo, ... }
    private String previous_use_of_soil; // TODO verificar se pode ser um ENUM {vazio, habitacao, cultivo, ... }
    // --- Informacoes Relacionadas com o solo ---

    public TerrainData() {

    }

    public TerrainData(LatLng[] parcela, String id_of_owner, String name_of_terrain, String description_of_terrain,
                       String conselho_of_terrain, String distrito_of_terrain, String section_of_terrain,
                       String number_article_terrain, String type_of_soil_coverage, String current_use_of_soil, String previous_use_of_soil) {
        this.parcela = parcela;
        this.id_of_owner = id_of_owner;
        this.name_of_terrain = name_of_terrain;
        this.description_of_terrain = description_of_terrain;
        this.conselho_of_terrain = conselho_of_terrain;
        this.distrito_of_terrain = distrito_of_terrain;
        this.section_of_terrain = section_of_terrain;
        this.number_article_terrain = number_article_terrain;
        this.type_of_soil_coverage = type_of_soil_coverage;
        this.current_use_of_soil = current_use_of_soil;
        this.previous_use_of_soil = previous_use_of_soil;
    }

    public LatLng[] getParcela() {
        return parcela;
    }

    public String getId_of_owner() {
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
    }

}
