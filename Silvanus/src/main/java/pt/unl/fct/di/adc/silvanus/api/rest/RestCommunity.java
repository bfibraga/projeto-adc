package pt.unl.fct.di.adc.silvanus.api.rest;

import pt.unl.fct.di.adc.silvanus.data.community.CommunityData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface RestCommunity {

    String PATH = "/community";

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON + RestInterface.CHARSET)
    Response list(@CookieParam(RestInterface.TOKEN) String token, @QueryParam(RestInterface.IDENTIFIER) @DefaultValue(" ") String identifier);

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + RestInterface.CHARSET)
    Response create(@CookieParam(RestInterface.TOKEN) String token, CommunityData data);

    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON + RestInterface.CHARSET)
    Response delete(@CookieParam(RestInterface.TOKEN) String token, @QueryParam("community_name") String name);

    @PUT
    @Path("/join")
    @Produces(MediaType.APPLICATION_JSON + RestInterface.CHARSET)
    Response join(@CookieParam(RestInterface.TOKEN) String token, @QueryParam("community_name") String name);

    @PUT
    @Path("/exit")
    @Produces(MediaType.APPLICATION_JSON + RestInterface.CHARSET)
    Response exit(@CookieParam(RestInterface.TOKEN) String token, @QueryParam("community_name") String name);

    @GET
    @Path("/members")
    @Produces(MediaType.APPLICATION_JSON + RestInterface.CHARSET)
    Response listMembers(@CookieParam(RestInterface.TOKEN) String token, @QueryParam("community_name") String name);
}
