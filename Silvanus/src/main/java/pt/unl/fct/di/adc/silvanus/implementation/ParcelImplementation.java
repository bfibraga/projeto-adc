package pt.unl.fct.di.adc.silvanus.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import pt.unl.fct.di.adc.silvanus.data.parcel.Chunk;
import pt.unl.fct.di.adc.silvanus.data.parcel.ParcelaData;
import pt.unl.fct.di.adc.silvanus.resources.ParcelaResource;
import pt.unl.fct.di.adc.silvanus.util.interfaces.Parcel;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        LOG.fine("\n\n\n" + banana + "\n\n\n");
        //LOG.fine("Attempt to register parcela by user: " + dataParcela.getId_of_owner());

       // LOG.fine("\n\n"+dataParcela.getParcela().toString() + "\n\n");


        return Result.ok();
                /*

        String result = metodoCompleto(factory, ,portugalContinentalPolygon, bigBBPolygon);

        String parcelID = dataParcela.getId_of_owner() + "/" + dataParcela.getName_of_terrain();
        Key parcelaKey = datastore.newKeyFactory().setKind("Parcela").newKey(parcelID);

        Entity parcelaEntity = datastore.get(parcelaKey);
        if (parcelaEntity != null) return Result.error(Status.FORBIDDEN, "Already exists this parcel");

        Transaction txn = datastore.newTransaction();
        try {
            parcelaEntity = Entity.newBuilder(parcelaKey).set("polygon", dataParcela.getParcela()).set("id_owner", dataParcela.getId_of_owner()).set("name_of_terrain", dataParcela.getName_of_terrain()).set("description_of_terrain", dataParcela.getDescription_of_terrain()).set("conselho_of_terrain", dataParcela.getConselho_of_terrain()).set("freguesia_of_concelho", dataParcela.getFreguesia_of_terrain()).set("section_of_terrain", dataParcela.getFreguesia_of_terrain()).set("section_of_terrain", dataParcela.getSection_of_terrain()).set("number_article_of_terrain", dataParcela.getNumber_article_terrain()).set("verification_document_of_terrain", dataParcela.getVerification_document_of_terrain()).set("type_of_soil_coverage", dataParcela.getType_of_soil_coverage()).set("current_use_of_soil", dataParcela.getCurrent_use_of_soil()).set("previous_use_of_soil", dataParcela.getPrevious_use_of_soil()).build();
            txn.add(parcelaEntity);
            txn.commit();
            LOG.info("Parcela was registered.");
            return Result.ok();
        } finally {
            if (txn.isActive()) txn.rollback();
        }
        */
    }
/*
    private String metodoCompleto(GeometryFactory factory, Polygon parcela, Polygon portugalContinental, Polygon bibBB) {

        // Verifica se a parcela esta dentro da Big Bounding Box
        if (!bibBB.contains(parcela)) return null;


        // Esta numa ilha ou nao, se esta, se esta completamente dentro
        preencherMapaDasIlhas();
        String result = parcelaEstaNasIlhas(parcela);
        if (result == null) return null;
        else if (!result.equals("")) return result;

        // Verificar se esta no continente e se esta, onde esta
        String resultContinente = parcelaEstaNoContinente(parcela, portugalContinental);
        return resultContinente;
    }

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

    private String parcelaEstaNasIlhas(Polygon parcela) {
        for (Map.Entry<String, Chunk> entry : chunksDasIlhas.entrySet()) {
            if (entry.getValue().getChunkAsPolygon().intersects(parcela)) {
                if (entry.getValue().getChunkAsPolygon().contains(parcela)) return entry.getKey();
                else return null;
            }
        }
        return "";
    }
/*
    private String parcelaEstaNoContinente(Polygon parcela, Polygon portugal) {
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

        // ----- Verificar se a parcela esta completamente dentro do chunk ----- \\
        Polygon chunkPrimeiroPonto = chunkDadosLinhaColuna(linhaPrimeiroPonto, colunaPrimeiroPonto);
        if (parcela.within(chunkPrimeiroPonto)) return String.format("%d,%d", linhaPrimeiroPonto, colunaPrimeiroPonto);
        // ----- Verificar se a parcela esta completamente dentro do chunk ----- \\

        // ----- Como não esta completamente so num chunk, quais dos chunks a volta e que a parcela interseta ----- \\
        List<Vector2<Integer, Integer>> list = new ArrayList<Vector2<Integer, Integer>>();
        List<String> chunksAdjacentes = gerarLinhasEColunasAVoltaDeUmChunk(linhaPrimeiroPonto, colunaPrimeiroPonto);
        for (String tmpString : chunksAdjacentes) {
            int latitude = Integer.parseInt(tmpString.split(",")[0]);
            int longitude = Integer.parseInt(tmpString.split(",")[1]);
            Polygon tmPolygon = chunkDadosLinhaColuna(latitude, longitude);
            if (tmPolygon.intersects(parcela)) list.add(new Vector2<Integer, Integer>(latitude, longitude));
        }
        // ----- Como não esta completamente so num chunk, quais dos chunks a volta e que a parcela interseta ----- \\

        // ----- Compor o output ----- \\
        StringBuilder res = new StringBuilder();
        for (Vector2<Integer, Integer> integerIntegerVector2 : list) {
            res.append(integerIntegerVector2.toString()).append(" ");
        }
        // ----- Compor o output ----- \\

        return res.toString();
    }

    private List<String> gerarLinhasEColunasAVoltaDeUmChunk(int nLinha, int nColuna) {
        List<String> newList = new ArrayList<String>();

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
*/
    private Polygon chunkDadosLinhaColuna(int nLinha, int nColuna) {
        GeometryFactory factory = new GeometryFactory();

        float topLatitude = (float) (TOP_MOST_LATITUDE_CONTINENTE - (FATOR_ADITIVO_LATITUDE * (nLinha - 1)));
        float bottomLatitude = (float) (TOP_MOST_LATITUDE_CONTINENTE - (FATOR_ADITIVO_LATITUDE * nLinha));

        float leftLongitude = (float) (LEFT_MOST_LONGITUDE_CONTINENTE + (FATOR_ADITIVO_LONGITUDE * (nColuna - 1)));
        float rightLongitude = (float) (LEFT_MOST_LONGITUDE_CONTINENTE + (FATOR_ADITIVO_LONGITUDE * nColuna));

        Coordinate[] coordinates = new Coordinate[5];

        coordinates[0] = new Coordinate(bottomLatitude, leftLongitude);
        coordinates[1] = new Coordinate(topLatitude, leftLongitude);
        coordinates[2] = new Coordinate(topLatitude, rightLongitude);
        coordinates[3] = new Coordinate(bottomLatitude, rightLongitude);
        coordinates[4] = new Coordinate(coordinates[0].getX(), coordinates[0].getY());

        Polygon polygon = factory.createPolygon(coordinates);

        return polygon;
    }

    private void preencherMapaDasIlhas() {
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


}
