package pt.unl.fct.di.adc.silvanus.api.rest;

import pt.unl.fct.di.adc.silvanus.data.notification.Notification;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface RestNotifications {

    /**
     * Metodo que e usada para uma notificacao poder ser enviada de um user para outro
     * @param data informacao sobre a notificacao a ser enviada
     * @return -> a resposta do servidor neste pedido
     */
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    Response sendNotification(Notification data);

    /**
     * Metodo que e usado para fazer uma querie a base de dados para saber quais as notificacoes que o user recebeu
     * @param userID -> id do user cujas notificacoes queremos obter
     * @return -> a resposta do servidor neste pedido
     */
    @GET
    @Path("/list/{identifier}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    Response listNotification(@PathParam("identifier") String userID);

    /**
     * Metodo que e usada para apagar uma notificacao do user que invoca esta operacao.
     * @param data informacao sobre a notificacao a ser enviada
     * @return -> a resposta do servidor neste pedido
     */
    @POST
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    Response deleteNotification(Notification data);
}
