package pt.unl.fct.di.adc.silvanus.api.rest;

import pt.unl.fct.di.adc.silvanus.data.parcel.ParcelaData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static pt.unl.fct.di.adc.silvanus.api.rest.RestParcels.CHARSET;

@Produces(MediaType.APPLICATION_JSON + CHARSET)
public interface RestParcels {
    String CHARSET = ";charset=utf-8";
    String PATH = "/parcel";

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    Response doRegister(@CookieParam("token") String token, ParcelaData dataParcela);
}
