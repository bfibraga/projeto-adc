package pt.unl.fct.di.adc.silvanus.implementation.terrain;

import com.google.cloud.datastore.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import pt.unl.fct.di.adc.silvanus.api.impl.Parcel;
import pt.unl.fct.di.adc.silvanus.data.terrain.*;
import pt.unl.fct.di.adc.silvanus.data.terrain.LatLng;
import pt.unl.fct.di.adc.silvanus.data.terrain.chunks.IslandChunk;
import pt.unl.fct.di.adc.silvanus.data.terrain.result.ChunkResultData;
import pt.unl.fct.di.adc.silvanus.data.terrain.result.PolygonDrawingData;
import pt.unl.fct.di.adc.silvanus.data.terrain.result.TerrainResultData;
import pt.unl.fct.di.adc.silvanus.util.JSON;
import pt.unl.fct.di.adc.silvanus.util.PolygonUtils;
import pt.unl.fct.di.adc.silvanus.util.Random;
import pt.unl.fct.di.adc.silvanus.util.cache.ChunkCacheManager;
import pt.unl.fct.di.adc.silvanus.util.cache.ParcelCacheManager;
import pt.unl.fct.di.adc.silvanus.data.terrain.chunks.Chunk;
import pt.unl.fct.di.adc.silvanus.data.terrain.chunks.ChunkBoard;
import pt.unl.fct.di.adc.silvanus.data.terrain.chunks.exceptions.OutOfChunkBounds;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

public class TerrainImplementation implements Parcel {

    private final double sizeX = (RIGHT_MOST_LONGITUDE_CONTINENTE - LEFT_MOST_LONGITUDE_CONTINENTE) / NUMBER_OF_COLLUMNS_IN_CONTINENTE;
    private final double sizeY = (TOP_MOST_LATITUDE_CONTINENTE - BOTTOM_MOST_LATITUDE_CONTINENTE) / NUMBER_OF_LINES_IN_CONTINENTE;

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
    public static final String ENTITY_PROPERTY_CONSELHO_OF_TERRAIN = "county_of_terrain";
    public static final String ENTITY_PROPERTY_DISTRITO_OF_TERRAIN = "district_of_concelho";
    public static final String ENTITY_PROPERTY_SECTION_OF_TERRAIN = "section_of_terrain";
    public static final String ENTITY_PROPERTY_NUMBER_ARTICLE_OF_TERRAIN = "number_article_of_terrain";
    public static final String ENTITY_PROPERTY_TYPE_OF_SOIL_COVERAGE = "type_of_soil_coverage";
    public static final String ENTITY_PROPERTY_CURRENT_USE_OF_SOIL = "current_use_of_soil";
    public static final String ENTITY_PROPERTY_PREVIOUS_USE_OF_SOIL = "previous_use_of_soil";
    public static final String ENTITY_PROPERTY_CHUNKS_OF_PARCELA = "chunks_of_terrain";
    public static final String ENTITY_PROPERTY_LEFT_MOST_POINT = "left_most_point";
    public static final String ENTITY_PROPERTY_RIGHT_MOST_POINT = "right_most_point";
    public static final String ENTITY_PROPERTY_TOP_MOST_POINT = "top_most_point";
    public static final String ENTITY_PROPERTY_BOTTOM_MOST_POINT = "bottom_most_point";


    //TODO Transferir esta parte para cache
    private Map<String, IslandChunk> chunksIslands;

    private static final Logger LOG = Logger.getLogger(TerrainImplementation.class.getName());

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private ParcelCacheManager<String> cacheManager = new ParcelCacheManager<>();
    private Polygon bigBBPolygon;

    private Polygon portugalContinentalPolygon;

    private GeometryFactory factory;


    private ChunkCacheManager<String> chunkCacheManager;
    private ChunkBoard<String> portugal;
    private ChunkBoard<String> madeira;
    private ChunkBoard<String> azores;

    public static final double PORTUGAL_SIZE_X = Math.abs(RIGHT_MOST_LONGITUDE_CONTINENTE - LEFT_MOST_LONGITUDE_CONTINENTE);
    public static final double PORTUGAL_SIZE_Y = Math.abs(TOP_MOST_LATITUDE_CONTINENTE - BOTTOM_MOST_LATITUDE_CONTINENTE);

    private static final double RIGHT_MOST_LONGITUDE_MADEIRA = -16.64;
    private static final double TOP_MOST_LATITUDE_MADEIRA = 32.88;
    private static final double LEFT_MOST_LONGITUDE_MADEIRA = -17.27;
    private static final double BOTTOM_MOST_LATITUDE_MADEIRA = 32.62;

    public static final double MADEIRA_SIZE_X = Math.abs(RIGHT_MOST_LONGITUDE_MADEIRA - LEFT_MOST_LONGITUDE_MADEIRA);
    public static final double MADEIRA_SIZE_Y = Math.abs(TOP_MOST_LATITUDE_MADEIRA - BOTTOM_MOST_LATITUDE_MADEIRA);


