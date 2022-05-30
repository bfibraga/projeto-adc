package pt.unl.fct.di.adc.silvanus.resources;

import pt.unl.fct.di.adc.silvanus.data.notification.Notification;
import pt.unl.fct.di.adc.silvanus.implementation.NotificationImplementation;
import pt.unl.fct.di.adc.silvanus.api.rest.RestNotifications;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/notification")
public class NotificationResource implements RestNotifications {

    private final NotificationImplementation implementation;

    public NotificationResource() {
        this.implementation = new NotificationImplementation();
    }

    @Override
    public Response sendNotification(Notification data) {
        Result res = implementation.sendNotification(data);
        if (res.isOK())
            return Response.ok().build();
        else
            return Response.status(res.error()).entity(res.statusMessage()).build();
    }

    @Override
    public Response listNotification(String userID) {
        Result<String> res = implementation.listNotificationOfUser(userID);
        if (res.isOK())
            return Response.ok(res.value()).build();
        else
            return Response.status(res.error()).entity(res.statusMessage()).build();
    }

    @Override
    public Response deleteNotification(Notification data) {
        Result res = implementation.deleteNotification(data);
        if (res.isOK())
            return Response.ok().build();
        else
            return Response.status(res.error()).entity(res.statusMessage()).build();
    }
}
