package pt.unl.fct.di.adc.silvanus.api.rest;

import pt.unl.fct.di.adc.silvanus.data.notification.Notification;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static pt.unl.fct.di.adc.silvanus.api.rest.RestInterface.IDENTIFIER;
import static pt.unl.fct.di.adc.silvanus.api.rest.RestInterface.TOKEN;
import static pt.unl.fct.di.adc.silvanus.api.rest.RestNotifications.PATH;

public interface RestNotifications {

    String PATH = "/notification";
    /**
     * Metodo que e usada para uma notificacao poder ser enviada de um user para outro
     * @param data informacao sobre a notificacao a ser enviada
     * @return -> a resposta do servidor neste pedido
     */
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    Response send(@CookieParam(TOKEN) String token, Notification data);

    /**
     * Metodo que e usado para fazer uma querie a base de dados para saber quais as notificacoes que o user recebeu
     * @param userID -> id do user cujas notificacoes queremos obter
     * @return -> a resposta do servidor neste pedido
     */
    @GET
    @Path("/list/{" + IDENTIFIER + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    Response list(@CookieParam(TOKEN) String token, @PathParam(IDENTIFIER) String userID);

    /**
     * Metodo que e usada para apagar uma notificacao do user que invoca esta operacao.
     * @param data informacao sobre a notificacao a ser enviada
     * @return -> a resposta do servidor neste pedido
     */
    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    Response delete(@CookieParam(IDENTIFIER) String token, Notification data);
}