    public TerrainImplementation() {
        this.factory = new GeometryFactory();
        chunksIslands = new HashMap();
        this.bigBBPolygon = generateChunkAsPolygon(LEFT_MOST_LONGITUDE_GLBOAL, RIGHT_MOST_LONGITUDE_CONTINENTE, TOP_MOST_LATITUDE_CONTINENTE, BOTTOM_MOST_LATITUDE_GLOBAL, factory);
        this.portugalContinentalPolygon = generateChunkAsPolygon(LEFT_MOST_LONGITUDE_CONTINENTE, RIGHT_MOST_LONGITUDE_CONTINENTE, TOP_MOST_LATITUDE_CONTINENTE, BOTTOM_MOST_LATITUDE_CONTINENTE, factory);

        portugal = new ChunkBoard<>(38, 26, PORTUGAL_SIZE_X, PORTUGAL_SIZE_Y, LEFT_MOST_LONGITUDE_CONTINENTE, BOTTOM_MOST_LATITUDE_CONTINENTE);
        madeira = new ChunkBoard<>(4, 6, MADEIRA_SIZE_X, MADEIRA_SIZE_Y, LEFT_MOST_LONGITUDE_MADEIRA, BOTTOM_MOST_LATITUDE_MADEIRA);
        chunkCacheManager = new ChunkCacheManager<>(1000 * 60 * 60 * 24);

    }

    // ---------- METHODS USED TO AID IN THE CREATION OF A TERRAIN ---------- \\

    @Override
    public Result<Void> createParcel(TerrainData terrainData) {
        terrainData.createEdges();
        Polygon terrainAsPolygon = PolygonUtils.polygon(terrainData.getParcela());

        //List<String> result = completeMethod(terrainAsPolygon, portugalContinentalPolygon, bigBBPolygon);

        /*if (result == null) {
            LOG.severe("Terrain is not well created.");
            return Result.error(Response.Status.NOT_ACCEPTABLE, "Terrain is not well created.");
        }*/

        List<Chunk<String>> chunks = locateChunks(terrainData.getParcela());

        if (chunks.isEmpty()) {
            LOG.severe("Terrain is not well created.");
            return Result.error(Response.Status.NOT_ACCEPTABLE, "Terrain is not well created.");
        }

        String terrainID = terrainData.getID();
        Key terrainKey = datastore.newKeyFactory().setKind(PARCELAS_TO_BE_APPROVED_TABLE_NAME).newKey(terrainID);
        Key ownerTerrainKey = datastore.newKeyFactory().setKind("TerrainOwner").newKey(terrainID);
        Key approvedTerrainKey = datastore.newKeyFactory().setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME).newKey(terrainID);

        Entity terrainEntity = datastore.get(terrainKey);
        if (terrainEntity != null || datastore.get(approvedTerrainKey) != null) {
            LOG.severe("Terrain already exists.");
            return Result.error(Response.Status.FORBIDDEN, "Terrain already exists.");
        }

        String coordinatesAsJSON = JSON.encode(terrainData.getParcela());

        TerrainIdentifierData id = terrainData.getCredentials();
        TerrainOwner owner = terrainData.getOwner();
        TerrainInfoData info = terrainData.getInfo();

