package pt.unl.fct.di.adc.silvanus.api.rest;

import pt.unl.fct.di.adc.silvanus.data.community.CommunityData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface RestCommunity {

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
    Response delete(@CookieParam(RestInterface.TOKEN) String token,  @QueryParam("name") @DefaultValue(" ") String name);

    @PUT
    @Path("/join/{community_name}")
    @Produces(MediaType.APPLICATION_JSON + RestInterface.CHARSET)
    Response join(@CookieParam(RestInterface.TOKEN) String token, @QueryParam(RestInterface.IDENTIFIER) @DefaultValue(" ") String identifier, @PathParam("community_name") String name);

    @PUT
    @Path("/exit/{community_name}")
    @Produces(MediaType.APPLICATION_JSON + RestInterface.CHARSET)
    Response exit(@CookieParam(RestInterface.TOKEN) String token, @QueryParam(RestInterface.IDENTIFIER) @DefaultValue(" ") String identifier, @PathParam("community_name") String name);
}
