package pt.unl.fct.di.adc.silvanus.implementation;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import pt.unl.fct.di.adc.silvanus.data.parcel.Chunk;
import pt.unl.fct.di.adc.silvanus.data.parcel.Coordenada;
import pt.unl.fct.di.adc.silvanus.data.parcel.ParcelaData;
import pt.unl.fct.di.adc.silvanus.resources.ParcelaResource;
import pt.unl.fct.di.adc.silvanus.util.interfaces.Parcel;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

public class ParcelImplementation implements Parcel {

    private static final int NUMBER_OF_COLLUMNS_IN_CONTINENTE = 26;
    private static final int NUMBER_OF_LINES_IN_CONTINENTE = 38;

    private static final float RIGHT_MOST_LONGITUDE_CONTINENTE = -6.17f;
    private static final float TOP_MOST_LATITUDE_CONTINENTE = 42.17f;
    private static final float LEFT_MOST_LONGITUDE_CONTINENTE = -9.55f;
    private static final float BOTTOM_MOST_LATITUDE_CONTINENTE = 36.85f;

    private static final float FATOR_ADITIVO_LONGITUDE = 0.13f;
    private static final float FATOR_ADITIVO_LATITUDE = 0.14f;

    private static final float LEFT_MOST_LONGITUDE_GLBOAL = -31.28f; // Bounding Box Flores (Açores)
    private static final float BOTTOM_MOST_LATITUDE_GLOBAL = 32.62f; // Bounding Box Madeira
    public static final String PARCELAS_TO_BE_APPROVED_TABLE_NAME = "Parcelas To Be Approved";

    private Map<String, Chunk> chunksDasIlhas;