        //TODO Send entity for onwership of this terrain and chunks the parcel is locate
        Transaction txn = datastore.newTransaction();
        try {

            Entity ownerTerrainEntity = Entity.newBuilder(ownerTerrainKey)
                    .set("owner_name", owner.getName())
                    .set("owner_id", owner.getNif())
                    .set("owner_address", owner.getAddress())
                    .set("owner_telephone", owner.getTelephone())
                    .set("owner_smartphone", owner.getSmartphone())
                    .build();

            //TODO Testing
            Entity chunkEntity;
            /*for (String chunk : result) {
                Key chunkKey = datastore.newKeyFactory().setKind("Chunk").newKey(chunk);
                chunkEntity = txn.get(chunkKey);

                if (chunkEntity == null) {
                    chunkEntity = Entity.newBuilder(chunkKey)
                            .set("parcels_id", terrainID)
                            .build();
                } else {
                    String alreadyContained = chunkEntity.getString("parcels_id");
                    alreadyContained = alreadyContained.concat("/" + terrainID);
                    chunkEntity = Entity.newBuilder(chunkKey)
                            .set("parcels_id", alreadyContained)
                            .build();
                }
                txn.put(chunkEntity);
            }*/
            for (Chunk<String> chunk : chunks) {
                String chunkID = chunk.getID();
                Key chunkKey = datastore.newKeyFactory().setKind("Chunk").newKey(chunkID);
                chunkEntity = txn.get(chunkKey);

                if (chunkEntity == null) {
                    chunkEntity = Entity.newBuilder(chunkKey)
                            .set("parcels_id", terrainID)
                            .build();
                } else {
                    String alreadyContained = chunkEntity.getString("parcels_id");
                    alreadyContained = alreadyContained.concat("/" + terrainID);
                    chunkEntity = Entity.newBuilder(chunkKey)
                            .set("parcels_id", alreadyContained)
                            .build();
                }
                txn.put(chunkEntity);
                this.chunkCacheManager.remove(chunkID);
            }

            terrainEntity = Entity.newBuilder(terrainKey)
                    .set(ENTITY_PROPERTY_COORDINATES, coordinatesAsJSON)
                    .set(ENTITY_PROPERTY_ID_OWNER, id.getUserID())
                    .set(ENTITY_PROPERTY_NAME_OF_TERRAIN, id.getName())
                    .set(ENTITY_PROPERTY_DESCRIPTION_OF_TERRAIN, info.getDescription())
                    .set(ENTITY_PROPERTY_CONSELHO_OF_TERRAIN, id.getTownhall())
                    .set(ENTITY_PROPERTY_DISTRITO_OF_TERRAIN, id.getDistrict())
                    .set(ENTITY_PROPERTY_SECTION_OF_TERRAIN, id.getSection())
                    .set(ENTITY_PROPERTY_NUMBER_ARTICLE_OF_TERRAIN, id.getNumber_article())
                    .set(ENTITY_PROPERTY_TYPE_OF_SOIL_COVERAGE, info.getType_of_soil_coverage())
                    .set(ENTITY_PROPERTY_CURRENT_USE_OF_SOIL, info.getCurrent_use())
                    .set(ENTITY_PROPERTY_PREVIOUS_USE_OF_SOIL, info.getPrevious_use())
                    //.set(ENTITY_PROPERTY_CHUNKS_OF_PARCELA, JSON.encode(chunks))
                    .set(ENTITY_PROPERTY_LEFT_MOST_POINT, terrainData.getEdgesTerrain()[0])
                    .set(ENTITY_PROPERTY_RIGHT_MOST_POINT, terrainData.getEdgesTerrain()[1])
                    .set(ENTITY_PROPERTY_TOP_MOST_POINT, terrainData.getEdgesTerrain()[2])
                    .set(ENTITY_PROPERTY_BOTTOM_MOST_POINT, terrainData.getEdgesTerrain()[3]).build();

            txn.put(terrainEntity, ownerTerrainEntity);
            txn.commit();
            LOG.info("Terrain was registered.");
            return Result.ok();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private List<Chunk<String>> locateChunks(LatLng[] points) {
        List<Chunk<String>> result = new ArrayList<>();
        try {
            result.addAll(madeira.polygon(points));
        } catch (OutOfChunkBounds ignored) {
        }
        //TODO Add Azores
        try {
            result.addAll(portugal.polygon(points));
        } catch (OutOfChunkBounds e) {
            LOG.severe(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * This is the method that encapsulates all the necessary verifications to validate a terrain
     *
     * @param terrain             the terrain that is being evaluates
     * @param portugalContinental the Bounding Box that contains Portugal Continental
     * @param bibBB               the Bounding Box that contains all the other bounding boxes
     * @return a list that contains all the chunks where the terrain lies, null otherwise
     */
    private List<String> completeMethod(Polygon terrain, Polygon portugalContinental, Polygon bibBB) throws OutOfChunkBounds {

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
        for (Map.Entry<String, IslandChunk> entry : chunksIslands.entrySet()) {
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
    private List<String> terrainIsInsidePortugalContinental(Polygon terrain, Polygon portugal) throws OutOfChunkBounds {
        if (!portugal.contains(terrain)) return null;

        // Find out in which chunk the first coordinate is \\
        int lineFirstCoordinate = 0;
        int collumnFirstCoordinate = 0;
        Coordinate firstCoordinate = terrain.getCoordinates()[0];
        //TODO Improve time complexity and refactor
        /*for (int i = 1; i <= NUMBER_OF_COLLUMNS_IN_CONTINENTE; i++) { // Searching through the collumns
            double leftLongitude = LEFT_MOST_LONGITUDE_CONTINENTE + (sizeX * (i - 1));
            double rightLongitude = LEFT_MOST_LONGITUDE_CONTINENTE + (sizeX * i);
            if (firstCoordinate.getY() > leftLongitude && firstCoordinate.getY() <= rightLongitude) {
                collumnFirstCoordinate = i;
                break;
            }

        }

        //TODO Improve time complexity and refactor
        for (int j = 1; j <= NUMBER_OF_LINES_IN_CONTINENTE; j++) { // Searching through the lines
            double topLatitude = TOP_MOST_LATITUDE_CONTINENTE - (sizeY * (j - 1));
            double bottomLatitude = TOP_MOST_LATITUDE_CONTINENTE - (sizeY * (j));
            if (firstCoordinate.getX() < topLatitude && firstCoordinate.getX() >= bottomLatitude) {
                lineFirstCoordinate = j;
                break;
            }
        }*/

        //TODO Figure it out the right offset to do
        int[] chunkPos = this.portugal.worldCoordsToChunk(firstCoordinate.getX(), firstCoordinate.getY());

        // - Find out in which chunk the first coordinate is - \\

        List<String> list = new ArrayList<>(); // List that contains the chunks that intersect the terrain

        // Check if the terrain is contained in the chunk \\
        Polygon chunkPrimeiroPonto = chunkGivenLineAndCollumn(chunkPos[0], chunkPos[1], factory);
        list.add(String.format("%d,%d", chunkPos[0], chunkPos[1]));
        if (terrain.within(chunkPrimeiroPonto)) {
            return list;
        }
        // - Check if the terrain is contained in the chunk - \\

        // Since the chunk doesn't contain the terrain it is needed to check which of the surrouding chunks intersect it  \\
        List<String> chunksAdjacentes = generatesChunksAroundAChunk(chunkPos[0], chunkPos[1]);
        for (String tmpString : chunksAdjacentes) {
            String[] parts = tmpString.split(",");
            int latitude = Integer.parseInt(parts[0]);
            int longitude = Integer.parseInt(parts[1]);
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
     * @param numberLine    number of the line of the central chunk
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
     * @param numberLine    number of the line of the chunk
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
     * This method is used to fill the map with the chunks of each of the islands.
     */
    private void fillMapOfIlands() {
        chunksIslands.clear();
        // Açores
        chunksIslands.put("São Miguel (Açores)", new IslandChunk("São Miguel (Açores)", -25.90f, -25.10f, 37.95f, 37.65f));
        chunksIslands.put("Santa Maria (Açores)", new IslandChunk("Santa Maria (Açores)", -25.20f, -25.00f, 37.02f, 36.92f));
        chunksIslands.put("Terceira (Açores)", new IslandChunk("Terceira (Açores)", -27.39f, -27.30f, 38.82f, 36.81f));
        chunksIslands.put("Graciosa (Açores)", new IslandChunk("Graciosa (Açores)", -28.08f, -27.93f, 39.10f, 39.00f));
        chunksIslands.put("São Jorge (Açores)", new IslandChunk("São Jorge (Açores)", -28.32f, -27.70f, 38.76f, 38.52f));
        chunksIslands.put("Pico (Açores)", new IslandChunk("Pico (Açores)", -28.55f, -28.02f, 38.57f, 38.38f));
        chunksIslands.put("Flores (Açores)", new IslandChunk("Flores (Açores)", -31.28f, -31.12f, 39.53f, 39.36f));
        chunksIslands.put("Corvo (Açores)", new IslandChunk("Corvo (Açores)", -31.13f, -31.08f, 39.73f, 39.66f));
        // Madeira
        chunksIslands.put("Porto Santo (Madeira)", new IslandChunk("Porto Santo (Madeira)", -16.42f, -16.27f, 33.11f, 32.99f));
        chunksIslands.put("Madeira", new IslandChunk("Madeira (Madeira)", -17.27f, -16.64f, 32.88f, 32.62f));
    }

    /**
     * Dado um array de Coordenadas converte as mesmas num Polygon
     *
     * @param latLngs coordenadas que compoem uma parcela
     * @return o objeto Polygon que representa uma parcela
     */
    private Polygon coordinatesToPolygon(LatLng[] latLngs) {
        Coordinate[] coordinatesPolygon = new Coordinate[latLngs.length + 1];
        for (int i = 0; i < latLngs.length; i++) {
            coordinatesPolygon[i] = new Coordinate(latLngs[i].getLat(), latLngs[i].getLng());
        }
        coordinatesPolygon[coordinatesPolygon.length - 1] = new Coordinate(coordinatesPolygon[0].getX(), coordinatesPolygon[0].getY());
        return factory.createPolygon(coordinatesPolygon);
    }

    // ---------- METHODS USED TO AID IN THE CREATION OF A TERRAIN ---------- \\


    // ---------- METHODS USED TO CHECK IF A TERRAIN INTERSECTS ANOTHER ---------- \\

    @Override
    public Result<String> checkIfParcelHasIntersections(LatLng[] terrain) {
        List<Chunk<String>> chunks = new ArrayList<>();
        try {
            chunks = madeira.polygon(terrain);
        } catch (OutOfChunkBounds ignored) {
        }

        try {
            chunks = portugal.polygon(terrain);
        } catch (OutOfChunkBounds e) {
            return Result.error(Response.Status.BAD_REQUEST, e.getMessage());
        }

        Polygon polygon = PolygonUtils.polygon(terrain);
        for (Chunk<String> chunk : chunks) {
            String chunkID = chunk.getID();

            ChunkResultData chunkResultData = this.chunkCacheManager.get(chunkID, "data", ChunkResultData.class);

            if (chunkResultData == null){
                chunkResultData = queryTerrainsInChunk(chunk.getX(), chunk.getY()).value();
            }

            System.out.println(chunkResultData.getChunk());
            Set<PolygonDrawingData> polygonDrawingData = chunkResultData.getData();
            for (PolygonDrawingData data: polygonDrawingData) {
                Polygon selected = PolygonUtils.polygon(data.getPoints());
                Geometry intersection = selected.intersection(polygon.getBoundary());

                System.out.println(Arrays.toString(intersection.getCoordinates()));
                if (intersection != null && intersection.getDimension() > 1){
                    return Result.ok(intersection.toText(), "Intersecta com um terreno existente");
                }
            }

        }
        return Result.ok();

        /*Polygon terrainAsPolygon = coordinatesToPolygon(terrain);
        LOG.fine("Query was started.");
        Result<String> res;
        res = querieTableThatContainsParcels(terrainAsPolygon, PARCELAS_TO_BE_APPROVED_TABLE_NAME);
        if (res != null)
            return res;
        return querieTableThatContainsParcels(terrainAsPolygon, PARCELAS_THAT_ARE_APPROVED_TABLE_NAME);*/
    }

    /**
     * Given a the name of a table (that is in the datstore) and a Polygon checks if any of the terrains in that table intersect the one in argument.
     *
     * @param polygonTerrain the terrain as a Polygon object
     * @param nameOfTable    the name of the table in the datastore in which to query
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
            LatLng[] c = JSON.decode(coordenadas, LatLng[].class);
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
            return Result.error(Response.Status.NOT_FOUND, "Terrain was not found.");
        Result<Void> result = denyTerrain(ownerTerrain, nameTerrain);
        if (!result.isOK())
            return result;

        //String str = terrainToBeApproved.getString(ENTITY_PROPERTY_ID_OWNER) + "/" + terrainToBeApproved.getString(ENTITY_PROPERTY_NAME_OF_TERRAIN);
        String str = terrainToBeApproved.getKey().getNameOrId().toString();
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
     *
     * @param ownerTerrain owner of the terrain
     * @param nameTerrain  name of the terrain
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
        if (!results.hasNext()) {
            LOG.severe("A parcela não foi encontrada.");
            return null;
        }
        Entity terrainToBeApproved = results.next(); // Since there can only be one, .next() will return the entity
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

    // ---------- METHODS USED TO AID IN THE PROCESS OF DELETING A TERRAIN ---------- \\

    @Override
    public Result<Void> deleteTerrain(String ownerTerrain, String nameTerrain) {
        Entity tmp = getTerrainAsEntity(ownerTerrain, nameTerrain, PARCELAS_THAT_ARE_APPROVED_TABLE_NAME);
        if (tmp == null)
            return Result.error(Response.Status.NOT_FOUND, "The Terrain was not found.");
        Transaction txn = datastore.newTransaction();
        Key keyOfTerrain = tmp.getKey();
        try {
            txn.delete(keyOfTerrain);
            txn.commit();
            LOG.info("Terrain was deleted.");
        } finally {
            if (txn.isActive()) txn.rollback();
        }
        return Result.ok();
    }

    // ---------- METHODS USED TO AID IN THE PROCESS OF DELETING A TERRAIN ---------- \\

    /**
     * This method is used to get a terrain from one of the tables it can be in
     *
     * @param ownerTerrain the id of the owner of the terrain
     * @param nameTerrain  the name of the terrain
     * @param nameOfTable  the name of the table in which to query
     * @return the entity (it can be null)
     */
    private Entity getTerrainAsEntity(String ownerTerrain, String nameTerrain, String nameOfTable) {
        String terrainID = ownerTerrain + "/" + nameTerrain;
        Key terrainKey = datastore.newKeyFactory().setKind(nameOfTable).newKey(terrainID);

        Entity terrainEntity = datastore.get(terrainKey);
        return terrainEntity;
    }

    @Override
    public Result<List<TerrainResultData>> getAllTerrainsOfUser(String ownerTerrain) {
        Query<Entity> query;
        QueryResults<Entity> results;

        query = Query.newEntityQueryBuilder().setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME)
                .setFilter(StructuredQuery.PropertyFilter.eq(ENTITY_PROPERTY_ID_OWNER, ownerTerrain))
                .build();

        results = datastore.run(query);

        if (!results.hasNext())
            return Result.error(Response.Status.NO_CONTENT, "No terrains were found.");
        List<TerrainResultData> list = new ArrayList<>();
        while (results.hasNext()) {
            Entity tmp = results.next();
            Key ownerKey = datastore.newKeyFactory().setKind("TerrainOwner").newKey(tmp.getKey().getNameOrId().toString());
            Entity owner = datastore.get(ownerKey);
            LatLng[] points = JSON.decode(tmp.getString(ENTITY_PROPERTY_COORDINATES), LatLng[].class);

            TerrainResultData resultData = new TerrainResultData(
                    points,
                    PolygonUtils.centroid(points),
                    Random.color(),
                    new TerrainIdentifierData(
                            tmp.getString(ENTITY_PROPERTY_NAME_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_CONSELHO_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_DISTRITO_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_SECTION_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_NUMBER_ARTICLE_OF_TERRAIN)
                    ),
                    new TerrainOwner(
                            owner.getString("owner_name"),
                            owner.getString("owner_id"),
                            owner.getString("owner_address"),
                            owner.getString("owner_telephone"),
                            owner.getString("owner_smartphone")
                    ),
                    new TerrainInfoData(
                            tmp.getString(ENTITY_PROPERTY_DESCRIPTION_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_TYPE_OF_SOIL_COVERAGE),
                            tmp.getString(ENTITY_PROPERTY_CURRENT_USE_OF_SOIL),
                            tmp.getString(ENTITY_PROPERTY_PREVIOUS_USE_OF_SOIL),
                            new String[]{},
                            new LatLng[]{}
                    )
            );
            list.add(resultData);
        }
        return Result.ok(list, "");
    }

    @Override
    public Result<List<TerrainResultData>> getAllPendingTerrainsOfUser(String ownerTerrain) {
        Query<Entity> query;
        QueryResults<Entity> results;

        query = Query.newEntityQueryBuilder()
                .setKind(PARCELAS_TO_BE_APPROVED_TABLE_NAME)
                .setFilter(StructuredQuery.PropertyFilter.eq(ENTITY_PROPERTY_ID_OWNER, ownerTerrain))
                .build();

        results = datastore.run(query);

        if (!results.hasNext())
            return Result.error(Response.Status.NO_CONTENT, "No terrains were found.");
        List<TerrainResultData> list = new ArrayList<>();
        while (results.hasNext()) {
            Entity tmp = results.next();
            Key ownerKey = datastore.newKeyFactory().setKind("TerrainOwner").newKey(tmp.getKey().getNameOrId().toString());
            Entity owner = datastore.get(ownerKey);
            LatLng[] points = JSON.decode(tmp.getString(ENTITY_PROPERTY_COORDINATES), LatLng[].class);

            TerrainResultData resultData = new TerrainResultData(
                    points,
                    PolygonUtils.centroid(points),
                    Random.color(),
                    new TerrainIdentifierData(
                            tmp.getString(ENTITY_PROPERTY_NAME_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_CONSELHO_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_DISTRITO_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_SECTION_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_NUMBER_ARTICLE_OF_TERRAIN)
                    ),
                    new TerrainOwner(
                            owner.getString("owner_name"),
                            owner.getString("owner_id"),
                            owner.getString("owner_address"),
                            owner.getString("owner_telephone"),
                            owner.getString("owner_smartphone")
                    ),
                    new TerrainInfoData(
                            tmp.getString(ENTITY_PROPERTY_DESCRIPTION_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_TYPE_OF_SOIL_COVERAGE),
                            tmp.getString(ENTITY_PROPERTY_CURRENT_USE_OF_SOIL),
                            tmp.getString(ENTITY_PROPERTY_PREVIOUS_USE_OF_SOIL),
                            new String[]{},
                            new LatLng[]{}
                    )
            );
            list.add(resultData);
        }
        return Result.ok(list, "");
    }

    @Override
    public Result<List<Entity>> getAllTerrainsInCounty(String nameOfCounty) {
        Query<Entity> query;
        QueryResults<Entity> results;

        query = Query.newEntityQueryBuilder().setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME)
                .setFilter(StructuredQuery.PropertyFilter.eq(ENTITY_PROPERTY_CONSELHO_OF_TERRAIN, nameOfCounty))
                .build();

        results = datastore.run(query);

        if (!results.hasNext())
            return Result.error(Response.Status.NO_CONTENT, "No terrains were found.");
        List<Entity> list = new ArrayList<>();
        while (results.hasNext()) {
            Entity tmp = results.next();
            list.add(tmp);
        }
        return Result.ok(list, "");
    }

    @Override
    public Result<List<Entity>> getAllTerrainsInDistrict(String nameOfDistrict) {
        Query<Entity> query;
        QueryResults<Entity> results;

        query = Query.newEntityQueryBuilder().setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME)
                .setFilter(StructuredQuery.PropertyFilter.eq(ENTITY_PROPERTY_DISTRITO_OF_TERRAIN, nameOfDistrict))
                .build();

        results = datastore.run(query);

        if (!results.hasNext())
            return Result.error(Response.Status.NO_CONTENT, "No terrains were found.");
        List<Entity> list = new ArrayList<>();
        while (results.hasNext()) {
            Entity tmp = results.next();
            tmp.getList("");
            list.add(tmp);
        }
        return Result.ok(list, "");
    }

    @Override
    public Result<List<String>> queryTerrainsOfUserInCounty(String idOfOwner, String nameOfConselho) {
        Query<Entity> query;
        QueryResults<Entity> results;

        query = Query.newEntityQueryBuilder().setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME)
                .setFilter(StructuredQuery.CompositeFilter.and(
                        StructuredQuery.PropertyFilter.eq(ENTITY_PROPERTY_ID_OWNER, idOfOwner),
                        StructuredQuery.PropertyFilter.eq(ENTITY_PROPERTY_CONSELHO_OF_TERRAIN, nameOfConselho)))
                .build();

        results = datastore.run(query);

        if (!results.hasNext())
            return Result.error(Response.Status.NO_CONTENT, "No terrains were found.");
        List<String> list = new ArrayList<>();
        while (results.hasNext()) {
            Entity tmp = results.next();
            list.add(tmp.getValue(ENTITY_PROPERTY_ID_OWNER).toString() +
                    "/" + tmp.getValue(ENTITY_PROPERTY_NAME_OF_TERRAIN).toString());
        }
        return Result.ok(list, "");
    }

    @Override
    public Result<ChunkResultData> queryTerrainsInChunk(LatLng pos) {
        double[] chunkSize = new double[2];
        int[] chunkCoords = new int[2];
        LatLng topRight = new LatLng();
        LatLng bottomLeft;
        try {
            chunkCoords = madeira.worldCoordsToChunk(pos.getLng(), pos.getLat());
            chunkSize = madeira.getChunkSize();
            bottomLeft = madeira.chunkToWorldCoords(chunkCoords[0], chunkCoords[1]);
            topRight = new LatLng((float) (bottomLeft.getLat() + chunkSize[0]), (float) (bottomLeft.getLng() + chunkSize[1]));
        } catch (OutOfChunkBounds ignored) {
        }

        try {
            chunkCoords = portugal.worldCoordsToChunk(pos.getLng(), pos.getLat());
            chunkSize = portugal.getChunkSize();
            bottomLeft = portugal.chunkToWorldCoords(chunkCoords[0], chunkCoords[1]);
            topRight = new LatLng((float) (bottomLeft.getLat() + chunkSize[0]), (float) (bottomLeft.getLng() + chunkSize[1]));
        } catch (OutOfChunkBounds e) {
            return Result.error(Response.Status.BAD_REQUEST, "Position " + pos + " out of bounds");
        }
        String chunk = String.format("(%s, %s)", chunkCoords[0], chunkCoords[1]);
        ChunkResultData resultData = this.chunkCacheManager.get(chunk, "data", ChunkResultData.class);

        if (resultData != null) {
            return Result.ok(resultData, "");
        }

        Set<PolygonDrawingData> result = new HashSet<>();
        Key chunkKey = datastore.newKeyFactory().setKind("Chunk").newKey(chunk);
        Entity selectedChunk = datastore.get(chunkKey);
        if (selectedChunk != null){
            String parcelsIDs = selectedChunk.getString("parcels_id");
            String[] parcels = parcelsIDs.split("/");

            KeyFactory selectedParcelKeyFactory = datastore.newKeyFactory().setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME);
            KeyFactory selectedParcelNotApprovedKeyFactory = datastore.newKeyFactory().setKind(PARCELAS_TO_BE_APPROVED_TABLE_NAME);

            Key selectedParcelKey;
            Key selectedParcelNotApprovedKey;
            for (String parcelID : parcels) {
                selectedParcelKey = selectedParcelKeyFactory.newKey(parcelID);
                Entity selectedParcel = datastore.get(selectedParcelKey);

                if (selectedParcel != null) {
                    LatLng[] points = JSON.decode(selectedParcel.getString(ENTITY_PROPERTY_COORDINATES), LatLng[].class);
                    PolygonDrawingData data = new PolygonDrawingData(points, Random.color(), true);
                    result.add(data);
                    //continue;
                }

                selectedParcelNotApprovedKey = selectedParcelNotApprovedKeyFactory.newKey(parcelID);
                Entity selectedParcelNotApproved = datastore.get(selectedParcelNotApprovedKey);

                if (selectedParcelNotApproved != null) {
                    LatLng[] points = JSON.decode(selectedParcelNotApproved.getString(ENTITY_PROPERTY_COORDINATES), LatLng[].class);
                    PolygonDrawingData data = new PolygonDrawingData(points, Random.color(), false);
                    result.add(data);
                }
            }
        }

        resultData = new ChunkResultData(chunk, topRight, bottomLeft, result);
        chunkCacheManager.put(chunk, "data", resultData);

        return Result.ok(resultData, "");
    }

    public Result<ChunkResultData> queryTerrainsInChunk(int chunkX, int chunkY) {
        double[] chunkSize = new double[2];
        int[] chunkCoords = new int[]{chunkX,chunkY};
        LatLng topRight = new LatLng();
        LatLng bottomLeft;
        try {
            chunkSize = madeira.getChunkSize();
            bottomLeft = madeira.chunkToWorldCoords(chunkCoords[0], chunkCoords[1]);
            topRight = new LatLng((float) (bottomLeft.getLat() + chunkSize[0]), (float) (bottomLeft.getLng() + chunkSize[1]));
        } catch (OutOfChunkBounds ignored) {
        }

        try {
            chunkSize = portugal.getChunkSize();
            bottomLeft = portugal.chunkToWorldCoords(chunkCoords[0], chunkCoords[1]);
            topRight = new LatLng((float) (bottomLeft.getLat() + chunkSize[0]), (float) (bottomLeft.getLng() + chunkSize[1]));
        } catch (OutOfChunkBounds e) {
            return Result.error(Response.Status.BAD_REQUEST, "Position " + Arrays.toString(chunkCoords) + " out of bounds");
        }
        String chunk = String.format("(%s, %s)", chunkCoords[0], chunkCoords[1]);
        ChunkResultData resultData = this.chunkCacheManager.get(chunk, "data", ChunkResultData.class);

        Set<PolygonDrawingData> result = new HashSet<>();
        if (resultData != null) {
            ChunkResultData data = new ChunkResultData(chunk, topRight, bottomLeft, result);
            return Result.ok(data, "");
        }

        Key chunkKey = datastore.newKeyFactory().setKind("Chunk").newKey(chunk);
        Entity selectedChunk = datastore.get(chunkKey);

        String parcelsIDs = selectedChunk.getString("parcels_id");
        String[] parcels = parcelsIDs.split("/");

        KeyFactory selectedParcelKeyFactory = datastore.newKeyFactory().setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME);
        KeyFactory selectedParcelNotApprovedKeyFactory = datastore.newKeyFactory().setKind(PARCELAS_TO_BE_APPROVED_TABLE_NAME);

        Key selectedParcelKey;
        Key selectedParcelNotApprovedKey;
        for (String parcelID : parcels) {
            selectedParcelKey = selectedParcelKeyFactory.newKey(parcelID);
            selectedParcelNotApprovedKey = selectedParcelNotApprovedKeyFactory.newKey(parcelID);
            Entity selectedParcel = datastore.get(selectedParcelKey);
            Entity selectedParcelNotApproved = datastore.get(selectedParcelNotApprovedKey);

            if (selectedParcel != null) {
                LatLng[] points = JSON.decode(selectedParcel.getString(ENTITY_PROPERTY_COORDINATES), LatLng[].class);
                PolygonDrawingData data = new PolygonDrawingData(points, Random.color(), true);
                result.add(data);
            }

            if (selectedParcelNotApproved != null) {
                LatLng[] points = JSON.decode(selectedParcelNotApproved.getString(ENTITY_PROPERTY_COORDINATES), LatLng[].class);
                PolygonDrawingData data = new PolygonDrawingData(points, Random.color(), false);
                result.add(data);
            }
        }
        resultData = new ChunkResultData(chunk, topRight, bottomLeft, result);
        chunkCacheManager.put(chunk, "data", resultData);

        return Result.ok(resultData, "");
    }

    @Override
    public Result<List<TerrainResultData>> getAllTerrainsInRelationToCoordinate(float coordinate, String relativePosition) {
        Query<Entity> query;
        QueryResults<Entity> results;

        switch (relativePosition) {
            case "NORTH":
                query = Query.newEntityQueryBuilder()
                        .setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME)
                        .setFilter(StructuredQuery.PropertyFilter.gt(ENTITY_PROPERTY_TOP_MOST_POINT, coordinate))
                        .build();
                break;
            case "SOUTH":
                query = Query.newEntityQueryBuilder()
                        .setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME)
                        .setFilter(StructuredQuery.PropertyFilter.lt(ENTITY_PROPERTY_BOTTOM_MOST_POINT, coordinate))
                        .build();
                break;
            case "WEST":
                query = Query.newEntityQueryBuilder()
                        .setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME)
                        .setFilter(StructuredQuery.PropertyFilter.lt(ENTITY_PROPERTY_LEFT_MOST_POINT, coordinate))
                        .build();
                break;
            case "EAST":
                query = Query.newEntityQueryBuilder()
                        .setKind(PARCELAS_THAT_ARE_APPROVED_TABLE_NAME)
                        .setFilter(StructuredQuery.PropertyFilter.gt(ENTITY_PROPERTY_RIGHT_MOST_POINT, coordinate))
                        .build();
                break;
            default:
                return Result.error(Response.Status.BAD_REQUEST, "The relative position is not valid.");
        }

