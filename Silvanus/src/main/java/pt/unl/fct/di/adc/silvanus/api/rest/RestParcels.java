package pt.unl.fct.di.adc.silvanus.api.rest;

import pt.unl.fct.di.adc.silvanus.data.parcel.TerrainData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface RestParcels {
    String PATH = "/parcel";

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + RestInterface.CHARSET)
    Response doRegister(@CookieParam("token") String token, TerrainData dataParcela);
}
