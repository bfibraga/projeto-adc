package pt.unl.fct.di.adc.silvanus.util.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.datastore.Entity;
import pt.unl.fct.di.adc.silvanus.data.parcel.Coordinate;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;
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
    Result<String> checkIfParcelHasIntersections(Coordinate[] parcela);

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
    Result<List<String>> getAllTerrainsOfUser(String ownerTerrain);

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


}
