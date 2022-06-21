package pt.unl.fct.di.adc.silvanus.resources;

import com.google.cloud.datastore.Entity;
import io.jsonwebtoken.Claims;
import pt.unl.fct.di.adc.silvanus.data.parcel.LatLng;
import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;
import pt.unl.fct.di.adc.silvanus.implementation.ParcelImplementation;
import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.api.rest.RestParcel;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/parcel")
public class ParcelaResource implements RestParcel {

    private ParcelImplementation impl = new ParcelImplementation();
    private UserImplementation userImplementation = new UserImplementation();

    public ParcelaResource() {
    }

    @Override
    public Response doRegister(String token, TerrainData terrainData) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null){
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

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
        if (result == null)
            return Response.ok("Nao ha intersecoes.").build();
        else
            return Response.ok("Foram detetadas intersecoes com a parcela:" + result.statusMessage()).build();
    }

    @Override
    public Response approveTerrain(String token, String userID, String terrainName) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

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

        Result<List<String>> result = impl.getAllTerrainsOfUser(idOfUser);
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

        Result<List<Entity>> result = impl.getAllTerrainsInDistrict(nameOfDistrict);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok(result.value()).build();
    }

    @Override
    public Response listTerrainsOfUserInCounty(String idOfOwner, String county) {
        Result<List<String>> result = impl.queryTerrainsOfUserInCounty(idOfOwner, county);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().entity(result.value()).build();
    }

    @Override
    public Response listTerrainsInChunk(String chunk) {
        Result<List<LatLng[]>> result = impl.queryTerrainsInChunk(chunk);
        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();
        return Response.ok().entity(result.value()).build();
    }
}
