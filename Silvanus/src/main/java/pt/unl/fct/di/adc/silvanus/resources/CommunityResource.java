package pt.unl.fct.di.adc.silvanus.resources;

import com.google.cloud.storage.Acl;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import pt.unl.fct.di.adc.silvanus.api.rest.RestCommunity;
import pt.unl.fct.di.adc.silvanus.data.community.CommunityData;
import pt.unl.fct.di.adc.silvanus.data.user.result.UserInfoVisible;
import pt.unl.fct.di.adc.silvanus.implementation.community.CommunityImplementation;
import pt.unl.fct.di.adc.silvanus.implementation.user.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Path(RestCommunity.PATH)
public class CommunityResource implements RestCommunity {

    private final CommunityImplementation impl = new CommunityImplementation();
    private final UserImplementation userImplementation = new UserImplementation();

    public CommunityResource() {
    }

    @Override
    public Response list(String token, String identifier) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<List<CommunityData>> result = impl.list();

        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();

        return Response.ok().entity(result.value()).build();
    }

    @Override
    public Response create(String token, CommunityData data) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }
        data.setResponsible(jws.getSubject());

        Result<Void> result = impl.create(data);

        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();

        return Response.ok().entity(result).build();
    }

    @Override
    public Response delete(String token, String name) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<Void> result = impl.delete(name, jws.getSubject());

        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();

        return Response.ok().entity(result).build();
    }

    @Override
    public Response join(String token, String name) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<Void> result = impl.join(name, jws.getSubject());

        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();

        return Response.ok().entity(result.value()).build();
    }

    @Override
    public Response exit(String token, String name) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<Void> result = impl.exit(name, jws.getSubject());;

        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();

        return Response.ok().entity(result.value()).build();
    }

    @Override
    public Response listMembers(String token, String name) {
        //Token verifycation
        Claims jws = TOKEN.verifyToken(token);

        System.out.println(name);

        if (jws == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<List<String>> result = impl.members(name);

        if (!result.isOK())
            return Response.status(result.error()).entity(result.statusMessage()).build();

        return Response.ok().entity(result.value()).build();
    }
}
