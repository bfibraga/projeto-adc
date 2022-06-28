package pt.unl.fct.di.adc.silvanus.api.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.datastore.Entity;
import pt.unl.fct.di.adc.silvanus.data.parcel.LatLng;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;
import pt.unl.fct.di.adc.silvanus.data.parcel.result.TerrainResultData;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import java.util.List;

public interface Parcel {

    /**
     * <p>This method is used to create a terrain. "Creating a terrain" entails adding the entity to a table that serves as a "wait list".</p>
     * A terrain is well created (valid) if and only if: the terrain is inside either the Bounding Box of Portugal Continental or
     * the Bounding Box of one of the islands (wether from the "Arquipélago da Madeira" or the "Arquipélago dos Açores")
     *
     * @param terrainData the necessary data to add a terrain, that data is outlined in the class "TerrainData"
     * @return of if everything went correctly, an error otherwise
     */
    Result<Void> createParcel(TerrainData terrainData) throws JsonProcessingException;


    /**
     * This method checks if the terrain in the argument intersects any other terrain that is in the database (registered or in the waitlist)
     *
     * @param parcela parcela a verificar a interseção
     * @return of if there is no intersection, an error otherwise
     */
    Result<String> checkIfParcelHasIntersections(LatLng[] parcela);

    /**
     * This method approves a terrain (moves it from the "wait list" to the table of approved terrains)
     *
     * @param ownerTerrain name of the owner of the terrain
     * @param nameTerrain  name of the terrain
     */
    Result<Void> approveTerrain(String ownerTerrain, String nameTerrain);

    /**
     * This method denys a terrain (removes it from the "wait list")
     *
     * @param ownerTerrain name of the owner of the terrain
     * @param nameTerrain  name of the terrain
     */
    Result<Void> denyTerrain(String ownerTerrain, String nameTerrain);


    /**
     * This method is used to delete a terrain from one of the tables.
     *
     * @param ownerTerrain the owner of the terrain (the id of the owner)
     * @param nameTerrain  the name of the terrain (user inserted)
     * @return ok if the terrain was removed, an error otherwise
     */
    Result<Void> deleteTerrain(String ownerTerrain, String nameTerrain);

    /**
     * Lists all the terrains that are registered by an owner
     *
     * @param ownerTerrain the name of the owner
     * @return a list with all the terrains
     */
    Result<List<TerrainResultData>> getAllTerrainsOfUser(String ownerTerrain);

    /**
     * Lists all the terrains that are registered by an owner
     *
     * @param ownerTerrain the name of the owner
     * @return a list with all the terrains
     */
    Result<List<TerrainResultData>> getAllPendingTerrainsOfUser(String ownerTerrain);

    /**
     * Generates a list of all the terrains in a given county (cocelho)
     * @param nameOfCounty name of the county to query
     * @return a list with all the terrains if they exist, an error otherwise
     */
    Result<List<Entity>> getAllTerrainsInCounty(String nameOfCounty);

    /**
     * Generates a list of all the terrains in a given district (distrito)
     * @param nameOfDistrict name of the district to query
     * @return a list with all the terrains if they exist, an error otherwise
     */
    Result<List<Entity>> getAllTerrainsInDistrict(String nameOfDistrict);

    /**
     * Generates a list of strings composed of the owner of a terrain and the name
     * of the same terrain, that are in a certain county (conselho)
     * @param idOfOwner id of the owner of the terrain
     * @param nameOfConselho name of the county in which to query
     * @return a list if they exist, an error otherwise
     */
    Result<List<String>> queryTerrainsOfUserInCounty(String idOfOwner, String nameOfConselho);

    /**
     * Genereates a
     * @param pos
     * @return
     */
    Result<List<LatLng[]>> queryTerrainsInChunk(LatLng pos);

    /**
     * Generates a list of terrains that are either west, east, north or south of a coordinate (represented as a float)
     * @param coordinate the coordinate in which to query
     * @param relativePosition the position the terrain is in relation to the coordinate (WEST, EAST, NORTH, SOUTH)
     * @return a list if they exist, an error otherwise
     */
    Result<List<TerrainResultData>> getAllTerrainsInRelationToCoordinate(float coordinate, String relativePosition);

}