    private static final Logger LOG = Logger.getLogger(ParcelaResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private Polygon bigBBPolygon;

    private Polygon portugalContinentalPolygon;

    private GeometryFactory factory;


    public ParcelImplementation() {
        this.factory = new GeometryFactory();
        chunksDasIlhas = new HashMap();
        this.bigBBPolygon = gerarPolygon(LEFT_MOST_LONGITUDE_GLBOAL, RIGHT_MOST_LONGITUDE_CONTINENTE, TOP_MOST_LATITUDE_CONTINENTE, BOTTOM_MOST_LATITUDE_GLOBAL, factory);
        this.portugalContinentalPolygon = gerarPolygon(LEFT_MOST_LONGITUDE_CONTINENTE, RIGHT_MOST_LONGITUDE_CONTINENTE, TOP_MOST_LATITUDE_CONTINENTE, BOTTOM_MOST_LATITUDE_CONTINENTE, factory);
    }

    @Override
    public Result<Void> createParcel(ParcelaData dataParcela) {
        String banana = g.toJson(dataParcela.getParcela());
        Polygon parcela = coordenadasParaPolygon(dataParcela.getParcela());

        List<String> result = metodoCompleto(parcela, portugalContinentalPolygon, bigBBPolygon);

        if (result == null) {
            LOG.severe("Esta parcela nao esta bem criada.");
            return Result.error(Response.Status.NOT_ACCEPTABLE, "Esta parcela nao esta bem criada.");
        }

        String parcelID = dataParcela.getId_of_owner() + "/" + dataParcela.getName_of_terrain();
        Key parcelaKey = datastore.newKeyFactory().setKind(PARCELAS_TO_BE_APPROVED_TABLE_NAME).newKey(parcelID);

        Entity parcelaEntity = datastore.get(parcelaKey);
        if (parcelaEntity != null) {
            LOG.severe("Esta parcela ja existe.");
            return Result.error(Response.Status.FORBIDDEN, "Esta parcela ja existe.");
        }

        String coordenadasAsJson = g.toJson(dataParcela.getParcela());

        parcelaEntity = Entity.newBuilder(parcelaKey).set("coordenadas", coordenadasAsJson)
                .set("id_owner", dataParcela.getId_of_owner())
                .set("name_of_terrain", dataParcela.getName_of_terrain())
                .set("description_of_terrain", dataParcela.getDescription_of_terrain())
                .set("conselho_of_terrain", dataParcela.getConselho_of_terrain())
                .set("freguesia_of_concelho", dataParcela.getFreguesia_of_terrain())
                .set("section_of_terrain", dataParcela.getFreguesia_of_terrain())
                .set("section_of_terrain", dataParcela.getSection_of_terrain())
                .set("number_article_of_terrain", dataParcela.getNumber_article_terrain())
                .set("type_of_soil_coverage", dataParcela.getType_of_soil_coverage())
                .set("current_use_of_soil", dataParcela.getCurrent_use_of_soil())
                .set("previous_use_of_soil", dataParcela.getPrevious_use_of_soil())
                .set("chunks_of_parcela", g.toJson(result)).build();

        Transaction txn = datastore.newTransaction();
        try {

            txn.add(parcelaEntity);
            txn.commit();
            LOG.info("Parcela was registered.");
            return Result.ok();
        } finally {
            if (txn.isActive()) txn.rollback();
        }
    }

    /**
     * Este e o metodo que faz todas as verificacoes de uma parcela
     *
     * @param parcela             a parcela que esta a ser avaliada
     * @param portugalContinental a Bounding Box de Portugal continentel
     * @param bibBB               a Bounding Box que e composta pelos extremos da latitude e da longitude
     * @return uma string com os chunks onde a parcela esta caso nao haja falhas, null caso contrario
     */
    private List<String> metodoCompleto(Polygon parcela, Polygon portugalContinental, Polygon bibBB) {

        // Verifica se a parcela esta dentro da Big Bounding Box
        if (!bibBB.contains(parcela)) return null;

        List<String> res = new ArrayList<>();
        // Esta numa ilha ou nao, se esta, se esta completamente dentro
        preencherMapaDasIlhas();
        String result = parcelaEstaNasIlhas(parcela);
        if (result == null) return null;
        else if (!result.equals("")) {
            res.add(result);
            return res;
        }

        // Verificar se esta no continente e se esta, onde esta
        List<String> resultContinente = parcelaEstaNoContinente(parcela, portugalContinental);
        return resultContinente;
    }


    /**
     * Este metodo recebe os valores da latitude e longitude de uma chunk e gera um objeto Polygon
     *
     * @param leftLon   longitude esquerda do chunk (menor valor da longitude)
     * @param rightLon  longitude direita do chunk (maior valor da longitude)
     * @param topLat    latitude superior do chunk (maior valor da latitude)
     * @param bottomLat latitude inferior do chunk (menor valor da latitude)
     * @param factory   objeto usado para criar o Polygon
     * @return o objeto Polygon que representa um chunk
     */
    private Polygon gerarPolygon(float leftLon, float rightLon, float topLat, float bottomLat, GeometryFactory factory) {
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(bottomLat, leftLon);
        coordinates[1] = new Coordinate(topLat, leftLon);
        coordinates[2] = new Coordinate(topLat, rightLon);
        coordinates[3] = new Coordinate(bottomLat, rightLon);
        coordinates[4] = new Coordinate(coordinates[0].getX(), coordinates[0].getY());
        Polygon polygon = factory.createPolygon(coordinates);
        return polygon;

    }

    /**
     * Este metodo verifica se uma parcela interseta alguma das ilhas. Caso intersete verifica se a parcela esta completamente contida na ilha.
     *
     * @param parcela parcela que queremos saber
     * @return "" (string vazia) caso a parcela nao intersete nenhum ilha, null caso a parcela intersete uma ilha mas nao esteja contida, o nome da ilha caso contrario
     */
    private String parcelaEstaNasIlhas(Polygon parcela) {
        for (Map.Entry<String, Chunk> entry : chunksDasIlhas.entrySet()) {
            if (entry.getValue().getChunkAsPolygon().intersects(parcela)) {
                if (entry.getValue().getChunkAsPolygon().contains(parcela))
                    return entry.getKey();
                else
                    return null;
            }
        }
        return "";
    }

    /**
     * Este metodo serve para verificar se uma parcela esta dentro da Bounding Box de Portugal Continental (doravante denominada BBP) e caso esteja em que chunk(s) esta.
     * <p>Primeiro fazemos a verificamos se a parcela esta completamente contida na BBP, caso nao esteja returnamos null.</p>
     * Apos verificar que a parcela esta completamente dentro da BBP, e necessario verificar a que chunks pertence.
     * Para isso usamos o primeiro ponto da parcela (o primeiro ponto a ser inserido pelo utilizador aquando da criacao da parcela) e descobrimos em que chunk o mesmo esta.
     * <p>Apos descobrir o chunk, verificamos se a parcela esta completamente contida no mesmo, caso esteja devolvemos uma String que contem o indice desse chunk (linha,coluna).</p>
     * Caso a parcela nao esteja completamente contida num determinado chunk, vamos verificar em quais dos chunks adjacentes ao inicial a parcela ha uma intersecao com a parcela e juntamos a uma lista.
     *
     * @param parcela  a parcela que queremos verificar
     * @param portugal a Bounding Box de Portugal continental
     * @return null caso haja alguma falha, uma lista que contem os chunks em que a parcela este representados como String
     */
    private List<String> parcelaEstaNoContinente(Polygon parcela, Polygon portugal) {
        if (!portugal.contains(parcela)) return null;

        // ----- Descobrir em que chunk esta o primeiro ponto ----- \\
        int linhaPrimeiroPonto = 0;
        int colunaPrimeiroPonto = 0;
        Coordinate primeiroPonto = parcela.getCoordinates()[0];
        for (int i = 1; i <= NUMBER_OF_LINES_IN_CONTINENTE; i++) { // Estamos a percorrer as colunas de portugal
            double leftLongitude = LEFT_MOST_LONGITUDE_CONTINENTE + (FATOR_ADITIVO_LONGITUDE * (i - 1));
            double rightLongitude = LEFT_MOST_LONGITUDE_CONTINENTE + (FATOR_ADITIVO_LONGITUDE * i);
            if (primeiroPonto.getY() > leftLongitude && primeiroPonto.getY() <= rightLongitude) {
                colunaPrimeiroPonto = i;
                break;
            }

        }
        for (int j = 1; j <= NUMBER_OF_COLLUMNS_IN_CONTINENTE; j++) { // Estamos a percorrer as linhas de Portugal
            double topLatitude = TOP_MOST_LATITUDE_CONTINENTE - (FATOR_ADITIVO_LATITUDE * (j - 1));
            double bottomLatitude = TOP_MOST_LATITUDE_CONTINENTE - (FATOR_ADITIVO_LATITUDE * (j));
            if (primeiroPonto.getX() < topLatitude && primeiroPonto.getX() >= bottomLatitude) {
                linhaPrimeiroPonto = j;
                break;
            }
        }
        // ----- Descobrir em que chunk esta o primeiro ponto ----- \\

        List<String> list = new ArrayList<>(); // Lista que vai conter os chunks em que a parcela esta

        // ----- Verificar se a parcela esta completamente dentro do chunk ----- \\
        Polygon chunkPrimeiroPonto = chunkDadosLinhaColuna(linhaPrimeiroPonto, colunaPrimeiroPonto);
        list.add(String.format("%d,%d", linhaPrimeiroPonto, colunaPrimeiroPonto));
        if (parcela.within(chunkPrimeiroPonto)) {
            return list;
        }
        // ----- Verificar se a parcela esta completamente dentro do chunk ----- \\

        // ----- Como não esta completamente so num chunk, quais dos chunks a volta e que a parcela interseta ----- \\
        List<String> chunksAdjacentes = gerarLinhasEColunasAVoltaDeUmChunk(linhaPrimeiroPonto, colunaPrimeiroPonto);
        for (String tmpString : chunksAdjacentes) {
            int latitude = Integer.parseInt(tmpString.split(",")[0]);
            int longitude = Integer.parseInt(tmpString.split(",")[1]);
            Polygon tmPolygon = chunkDadosLinhaColuna(latitude, longitude);
            if (tmPolygon.intersects(parcela))
                list.add(String.format("%d,%d", latitude, longitude));
        }
        // ----- Como não esta completamente so num chunk, quais dos chunks a volta e que a parcela interseta ----- \\

        return list;
    }

    /**
     * Este metodo serve para gerar quais os chunks adjacentes a um dado chunk identificado pelo par (nLinha,nColuna). Este metodo so entra em jogo quando estamos a verificar Portugal
     * continental. Como nos dividimos Portugal continental em 26 linhas e 38 colunas os chunks adjacentes variam entre ( [nLinha - 1 ; nLinha +1] , [nColuna - 1 ; nColuna + 1] ). Caso
     * algum dos valores caia fora da limitacoes, 1 > nLinha ou nLinha > 26 ou 1 > nColuna ou nColuna > 38, usamos os valores que ultrapassaram como barreira.
     *
     * @param nLinha  numero da linha do chunk a gerar as adjacencias
     * @param nColuna numero da coluna do chunk a gerar as adjacencias
     * @return uma lista com os indices dos chunks adjacentes
     */
    private List<String> gerarLinhasEColunasAVoltaDeUmChunk(int nLinha, int nColuna) {
        List<String> newList = new ArrayList<>();

        int upperBound = nLinha - 1 >= 1 ? nLinha - 1 : nLinha;

        int lowerBound = nLinha + 1 <= NUMBER_OF_LINES_IN_CONTINENTE ? nLinha + 1 : nLinha;

        int leftBound = nColuna - 1 >= 1 ? nColuna - 1 : nColuna;

        int rightBound = nColuna + 1 <= NUMBER_OF_COLLUMNS_IN_CONTINENTE ? nColuna + 1 : nColuna;

        for (int i = upperBound; i <= lowerBound; i++) { // Linha
            for (int j = leftBound; j <= rightBound; j++) { // Coluna
                if (!(nLinha == i && nColuna == j)) newList.add(i + "," + j);
            }
        }
        return newList;

    }

    /**
     * Dados um par (nLinha,nColuna) gera o chunk (Polygon) respetivo
     *
     * @param nLinha  numero da linha do chunk
     * @param nColuna numero da coluna do chunk
     * @return o Polygon que representa o chunk
     */
    private Polygon chunkDadosLinhaColuna(int nLinha, int nColuna) {
        GeometryFactory factory = new GeometryFactory();

        float topLatitude = TOP_MOST_LATITUDE_CONTINENTE - (FATOR_ADITIVO_LATITUDE * (nLinha - 1));
        float bottomLatitude = TOP_MOST_LATITUDE_CONTINENTE - (FATOR_ADITIVO_LATITUDE * nLinha);

        float leftLongitude = LEFT_MOST_LONGITUDE_CONTINENTE + (FATOR_ADITIVO_LONGITUDE * (nColuna - 1));
        float rightLongitude = LEFT_MOST_LONGITUDE_CONTINENTE + (FATOR_ADITIVO_LONGITUDE * nColuna);

        Polygon polygon = gerarPolygon(leftLongitude, rightLongitude, topLatitude, bottomLatitude, factory);

        return polygon;
    }

    /**
     * Este metodo preenche um mapa com as Bounding Boxes das ilhas. O mapa e do tipo Map(String, Chunk) sendo a String o ID do chunk, e Chunk o chunk
     * que representa a Bounding Box da ilha em questao.
     */
    private void preencherMapaDasIlhas() {
        chunksDasIlhas.clear();
        // ----- Açores -----
        chunksDasIlhas.put("São Miguel (Açores)", new Chunk("São Miguel (Açores)", -25.90f, -25.10f, 37.95f, 37.65f));
        chunksDasIlhas.put("Santa Maria (Açores)", new Chunk("Santa Maria (Açores)", -25.20f, -25.00f, 37.02f, 36.92f));
        chunksDasIlhas.put("Terceira (Açores)", new Chunk("Terceira (Açores)", -27.39f, -27.30f, 38.82f, 36.81f));
        chunksDasIlhas.put("Graciosa (Açores)", new Chunk("Graciosa (Açores)", -28.08f, -27.93f, 39.10f, 39.00f));
        chunksDasIlhas.put("São Jorge (Açores)", new Chunk("São Jorge (Açores)", -28.32f, -27.70f, 38.76f, 38.52f));
        chunksDasIlhas.put("Pico (Açores)", new Chunk("Pico (Açores)", -28.55f, -28.02f, 38.57f, 38.38f));
        chunksDasIlhas.put("Flores (Açores)", new Chunk("Flores (Açores)", -31.28f, -31.12f, 39.53f, 39.36f));
        chunksDasIlhas.put("Corvo (Açores)", new Chunk("Corvo (Açores)", -31.13f, -31.08f, 39.73f, 39.66f));
        // ----- Madeira -----
        chunksDasIlhas.put("Porto Santo (Madeira)", new Chunk("Porto Santo (Madeira)", -16.42f, -16.27f, 33.11f, 32.99f));
        chunksDasIlhas.put("Madeira", new Chunk("Madeira (Madeira)", -17.27f, -16.64f, 32.88f, 32.62f));
    }

    /**
     * Dado um array de Coordenadas converte as mesmas num Polygon
     *
     * @param coordenadas coordenadas que compoem uma parcela
     * @return o objeto Polygon que representa uma parcela
     */
    private Polygon coordenadasParaPolygon(Coordenada[] coordenadas) {
        Coordinate[] coordinates = new Coordinate[coordenadas.length + 1];
        for (int i = 0; i < coordenadas.length; i++) {
            coordinates[i] = new Coordinate(coordenadas[i].getLat(), coordenadas[i].getLon());
        }
        coordinates[coordinates.length - 1] = new Coordinate(coordinates[0].getX(), coordinates[0].getY());
        return factory.createPolygon(coordinates);
    }

    /**
     * Insert praise the sun meme
     */
    @Override
    public void quuéééééééééééééééééééééériiiiiiiiiisssssssssss(Coordenada[] parcela) {
        Polygon polygonParcela = coordenadasParaPolygon(parcela);
        LOG.fine("Query was started.");
        Query<Entity> query;
        QueryResults<Entity> results;
        query = Query.newEntityQueryBuilder().setKind(PARCELAS_TO_BE_APPROVED_TABLE_NAME).build();
        results = datastore.run(query);
        while (results.hasNext()) {
            Entity tmp = results.next();
            String coordenadas = tmp.getString("coordenadas");
            Coordenada[] c = g.fromJson(coordenadas, Coordenada[].class);
            Polygon p = coordenadasParaPolygon(c);
            if (p.intersects(polygonParcela)) {
                LOG.warning("Existe uma interseccao com a parcela: \"" + tmp.getString("id_owner") + "/" + tmp.getString("name_of_terrain") + "\"");
            }

        }
    }
}
