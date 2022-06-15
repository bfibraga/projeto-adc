package pt.unl.fct.di.adc.silvanus.util.interfaces;

import pt.unl.fct.di.adc.silvanus.data.parcel.Coordinate;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;
import pt.unl.fct.di.adc.silvanus.util.Pair;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface RestParcel {

    /**
     * This method is used to register a terrain and send it to the "wait list".
     *
     * @param terrainData data of the terrain to register
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    Response doRegister(TerrainData terrainData);

    /**
     * This method is use to check if a terrain intersects any other terrain.
     *
     * @param terrain the terrain to evaluate
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/check_intersection")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    Response checkIfTerrainHasIntersections(Coordinate[] terrain);

    /**
     * This method is used to approve a terrain. It receives a pair of strings
     *
     * @param pair this pair represents the owner and name of the terrain to approve
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/approve_parcel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    Response approveTerrain(Pair<String> pair);

    /**
     * This method is used to approve a terrain. It receives a pair of strings
     *
     * @param pair this pair represents the owner and name of the terrain to approve
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/deny_parcel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    Response denyTerrain(Pair<String> pair);

    /**
     * This method is used to delete a terrain (it is already approved). It an array of strings
     *
     * @param info the information about the terrain
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/delete_terrain")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response deleteTerrain(String[] info);

    /**
     * This method is used to list all the terrains a certain user as registered. Those terrains
     * need to be approved to be included in this list.
     *
     * @param idOfUser the id of the user who'se terrains are requested
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/terrains_of_user/{idOfUser}")
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainUser(@PathParam("idOfUser") String idOfUser);

    /**
     * This method is used to list all the terrains in a certain county (concelho). Those terrains
     * need to be approved to be included in this list.
     *
     * @param nameOfCounty name of the county to query
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/terrains_of_user/{nameOfCounty}")
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainInCounty(@PathParam("nameOfCounty") String nameOfCounty);

    /**
     * This method is used to list all the terrains in a certain district (distrito). Those terrains
     * need to be approved to be included in this list.
     *
     * @param nameOfDistrict name of the district to query
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/terrains_of_user/{nameOfDistrict}")
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainInDistrict(@PathParam("nameOfDistrict") String nameOfDistrict);
}
