package pt.unl.fct.di.adc.silvanus.resources;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import pt.unl.fct.di.adc.silvanus.api.rest.RestCommunity;
import pt.unl.fct.di.adc.silvanus.data.community.CommunityData;
import pt.unl.fct.di.adc.silvanus.implementation.community.CommunityImplementation;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@Path("/community")
public class CommunityResource implements RestCommunity {

    private CommunityImplementation impl;

    public CommunityResource() {
    }

    @Override
    public Response list(String token, String identifier) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null){
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<String> result = Result.ok();
        return Response.ok().entity(result).build();
    }

    @Override
    public Response create(String token, CommunityData data) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null){
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }
        System.out.println(data);
        Result<CommunityData> result = Result.ok(data,"Community Created");
        return Response.ok().entity(result).build();
    }

    @Override
    public Response delete(String token, String name) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null){
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<String> result = Result.ok();
        return Response.ok().entity(result).build();
    }

    @Override
    public Response join(String token, String identifier, String name) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null){
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<String> result = Result.ok();
        return Response.ok().entity(result).build();
    }

    @Override
    public Response exit(String token, String identifier, String name) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null){
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<String> result = Result.ok();
        return Response.ok().entity(result).build();
    }

    @Override
    public Response listMembers(String token, String name) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null){
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<String> result = Result.ok();
        return Response.ok().entity(result).build();
    }
}
