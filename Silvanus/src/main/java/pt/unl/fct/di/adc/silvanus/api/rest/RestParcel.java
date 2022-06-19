package pt.unl.fct.di.adc.silvanus.api.rest;

import pt.unl.fct.di.adc.silvanus.data.parcel.LatLng;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;
import static pt.unl.fct.di.adc.silvanus.api.rest.RestInterface.*;

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
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response doRegister(TerrainData terrainData);

    /**
     * This method is use to check if a terrain intersects any other terrain.
     *
     * @param terrain the terrain to evaluate
     * @return of if everything went correctly, an error otherwise
     */
    @PUT
    @Path("/intersect")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response checkIfTerrainHasIntersections(LatLng[] terrain);

    /**
     * This method is used to approve a terrain. It receives a pair of strings
     *
     * @param pair this pair represents the owner and name of the terrain to approve
     * @return of if everything went correctly, an error otherwise
     */
    @PUT
    @Path("/approve/{" + IDENTIFIER + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response approveTerrain(@PathParam(IDENTIFIER) String userID, @QueryParam("terrain") String terrainName);

    /**
     * This method is used to approve a terrain. It receives a pair of strings
     *
     * @param pair this pair represents the owner and name of the terrain to approve
     * @return of if everything went correctly, an error otherwise
     */
    @PUT
    @Path("/deny/{" + IDENTIFIER + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response denyTerrain(@PathParam(IDENTIFIER) String userID, @QueryParam("terrain") String terrainName);

    /**
     * This method is used to delete a terrain (it is already approved). It an array of strings
     *
     * @param info the information about the terrain
     * @return of if everything went correctly, an error otherwise
     */
    @DELETE
    @Path("/delete/{" + IDENTIFIER + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response deleteTerrain(@PathParam(IDENTIFIER) String userID, @QueryParam("terrain") String terrainName);

    /**
     * This method is used to list all the terrains a certain user as registered. Those terrains
     * need to be approved to be included in this list.
     *
     * @param idOfUser the id of the user who'se terrains are requested
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/list/user/{user}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainUser(@PathParam("user") String idOfUser);

    /**
     * This method is used to list all the terrains in a certain county (concelho). Those terrains
     * need to be approved to be included in this list.
     *
     * @param nameOfCounty name of the county to query
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/list/country/{country}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainInCounty(@PathParam("country") String nameOfCounty);

    /**
     * This method is used to list all the terrains in a certain district (distrito). Those terrains
     * need to be approved to be included in this list.
     *
     * @param nameOfDistrict name of the district to query
     * @return of if everything went correctly, an error otherwise
     */
    @POST
    @Path("/list/district/{district}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainInDistrict(@PathParam("district") String nameOfDistrict);
}
