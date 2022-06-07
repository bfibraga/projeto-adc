package pt.unl.fct.di.adc.silvanus.implementation;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import pt.unl.fct.di.adc.silvanus.data.parcel.Chunk;
import pt.unl.fct.di.adc.silvanus.data.parcel.Coordinate;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;
import pt.unl.fct.di.adc.silvanus.resources.ParcelaResource;
import pt.unl.fct.di.adc.silvanus.util.interfaces.Parcel;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.core.Response;
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

    // Names of the tables in the datastore
    public static final String PARCELAS_TO_BE_APPROVED_TABLE_NAME = "Parcelas To Be Approved";
    public static final String PARCELAS_THAT_ARE_APPROVED_TABLE_NAME = "Parcelas";

    // Names of the properties of an entity
    public static final String ENTITY_PROPERTY_COORDINATES = "coordinates";
    public static final String ENTITY_PROPERTY_ID_OWNER = "id_owner";
    public static final String ENTITY_PROPERTY_NAME_OF_TERRAIN = "name_of_terrain";
    public static final String ENTITY_PROPERTY_DESCRIPTION_OF_TERRAIN = "description_of_terrain";
    public static final String ENTITY_PROPERTY_CONSELHO_OF_TERRAIN = "conselho_of_terrain";
    public static final String ENTITY_PROPERTY_DISTRITO_OF_CONCELHO = "freguesia_of_concelho";
    public static final String ENTITY_PROPERTY_SECTION_OF_TERRAIN = "section_of_terrain";
    public static final String ENTITY_PROPERTY_NUMBER_ARTICLE_OF_TERRAIN = "number_article_of_terrain";
    public static final String ENTITY_PROPERTY_TYPE_OF_SOIL_COVERAGE = "type_of_soil_coverage";
    public static final String ENTITY_PROPERTY_CURRENT_USE_OF_SOIL = "current_use_of_soil";
    public static final String ENTITY_PROPERTY_PREVIOUS_USE_OF_SOIL = "previous_use_of_soil";
    public static final String ENTITY_PROPERTY_CHUNKS_OF_PARCELA = "chunks_of_terrain";

    private Map<String, Chunk> chunksIslands;

    private static final Logger LOG = Logger.getLogger(ParcelaResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private Polygon bigBBPolygon;

    private Polygon portugalContinentalPolygon;

    private GeometryFactory factory;


    public ParcelImplementation() {
        this.factory = new GeometryFactory();
        chunksIslands = new HashMap();
        this.bigBBPolygon = generateChunkAsPolygon(LEFT_MOST_LONGITUDE_GLBOAL, RIGHT_MOST_LONGITUDE_CONTINENTE, TOP_MOST_LATITUDE_CONTINENTE, BOTTOM_MOST_LATITUDE_GLOBAL, factory);
        this.portugalContinentalPolygon = generateChunkAsPolygon(LEFT_MOST_LONGITUDE_CONTINENTE, RIGHT_MOST_LONGITUDE_CONTINENTE, TOP_MOST_LATITUDE_CONTINENTE, BOTTOM_MOST_LATITUDE_CONTINENTE, factory);
    }

    // ---------- METHODS USED TO AID IN THE CREATION OF A TERRAIN ---------- \\

    @Override
    public Result<Void> createParcel(TerrainData terrainData) {
        Polygon terrainAsPolygon = coordinatesToPolygon(terrainData.getParcela());

        List<String> result = completeMethod(terrainAsPolygon, portugalContinentalPolygon, bigBBPolygon);

        if (result == null) {
            LOG.severe("Terrain is not well created.");
            return Result.error(Response.Status.NOT_ACCEPTABLE, "Terrain is not well created.");
        }

        String terrainID = terrainData.getId_of_owner() + "/" + terrainData.getName_of_terrain();
        Key terrainKey = datastore.newKeyFactory().setKind(PARCELAS_TO_BE_APPROVED_TABLE_NAME).newKey(terrainID);

        Entity terrainEntity = datastore.get(terrainKey);
        if (terrainEntity != null) {
            LOG.severe("Terrain already exists.");
            return Result.error(Response.Status.FORBIDDEN, "Terrain already exists.");
        }

        String coordinatesAsJSON = g.toJson(terrainData.getParcela());

        terrainEntity = Entity.newBuilder(terrainKey).set(ENTITY_PROPERTY_COORDINATES, coordinatesAsJSON)
                .set(ENTITY_PROPERTY_ID_OWNER, terrainData.getId_of_owner())
                .set(ENTITY_PROPERTY_NAME_OF_TERRAIN, terrainData.getName_of_terrain())
                .set(ENTITY_PROPERTY_DESCRIPTION_OF_TERRAIN, terrainData.getDescription_of_terrain())
                .set(ENTITY_PROPERTY_CONSELHO_OF_TERRAIN, terrainData.getConselho_of_terrain())
                .set(ENTITY_PROPERTY_DISTRITO_OF_CONCELHO, terrainData.getDistrito_of_terrain())
                .set(ENTITY_PROPERTY_SECTION_OF_TERRAIN, terrainData.getSection_of_terrain())
                .set(ENTITY_PROPERTY_NUMBER_ARTICLE_OF_TERRAIN, terrainData.getNumber_article_terrain())
                .set(ENTITY_PROPERTY_TYPE_OF_SOIL_COVERAGE, terrainData.getType_of_soil_coverage())
                .set(ENTITY_PROPERTY_CURRENT_USE_OF_SOIL, terrainData.getCurrent_use_of_soil())
                .set(ENTITY_PROPERTY_PREVIOUS_USE_OF_SOIL, terrainData.getPrevious_use_of_soil())
                .set(ENTITY_PROPERTY_CHUNKS_OF_PARCELA, g.toJson(result)).build();

        Transaction txn = datastore.newTransaction();
        try {

            txn.add(terrainEntity);
            txn.commit();
            LOG.info("Terrain was registered.");
            return Result.ok();
        } finally {
            if (txn.isActive()) txn.rollback();
        }
    }

    /**
     * This is the method that encapsulates all the necessary verifications to validate a terrain
     *
     * @param terrain the terrain that is being evaluates
     * @param portugalContinental the Bounding Box that contains Portugal Continental
     * @param bibBB               the Bounding Box that contains all the other bounding boxes
     * @return a list that contains all the chunks where the terrain lies, null otherwise
     */
    private List<String> completeMethod(Polygon terrain, Polygon portugalContinental, Polygon bibBB) {

        // Checks if the terrain is inside Big Bounding Box
        if (!bibBB.contains(terrain)) return null;

        // Checks if the terrain intersects the bounding box of one of the islands, if it does, checks if it is completely inside
        List<String> res = new ArrayList<>();
        fillMapOfIlands();
        String result = isTerrainInsideIsland(terrain);
        if (result == null) return null;
        else if (!result.equals("")) {
            res.add(result);
            return res;
        }

        // Checks where the terrain is inside of the Bounding Box of Portugal Continentel
        List<String> resultContinente = terrainIsInsidePortugalContinental(terrain, portugalContinental);
        return resultContinente;
    }


    /**
     * This methods receive the leftmost and rightmost values of a longitude and the topmost and bottom-most
     * value of a latitude and converts them into a Polygon object that represents a chunk
     *
     * @param leftLon   leftmost value of the longitude of the Polygon
     * @param rightLon  rightmost value of the longitude of the Polygon
     * @param topLat    topmost value of the latitude of the Polygon
     * @param bottomLat bottom-most value of the latitude of the Polygon
     * @param factory   object used to create the Polygon
     * @return the Polygon object that represent a chunk
     */
    private Polygon generateChunkAsPolygon(float leftLon, float rightLon, float topLat, float bottomLat, GeometryFactory factory) {
        org.locationtech.jts.geom.Coordinate[] coordinates = new org.locationtech.jts.geom.Coordinate[5];
        coordinates[0] = new org.locationtech.jts.geom.Coordinate(bottomLat, leftLon);
        coordinates[1] = new org.locationtech.jts.geom.Coordinate(topLat, leftLon);
        coordinates[2] = new org.locationtech.jts.geom.Coordinate(topLat, rightLon);
        coordinates[3] = new org.locationtech.jts.geom.Coordinate(bottomLat, rightLon);
        coordinates[4] = new org.locationtech.jts.geom.Coordinate(coordinates[0].getX(), coordinates[0].getY());
        Polygon polygon = factory.createPolygon(coordinates);
        return polygon;

    }

    /**
     * This method checks if a terrain intersects one of the islands. If it does, checks if it is completely inside that same island.
     *
     * @param terrain terrain that is being evaluated
     * @return "" (empty string) if it does not intersect any island, null in case it intersects but is not completely contained,
     * the identifier of the island otherwise
     */
    private String isTerrainInsideIsland(Polygon terrain) {
        for (Map.Entry<String, Chunk> entry : chunksIslands.entrySet()) {
            if (entry.getValue().getChunkAsPolygon().intersects(terrain)) {
                if (entry.getValue().getChunkAsPolygon().contains(terrain))
                    return entry.getKey();
                else
                    return null;
            }
        }
        return "";
    }

    /**
     * This method is used to check if a terrain is inside Portugal Continental's Bounding Box (henceforth refered as BBP) and if it is, in which chunks it lies.
     * <p>Firstly it is verfied if the terrain is inside BBP, if it is not, return null.</p>
     * Since we now know that the terrain is inside BBP the next step is to know in which chunks the terrain lies.
     * To do so, the first coordinate is used as a base. Then it is discovered in which chunk it lies.
     * <p>After having said chunk, the next step is to verify is the terrain is contained in the chunk, if it is, return the id of the cunk (line,collumn).</p>
     * If it isn't, it is checked in which of the surrounding chunks the terrain intersects and they are added to a list.
     *
     * @param terrain  the terrain to verify
     * @param portugal the Bounding Box of Portugal continental
     * @return a list with all the chunks the terrain intersects, null otherwise (there was an error)
     */
    private List<String> terrainIsInsidePortugalContinental(Polygon terrain, Polygon portugal) {
        if (!portugal.contains(terrain)) return null;

        // Find out in which chunk the first coordinate is \\
        int lineFirstCoordinate = 0;
        int collumnFirstCoordinate = 0;
        org.locationtech.jts.geom.Coordinate firstCoordinate = terrain.getCoordinates()[0];
        for (int i = 1; i <= NUMBER_OF_COLLUMNS_IN_CONTINENTE; i++) { // Searching through the collumns
            double leftLongitude = LEFT_MOST_LONGITUDE_CONTINENTE + (FATOR_ADITIVO_LONGITUDE * (i - 1));
            double rightLongitude = LEFT_MOST_LONGITUDE_CONTINENTE + (FATOR_ADITIVO_LONGITUDE * i);
            if (firstCoordinate.getY() > leftLongitude && firstCoordinate.getY() <= rightLongitude) {
                collumnFirstCoordinate = i;
                break;
            }

        }
        for (int j = 1; j <= NUMBER_OF_LINES_IN_CONTINENTE; j++) { // Searching through the lines
            double topLatitude = TOP_MOST_LATITUDE_CONTINENTE - (FATOR_ADITIVO_LATITUDE * (j - 1));
            double bottomLatitude = TOP_MOST_LATITUDE_CONTINENTE - (FATOR_ADITIVO_LATITUDE * (j));
            if (firstCoordinate.getX() < topLatitude && firstCoordinate.getX() >= bottomLatitude) {
                lineFirstCoordinate = j;
                break;
            }
        }
        // - Find out in which chunk the first coordinate is - \\

        List<String> list = new ArrayList<>(); // List that contains the chunks that intersect the terrain

        // Check if the terrain is contained in the chunk \\
        Polygon chunkPrimeiroPonto = chunkGivenLineAndCollumn(lineFirstCoordinate, collumnFirstCoordinate, factory);
        list.add(String.format("%d,%d", lineFirstCoordinate, collumnFirstCoordinate));
        if (terrain.within(chunkPrimeiroPonto)) {
            return list;
        }
        // - Check if the terrain is contained in the chunk - \\

        // Since the chunk doesn't contain the terrain it is needed to check which of the surrouding chunks intersect it  \\
        List<String> chunksAdjacentes = generatesChunksAroundAChunk(lineFirstCoordinate, collumnFirstCoordinate);
        for (String tmpString : chunksAdjacentes) {
            int latitude = Integer.parseInt(tmpString.split(",")[0]);
            int longitude = Integer.parseInt(tmpString.split(",")[1]);
            Polygon tmPolygon = chunkGivenLineAndCollumn(latitude, longitude, factory);
            if (tmPolygon.intersects(terrain))
                list.add(String.format("%d,%d", latitude, longitude));
        }
        // - Since the chunk doesn't contain the terrain it is needed to check which of the surrouding chunks intersect it - \\

        return list;
    }

    /**
     * This method is used to generate the surrounding chunks of a certain chunk identified by the number of it's line and collumn (passed as argument).
     * The identifiers of the chunks are limited, the line € [1 , 26] and the collumn € [1 , 38]. The line and collumn are both integers. If the inputs given
     * would result in the number of the line and/or collumn going outside their bounds, the value used is the closest bound.
     *
     * @param numberLine  number of the line of the central chunk
     * @param numberCollumn number of the collumn of the central chunk
     * @return a list of the indexes of the surrounding chunks (central not included)
     */
    private List<String> generatesChunksAroundAChunk(int numberLine, int numberCollumn) {
        List<String> newList = new ArrayList<>();

        int upperBound = numberLine - 1 >= 1 ? numberLine - 1 : numberLine;
        int lowerBound = numberLine + 1 <= NUMBER_OF_LINES_IN_CONTINENTE ? numberLine + 1 : numberLine;
        int leftBound = numberCollumn - 1 >= 1 ? numberCollumn - 1 : numberCollumn;
        int rightBound = numberCollumn + 1 <= NUMBER_OF_COLLUMNS_IN_CONTINENTE ? numberCollumn + 1 : numberCollumn;

        for (int i = upperBound; i <= lowerBound; i++) { // Line
            for (int j = leftBound; j <= rightBound; j++) { // Collumn
                if (!(numberLine == i && numberCollumn == j)) newList.add(i + "," + j);
            }
        }
        return newList;
    }

    /**
     * Given a pair(numberLine , numberCollumn) generates the chunk as a Polygon object
     *
     * @param numberLine  number of the line of the chunk
     * @param numberCollumn number of the collumn of the chunk
     * @return the chunk as a Polygon object
     */
    private Polygon chunkGivenLineAndCollumn(int numberLine, int numberCollumn, GeometryFactory factory) {
        float topLatitude = TOP_MOST_LATITUDE_CONTINENTE - (FATOR_ADITIVO_LATITUDE * (numberLine - 1));
        float bottomLatitude = TOP_MOST_LATITUDE_CONTINENTE - (FATOR_ADITIVO_LATITUDE * numberLine);

        float leftLongitude = LEFT_MOST_LONGITUDE_CONTINENTE + (FATOR_ADITIVO_LONGITUDE * (numberCollumn - 1));
        float rightLongitude = LEFT_MOST_LONGITUDE_CONTINENTE + (FATOR_ADITIVO_LONGITUDE * numberCollumn);

        Polygon polygon = generateChunkAsPolygon(leftLongitude, rightLongitude, topLatitude, bottomLatitude, factory);

        return polygon;
    }

    /**
     *
     * This method is used to fill the map with the chunks of each of the islands.
     *
     */
    private void fillMapOfIlands() {
        chunksIslands.clear();
        // Açores
        chunksIslands.put("São Miguel (Açores)", new Chunk("São Miguel (Açores)", -25.90f, -25.10f, 37.95f, 37.65f));
        chunksIslands.put("Santa Maria (Açores)", new Chunk("Santa Maria (Açores)", -25.20f, -25.00f, 37.02f, 36.92f));
        chunksIslands.put("Terceira (Açores)", new Chunk("Terceira (Açores)", -27.39f, -27.30f, 38.82f, 36.81f));
        chunksIslands.put("Graciosa (Açores)", new Chunk("Graciosa (Açores)", -28.08f, -27.93f, 39.10f, 39.00f));
        chunksIslands.put("São Jorge (Açores)", new Chunk("São Jorge (Açores)", -28.32f, -27.70f, 38.76f, 38.52f));
        chunksIslands.put("Pico (Açores)", new Chunk("Pico (Açores)", -28.55f, -28.02f, 38.57f, 38.38f));
        chunksIslands.put("Flores (Açores)", new Chunk("Flores (Açores)", -31.28f, -31.12f, 39.53f, 39.36f));
        chunksIslands.put("Corvo (Açores)", new Chunk("Corvo (Açores)", -31.13f, -31.08f, 39.73f, 39.66f));
        // Madeira
        chunksIslands.put("Porto Santo (Madeira)", new Chunk("Porto Santo (Madeira)", -16.42f, -16.27f, 33.11f, 32.99f));
        chunksIslands.put("Madeira", new Chunk("Madeira (Madeira)", -17.27f, -16.64f, 32.88f, 32.62f));
    }

    /**
     * Dado um array de Coordenadas converte as mesmas num Polygon
     *
     * @param coordinates coordenadas que compoem uma parcela
     * @return o objeto Polygon que representa uma parcela
     */
    private Polygon coordinatesToPolygon(Coordinate[] coordinates) {
        org.locationtech.jts.geom.Coordinate[] coordinatesPolygon = new org.locationtech.jts.geom.Coordinate[coordinates.length + 1];
        for (int i = 0; i < coordinates.length; i++) {
            coordinatesPolygon[i] = new org.locationtech.jts.geom.Coordinate(coordinates[i].getLat(), coordinates[i].getLon());
        }
        coordinatesPolygon[coordinatesPolygon.length - 1] = new org.locationtech.jts.geom.Coordinate(coordinatesPolygon[0].getX(), coordinatesPolygon[0].getY());
        return factory.createPolygon(coordinatesPolygon);
    }

    // ---------- METHODS USED TO AID IN THE CREATION OF A TERRAIN ---------- \\


    // ---------- METHODS USED TO CHECK IF A TERRAIN INTERSECTS ANOTHER ---------- \\

    @Override
    public Result<String> checkIfParcelHasIntersections(Coordinate[] terrain) {
        Polygon terrainAsPolygon = coordinatesToPolygon(terrain);
        LOG.fine("Query was started.");
        Result<String> res;
        res = querieTableThatContainsParcels(terrainAsPolygon, PARCELAS_TO_BE_APPROVED_TABLE_NAME);
        if (res != null)
            return res;
        return querieTableThatContainsParcels(terrainAsPolygon, PARCELAS_THAT_ARE_APPROVED_TABLE_NAME);
    }

    /**
     * Given a the name of a table (that is in the datstore) and a Polygon checks if any of the terrains in that table intersect the one in argument.
     * @param polygonTerrain the terrain as a Polygon object
     * @param nameOfTable the name of the table in the datastore in which to query
     * @return an error if it does, null if it doesn't
     */
    private Result<String> querieTableThatContainsParcels(Polygon polygonTerrain, String nameOfTable) {
        Query<Entity> query;
        QueryResults<Entity> results;
        query = Query.newEntityQueryBuilder().setKind(nameOfTable).build();
        results = datastore.run(query);
        while (results.hasNext()) {
            Entity tmp = results.next();
            String coordenadas = tmp.getString(ENTITY_PROPERTY_COORDINATES);
            Coordinate[] c = g.fromJson(coordenadas, Coordinate[].class);
            Polygon p = coordinatesToPolygon(c);
            if (p.intersects(polygonTerrain)) {
                LOG.warning("Existe uma interseccao com a parcela: \"" + tmp.getString(ENTITY_PROPERTY_ID_OWNER) + "/" + tmp.getString(ENTITY_PROPERTY_NAME_OF_TERRAIN) + "\"");
                return Result.error(Response.Status.CONFLICT, "Foi detetada uma interseção com a parcela: \"" + tmp.getString("id_owner") + "/" + tmp.getString("name_of_terrain") + "\"");
            }
        }
        return null;
    }

    // ---------- METHODS USED TO CHECK IF A TERRAIN INTERSECTS ANOTHER ---------- \\


    // ---------- METHODS USED TO AID IN THE PROCESS OF APPROVING A TERRAIN ---------- \\

    @Override
    public Result<Void> approveTerrain(String ownerTerrain, String nameTerrain) {
        Entity terrainToBeApproved = checkIfTerrainIsInWaitList(ownerTerrain, nameTerrain);
        if (terrainToBeApproved == null)
            return null;
        Result<Void> result = denyTerrain(ownerTerrain, nameTerrain);
        if (!result.isOK())
            return result;

        String str = terrainToBeApproved.getString(ENTITY_PROPERTY_ID_OWNER) + "/" + terrainToBeApproved.getString(ENTITY_PROPERTY_NAME_OF_TERRAIN);
        Key parcelaKey = datastore.newKeyFactory().setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME).newKey(str);
        Entity parcelaApproved = Entity.newBuilder(parcelaKey, terrainToBeApproved).build();
        Transaction txn = datastore.newTransaction();

        try {
            txn.add(parcelaApproved);
            txn.commit();
            LOG.info("Parcela foi inserida na tabela final.");
        } finally {
            if (txn.isActive()) txn.rollback();
        }
        return Result.ok();
    }

    /**
     * Checks if a terrain (identified by the string "ownerTerrain/nameTerrain") is in the waitlist
     * @param ownerTerrain owner of the terrain
     * @param nameTerrain name of the terrain
     * @return the terrain as an Entity object if it exists, null otherwise
     */
    private Entity checkIfTerrainIsInWaitList(String ownerTerrain, String nameTerrain) {
        Query<Entity> query;
        QueryResults<Entity> results;
        // Queries the "wait list" for the terrain (as an Entity)
        query = Query.newEntityQueryBuilder().setKind(PARCELAS_TO_BE_APPROVED_TABLE_NAME)
                .setFilter(StructuredQuery.CompositeFilter.and(
                        StructuredQuery.PropertyFilter.eq(ENTITY_PROPERTY_ID_OWNER, ownerTerrain),
                        StructuredQuery.PropertyFilter.eq(ENTITY_PROPERTY_NAME_OF_TERRAIN, nameTerrain)
                )).build();
        results = datastore.run(query);
        Entity terrainToBeApproved = results.next(); // Since there can only be one, .next() will return the entity
        if (terrainToBeApproved == null) {
            LOG.severe("A parcela não foi encontrada.");
            return null;
        }
        return terrainToBeApproved;
    }

    // ---------- METHODS USED TO AID IN THE PROCESS OF APPROVING A TERRAIN ---------- \\

    // ---------- METHODS USED TO AID IN THE PROCESS OF DENYING A TERRAIN ---------- \\

    @Override
    public Result<Void> denyTerrain(String ownerTerrain, String nameTerrain) {
        Entity terrainToBeApproved = checkIfTerrainIsInWaitList(ownerTerrain, nameTerrain);
        if (terrainToBeApproved == null)
            return Result.error(Response.Status.NOT_FOUND, "Terrain was not found.");
        Transaction txn = datastore.newTransaction();
        try {
            txn.delete(terrainToBeApproved.getKey());
            txn.commit();
            LOG.info("Terrain was removed from the \"wait list\".");
        } finally {
            if (txn.isActive()) txn.rollback();
        }
        return Result.ok();
    }

    // ---------- METHODS USED TO AID IN THE PROCESS OF APPROVING A TERRAIN ---------- \\
}
