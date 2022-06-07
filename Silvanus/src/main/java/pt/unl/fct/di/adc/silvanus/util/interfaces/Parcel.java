package pt.unl.fct.di.adc.silvanus.util.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import pt.unl.fct.di.adc.silvanus.data.parcel.Coordinate;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

public interface Parcel {

    /**
     * <p>This method is used to create a terrain. "Creating a terrain" entails adding the entity to a table that serves as a "wait list".</p>
     * A terrain is well created (valid) if and only if: the terrain is inside either the Bounding Box of Portugal Continental or
     * the Bounding Box of one of the islands (wether from the "Arquipélago da Madeira" or the "Arquipélago dos Açores")
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
     * @param nameTerrain name of the terrain
     */
    Result<Void> approveTerrain(String ownerTerrain, String nameTerrain);

    /**
     * This method denys a terrain (removes it from the "wait list")
     *
     * @param ownerTerrain name of the owner of the terrain
     * @param nameTerrain name of the terrain
     */
    Result<Void> denyTerrain(String ownerTerrain, String nameTerrain);


}
