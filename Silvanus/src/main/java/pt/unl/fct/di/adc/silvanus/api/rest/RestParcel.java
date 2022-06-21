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
     * @return ok if everything went correctly, an error otherwise
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response doRegister(@CookieParam(TOKEN) String token, TerrainData terrainData);

    /**
     * This method is used to check if a terrain intersects any other terrain.
     *
     * @param terrain the terrain to evaluate
     * @return ok if everything went correctly, an error otherwise
     */
    @PUT
    @Path("/intersect")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response checkIfTerrainHasIntersections(@CookieParam(TOKEN) String token, LatLng[] terrain);

    /**
     * This method is used to approve a terrain. A terrain can only be approved by the
     * competent authorities.
     *
     * @param token       the token the user is given upon login
     * @param userID      the id of the user who'se terrain is being approved
     * @param terrainName the name of the terrain that is being approved
     * @return ok if everything went correctly, an error otherwise
     */
    @PUT
    @Path("/approve/{" + IDENTIFIER + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response approveTerrain(@CookieParam(TOKEN) String token, @PathParam(IDENTIFIER) String userID, @QueryParam("terrain") @DefaultValue(" ") String terrainName);

    /**
     * This method is used to deny a terrain. A terrain can only be denied by the
     * competent authorities.
     *
     * @param token       the token the user is given upon login
     * @param userID      the id of the user who'se terrain is being denied
     * @param terrainName the name of the terrain that is being denied
     * @return ok if everything went correctly, an error otherwise
     */
    @PUT
    @Path("/deny/{" + IDENTIFIER + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response denyTerrain(@CookieParam(TOKEN) String token, @PathParam(IDENTIFIER) String userID, @QueryParam("terrain") String terrainName);

    /**
     * This method is used to delete a terrain. A terrain can be deleted by
     * it's creator or the competent authorities.
     *
     * @param token       the token the user is given upon login
     * @param userID      the id of the user who'se terrain is being deleted
     * @param terrainName the name of the terrain that is being deleted
     * @return ok if everything went correctly, an error otherwise
     */
    @DELETE
    @Path("/delete/{" + IDENTIFIER + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response deleteTerrain(@CookieParam(TOKEN) String token, @PathParam(IDENTIFIER) String userID, @QueryParam("terrain") String terrainName);

    /**
     * This method is used to list all the terrains a certain user as registered. Those terrains
     * need to be approved to be included in this list.
     *
     * @param idOfUser the id of the user who'se terrains are requested
     * @return ok if everything went correctly, an error otherwise
     */
    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainUser(@CookieParam(TOKEN) String token, @QueryParam("user") @DefaultValue(" ") String idOfUser);

    /**
     * This method is used to list all the terrains in a certain county (concelho). Those terrains
     * need to be approved to be included in this list.
     *
     * @param nameOfCounty name of the county to query
     * @return ok if everything went correctly, an error otherwise
     */
    @POST
    @Path("/list/county/{county}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainInCounty(@CookieParam(TOKEN) String token, @PathParam("county") String nameOfCounty);

    /**
     * This method is used to list all the terrains in a certain district (distrito). Those terrains
     * need to be approved to be included in this list.
     *
     * @param nameOfDistrict name of the district to query
     * @return ok if everything went correctly, an error otherwise
     */
    @POST
    @Path("/list/district/{district}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainInDistrict(@CookieParam(TOKEN) String token, @PathParam("district") String nameOfDistrict);

    /**
     * This method is used to list all the terrains a user has in a certain county (conselho). Those
     * terrain need to be approved to be included in this list.
     *
     * @param idOfOwner id of the owner of the terrain
     * @param county    name of the county in which to query
     * @return ok if everything went correctly, an error otherwise
     */
    @POST
    @Path("/list/{idOfOwner}/county")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainsOfUserInCounty(@PathParam("idOfOwner") String idOfOwner, @QueryParam("terrain") String county);

    /**
     * This method is used to list all the terrains in a given chunk.
     * @param pos position of the user on the map
     * @return ok if everything went correctly, an error otherwise
     */
    @POST
    @Path("/list/chunk/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainsInChunk(LatLng pos);
}
