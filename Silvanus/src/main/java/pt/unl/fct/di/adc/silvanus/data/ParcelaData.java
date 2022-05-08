package pt.unl.fct.di.adc.silvanus.data;

public class ParcelaData {

	// --- Pontos do mapa, area e o centro da parcela ---
	// private Polygon parcela;
	private String parcela;
	// private double[] coordinates;
	// --- Pontos do mapa, area e o centro da parcela ---

	// --- Informacao do utilizador ou que este insere ---
	private String id_of_owner;
	private String name_of_terrain;
	private String description_of_terrain;
	// --- Informacao do utilizador ou que este insere ---

	// --- Identificacao do Cadastro ---
	private String conselho_of_terrain;
	private String freguesia_of_terrain;
	private String section_of_terrain;
	private String number_article_terrain;
	private String verification_document_of_terrain; // TODO mudar isto para poder levar um ficheiro
	// --- Identificacao do Cadastro ---

	// --- Informacoes Relacionadas com o solo ---
	private String type_of_soil_coverage;
	private String current_use_of_soil; // TODO verificar se pode ser um ENUM {vazio, habitacao, cultivo, ... }
	private String previous_use_of_soil; // TODO verificar se pode ser um ENUM {vazio, habitacao, cultivo, ... }
	// --- Informacoes Relacionadas com o solo ---

	public ParcelaData() {

	}

	public ParcelaData(String pointInMaps, String id_of_owner, String name_of_terrain, String description_of_terrain,
			String conselho_of_terrain, String freguesia_of_terrain, String section_of_terrain,
			String number_article_terrain, String verification_document_of_terrain, String type_of_soil_coverage,
			String current_use_of_soil, String previous_use_of_soil) {
		// setPointsInMap(pointInMaps);
		//
		this.parcela = pointInMaps;
		//
		this.id_of_owner = id_of_owner;
		this.name_of_terrain = name_of_terrain;
		this.description_of_terrain = description_of_terrain;
		this.conselho_of_terrain = conselho_of_terrain;
		this.freguesia_of_terrain = freguesia_of_terrain;
		this.section_of_terrain = section_of_terrain;
		this.number_article_terrain = number_article_terrain;
		this.verification_document_of_terrain = verification_document_of_terrain;
		this.type_of_soil_coverage = type_of_soil_coverage;
		this.current_use_of_soil = current_use_of_soil;
		this.previous_use_of_soil = previous_use_of_soil;

	}

	/**
	 * 
	 * @return the points in the map
	 */
	public String getParcela() {
		return parcela;
	}

	/**
	 * @return the id_of_owner
	 */
	public String getId_of_owner() {
		return id_of_owner;
	}

	/**
	 * @return the name_of_terrain
	 */
	public String getName_of_terrain() {
		return name_of_terrain;
	}

	/**
	 * @return the description_of_terrain
	 */
	public String getDescription_of_terrain() {
		return description_of_terrain;
	}

	/**
	 * @return the conselho_of_terrain
	 */
	public String getConselho_of_terrain() {
		return conselho_of_terrain;
	}

	/**
	 * @return the freguesia_of_terrain
	 */
	public String getFreguesia_of_terrain() {
		return freguesia_of_terrain;
	}

	/**
	 * @return the section_of_terrain
	 */
	public String getSection_of_terrain() {
		return section_of_terrain;
	}

	/**
	 * @return the number_article_terrain
	 */
	public String getNumber_article_terrain() {
		return number_article_terrain;
	}

	/**
	 * @return the verification_document_of_terrain
	 */
	public String getVerification_document_of_terrain() {
		return verification_document_of_terrain;
	}

	/**
	 * @return the type_of_soil_coverage
	 */
	public String getType_of_soil_coverage() {
		return type_of_soil_coverage;
	}

	/**
	 * @return the current_use_of_soil
	 */
	public String getCurrent_use_of_soil() {
		return current_use_of_soil;
	}

	/**
	 * @return the previous_use_of_soil
	 */
	public String getPrevious_use_of_soil() {
		return previous_use_of_soil;
	}

	/**
	 * @return the coordinates
	 * 
	 *         public double[] getCoordinates() { return coordinates; }
	 * 
	 *         private void setPointsInMap(String points) { Coordinate[] coordinates
	 *         = new Coordinate[points.length]; for (int i = 0; i < points.length; i
	 *         += 2) { coordinates[i] = new Coordinate(points[i], points[i + 1]); }
	 *         coordinates[points.length - 1] = new Coordinate(points[0],
	 *         points[1]); GeometryFactory factory = new GeometryFactory();
	 *         this.parcela = factory.createPolygon(coordinates); }
	 */

}