        results = datastore.run(query);

        if (!results.hasNext())
            return Result.error(Response.Status.NO_CONTENT, "No terrains were found.");
        List<TerrainResultData> list = new ArrayList<>();
        while (results.hasNext()) {
            Entity tmp = results.next();
            Key ownerKey = datastore.newKeyFactory().setKind("TerrainOwner").newKey(tmp.getKey().getNameOrId().toString());
            Entity owner = datastore.get(ownerKey);
            LatLng[] points = JSON.decode(tmp.getString(ENTITY_PROPERTY_COORDINATES), LatLng[].class);

            TerrainResultData resultData = new TerrainResultData(
                    points,
                    new LatLng(),
                    Random.color(),
                    new TerrainIdentifierData(
                            tmp.getString(ENTITY_PROPERTY_NAME_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_CONSELHO_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_DISTRITO_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_SECTION_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_NUMBER_ARTICLE_OF_TERRAIN)
                    ),
                    new TerrainOwner(
                            owner.getString("owner_name"),
                            owner.getString("owner_id"),
                            owner.getString("owner_address"),
                            owner.getString("owner_telephone"),
                            owner.getString("owner_smartphone")
                    ),
                    new TerrainInfoData(
                            tmp.getString(ENTITY_PROPERTY_DESCRIPTION_OF_TERRAIN),
                            tmp.getString(ENTITY_PROPERTY_TYPE_OF_SOIL_COVERAGE),
                            tmp.getString(ENTITY_PROPERTY_CURRENT_USE_OF_SOIL),
                            tmp.getString(ENTITY_PROPERTY_PREVIOUS_USE_OF_SOIL),
                            new String[]{},
                            new LatLng[]{}
                    )
            );
            list.add(resultData);
        }
        return Result.ok(list, "");
    }


}
