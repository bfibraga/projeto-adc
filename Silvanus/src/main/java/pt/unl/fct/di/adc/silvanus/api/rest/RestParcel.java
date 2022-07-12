package pt.unl.fct.di.adc.silvanus.api.rest;

import pt.unl.fct.di.adc.silvanus.data.terrain.LatLng;
import pt.unl.fct.di.adc.silvanus.data.terrain.TerrainData;

import static pt.unl.fct.di.adc.silvanus.api.rest.RestInterface.*;

import javax.print.attribute.standard.Media;
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
     * This method is used to list all the terrains a certain user as registered. Those terrains
     * need to be approved to be included in this list.
     *
     * @param idOfUser the id of the user who'se terrains are requested
     * @return ok if everything went correctly, an error otherwise
     */
    @POST
    @Path("/list/pending")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response listPendingTerrainUser(@CookieParam(TOKEN) String token, @QueryParam("user") @DefaultValue(" ") String idOfUser);

    @GET
    @Path("/list/pending/county/{county}")
    @Produces(MediaType.APPLICATION_JSON)
    Response listPendingTerrainCounty(@CookieParam(TOKEN) String token, @PathParam("county") String county);

    @GET
    @Path("/list/pending/district/{district}")
    @Produces(MediaType.APPLICATION_JSON)
    Response listPendingTerrainDistrict(@CookieParam(TOKEN) String token, @PathParam("district") String district);

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
    Response listTerrainsOfUserInCounty(@CookieParam(TOKEN) String token, @PathParam("idOfOwner") String idOfOwner, @QueryParam("terrain") String county);

    /**
     * This method is used to list all the terrains in a certain chunk. The chunk is identified by a
     * latitude value and a longitude value.
     *
     * @param token the token the user gets upon login
     * @param lat the latitude to query
     * @param lng the longitude to query
     * @return ok if everything went correctly, an error otherwise
     */
    @GET
    @Path("/list/chunk")
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainsInChunk(@CookieParam(TOKEN) String token, @QueryParam("lat") double lat, @QueryParam("lng") double lng);


    /**
     * This method is used to list all the approved terrains that are in a relative position (NORTH, SOUTH, EAST, WEST) to a
     * certain coordinate regardless of user.
     * @param token the token the user gets upon login
     * @param coordinate the coordinate that serves as a base point
     * @param orientation the relation between the coordinate and the terrain
     * @return ok if everything went correctly, an error otherwise
     */
    @POST
    @Path("/list/search/coordinate/{coordinate}/orientation/{orientation}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response listTerrainsInRelationToCoordinate(@CookieParam(TOKEN) String token, @PathParam("coordinate") float coordinate, @PathParam("orientation") String orientation);
}
