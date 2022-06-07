package pt.unl.fct.di.adc.silvanus.resources;

import pt.unl.fct.di.adc.silvanus.data.notification.Notification;
import pt.unl.fct.di.adc.silvanus.implementation.NotificationImplementation;
import pt.unl.fct.di.adc.silvanus.api.rest.RestNotifications;
import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class NotificationResource implements RestNotifications {

    private final NotificationImplementation implementation;

    public NotificationResource() {
        this.implementation = new NotificationImplementation();
    }

    @Override
    public Response send(String token, Notification data) {
        Result<Void> res = implementation.send(data);
        if (res.isOK())
            return Response.ok().build();
        else
            return Response.status(res.error()).entity(res.statusMessage()).build();
    }

    @Override
    public Response list(String token, String userID) {
        Result<String> res = implementation.list(userID);
        if (res.isOK())
            return Response.ok(res.value()).build();
        else
            return Response.status(res.error()).entity(res.statusMessage()).build();
    }

    @Override
    public Response delete(String token, Notification data) {
        Result<Void> res = implementation.delete(data);
        if (res.isOK())
            return Response.ok().build();
        else
            return Response.status(res.error()).entity(res.statusMessage()).build();
    }
}
