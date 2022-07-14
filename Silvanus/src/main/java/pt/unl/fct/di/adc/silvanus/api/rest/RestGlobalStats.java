package pt.unl.fct.di.adc.silvanus.api.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static pt.unl.fct.di.adc.silvanus.api.rest.RestInterface.*;

public interface RestGlobalStats {

    @GET
    @Path("/county_stats")
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response fetchCountyStats(@CookieParam(TOKEN) String token);

    @GET
    @Path("/district_stats")
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response fetchDistrictStats(@CookieParam(TOKEN) String token);

    @GET
    @Path("{userID}/get_area")
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response fetchAreaOfUser(@CookieParam(TOKEN) String token, @PathParam("userID") String userID);

    @POST
    @Path("/send_to_db")
    @Consumes(MediaType.APPLICATION_JSON + CHARSET)
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response sendDataToDB(@CookieParam(TOKEN) String token);
}
