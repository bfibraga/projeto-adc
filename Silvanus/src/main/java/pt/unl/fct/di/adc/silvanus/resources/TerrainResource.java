package pt.unl.fct.di.adc.silvanus.resources;

import com.google.cloud.datastore.Entity;
import io.jsonwebtoken.Claims;
import pt.unl.fct.di.adc.silvanus.data.terrain.LatLng;
import pt.unl.fct.di.adc.silvanus.data.terrain.TerrainData;
import pt.unl.fct.di.adc.silvanus.data.terrain.result.ChunkResultData;
import pt.unl.fct.di.adc.silvanus.data.terrain.result.TerrainResultData;
import pt.unl.fct.di.adc.silvanus.implementation.terrain.TerrainImplementation;
import pt.unl.fct.di.adc.silvanus.implementation.user.UserImplementation;
import pt.unl.fct.di.adc.silvanus.api.rest.RestParcel;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/parcel")
public class TerrainResource implements RestParcel {

    private TerrainImplementation impl = new TerrainImplementation();
    private UserImplementation userImplementation = new UserImplementation();

    public TerrainResource() {
    }

    @Override
    public Response doRegister(String token, TerrainData terrainData) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        /*Set<String> scope = jws.get("scope", HashSet.class);

        if (!scope.contains("can_create_own_terrain")){
            return Response.status(Response.Status.FORBIDDEN).entity("Not Enough permission to execute").build();
        }*/

        terrainData.getCredentials().setUserID(jws.getSubject());
        Result<Void> result = impl.createParcel(terrainData);

        if (!result.isOK()) {
            return Response.status(result.error()).entity(result.statusMessage()).build();
        }

        return Response.ok().build();
    }

    @Override
    public Response checkIfTerrainHasIntersections(String token, LatLng[] terrain) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<String> result = impl.checkIfParcelHasIntersections(terrain);
        if (result.isOK())
            return Response.ok().build();
        else
            return Response.status(result.error()).entity(result.statusMessage()).build();
    }

    @Override
    public Response approveTerrain(String token, String userID, String terrainName) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        //Permissions to run this function
        /*Set<String> scope = jws.get("scope", HashSet.class);

        //TODO Alter this permmission
        if (!scope.contains("can_create_own_terrain")){
            return Response.status(Response.Status.FORBIDDEN).entity("Not Enough permission to execute").build();
        }*/

        Result<Void> result = impl.approveTerrain(userID, terrainName);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().build();
    }

    @Override
    public Response denyTerrain(String token, String userID, String terrainName) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        /*Set<String> scope = jws.get("scope", HashSet.class);

        //TODO Alter this permmission
        if (!scope.contains("can_create_own_terrain")){
            return Response.status(Response.Status.FORBIDDEN).entity("Not Enough permission to execute").build();
        }*/

        Result<Void> result = impl.denyTerrain(userID, terrainName);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().build();
    }

    @Override
    public Response deleteTerrain(String token, String userID, String terrainName) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        /*Set<String> scope = jws.get("scope", HashSet.class);

        //TODO Alter this permmission
        if (!scope.contains("can_create_own_terrain")){
            return Response.status(Response.Status.FORBIDDEN).entity("Not Enough permission to execute").build();
        }*/

        Result<Void> result = impl.deleteTerrain(userID, terrainName);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok(result).build();
    }

    @Override
    public Response listTerrainUser(String token, String idOfUser) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        /*Set<String> scope = jws.get("scope", HashSet.class);

        //TODO Alter this permmission
        if (!scope.contains("can_create_own_terrain")){
            return Response.status(Response.Status.FORBIDDEN).entity("Not Enough permission to execute").build();
        }*/

        if (idOfUser.trim().equals("")) {
            idOfUser = jws.getSubject();
        }

        Result<List<TerrainResultData>> result = impl.getAllTerrainsOfUser(idOfUser);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok(result.value()).build();
    }

    @Override
    public Response listPendingTerrainUser(String token, String idOfUser) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        /*Set<String> scope = jws.get("scope", HashSet.class);

        //TODO Alter this permmission
        if (!scope.contains("can_create_own_terrain")){
            return Response.status(Response.Status.FORBIDDEN).entity("Not Enough permission to execute").build();
        }*/

        if (idOfUser.trim().equals("")) {
            idOfUser = jws.getSubject();
        }

        Result<List<TerrainResultData>> result = impl.getAllPendingTerrainsOfUser(idOfUser);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok(result.value()).build();
    }

    @Override
    public Response listTerrainInCounty(String token, String nameOfCounty) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        /*Set<String> scope = jws.get("scope", HashSet.class);

        //TODO Alter this permmission
        if (!scope.contains("can_create_own_terrain")){
            return Response.status(Response.Status.FORBIDDEN).entity("Not Enough permission to execute").build();
        }*/

        Result<List<Entity>> result = impl.getAllTerrainsInCounty(nameOfCounty);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok(result.value()).build();
    }

    @Override
    public Response listTerrainInDistrict(String token, String nameOfDistrict) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        /*Set<String> scope = jws.get("scope", HashSet.class);

        //TODO Alter this permmission
        if (!scope.contains("can_create_own_terrain")){
            return Response.status(Response.Status.FORBIDDEN).entity("Not Enough permission to execute").build();
        }*/

        Result<List<Entity>> result = impl.getAllTerrainsInDistrict(nameOfDistrict);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok(result.value()).build();
    }

    @Override
    public Response listTerrainsOfUserInCounty(String token, String idOfOwner, String county) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<List<String>> result = impl.queryTerrainsOfUserInCounty(idOfOwner, county);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().entity(result.value()).build();
    }

    @Override
    public Response listTerrainsInChunk(String token, double lat, double lng) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        LatLng pos = new LatLng((float) lat, (float) lng);
        Result<ChunkResultData> result = impl.queryTerrainsInChunk(pos);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().entity(result.value()).build();
    }

    @Override
    public Response listTerrainsInRelationToCoordinate(String token, float coordinate, String orientation) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }
        Result<List<TerrainResultData>> result = impl.getAllTerrainsInRelationToCoordinate(coordinate, orientation);

        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().entity(result.value()).build();
    }
}
