package pt.unl.fct.di.adc.silvanus.resources;

import io.jsonwebtoken.Claims;
import pt.unl.fct.di.adc.silvanus.api.rest.RestGlobalStats;
import pt.unl.fct.di.adc.silvanus.data.global_stats.Stat;
import pt.unl.fct.di.adc.silvanus.implementation.global_stats.GlobalStatsImplementation;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/global_stats")
public class GlobalStatsResource implements RestGlobalStats {

    private GlobalStatsImplementation impl = new GlobalStatsImplementation();

    public GlobalStatsResource() {

    }

    @Override
    public Response fetchCountyStats(String token) {
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<List<Stat>> result = impl.getStatsOnCountyOfTerrains();

        if (!result.isOK()) {
            return Response.status(result.error()).entity(result.statusMessage()).build();
        }

        return Response.ok().build();
    }

    @Override
    public Response fetchDistrictStats(String token) {
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<List<Stat>> result = impl.getStatsOnDistrictOfTerrains();

        if (!result.isOK()) {
            return Response.status(result.error()).entity(result.statusMessage()).build();
        }

        return Response.ok().build();
    }

    @Override
    public Response fetchAreaOfUser(String token, String userID) {
        Claims jws = TOKEN.verifyToken(token);


        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<List<Stat>> result = impl.getStatsOnAreaOfTerrainOfUser(userID);

        if (!result.isOK()) {
            return Response.status(result.error()).entity(result.statusMessage()).build();
        }

        return Response.ok().build();
    }

    @Override
    public Response sendDataToDB(String token) {
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<Void> result = impl.sendDataToDB();

        if (!result.isOK()) {
            return Response.status(result.error()).entity(result.statusMessage()).build();
        }

        return Response.ok().build();
    }
}
