package pt.unl.fct.di.adc.silvanus.resources;

import io.jsonwebtoken.Claims;
import pt.unl.fct.di.adc.silvanus.data.notification.Notification;
import pt.unl.fct.di.adc.silvanus.data.user.result.UserInfoVisible;
import pt.unl.fct.di.adc.silvanus.implementation.NotificationImplementation;
import pt.unl.fct.di.adc.silvanus.api.rest.RestNotifications;
import pt.unl.fct.di.adc.silvanus.implementation.UserImplementation;
import pt.unl.fct.di.adc.silvanus.util.TOKEN;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path(RestNotifications.PATH)
public class NotificationResource implements RestNotifications {

    private final NotificationImplementation notificationImplementation;
    private final UserImplementation userImplementation;

    public NotificationResource() {
        this.notificationImplementation = new NotificationImplementation();
        this.userImplementation = new UserImplementation();
    }

    @Override
    public Response send(String token, Notification data) {
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null){
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<Set<UserInfoVisible>> userResult = userImplementation.getUser(token, data.getReceiver());

        if (!userResult.isOK() || userResult.value().isEmpty()){
            return Response.status(userResult.error()).entity(userResult.statusMessage()).build();
        }

        Result<Void> res = notificationImplementation.send(data);
        if (res.isOK())
            return Response.ok().build();
        else
            return Response.status(res.error()).entity(res.statusMessage()).build();
    }

    @Override
    public Response list(String token, String userID) {
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null){
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<Set<UserInfoVisible>> userResult = userImplementation.getUser(token, userID);

        if (!userResult.isOK() || userResult.value().isEmpty()){
            return Response.status(userResult.error()).entity(userResult.statusMessage()).build();
        }

        Result<Set<Notification>> res = notificationImplementation.list(userID);
        if (res.isOK())
            return Response.ok(res.value()).build();
        else
            return Response.status(res.error()).entity(res.statusMessage()).build();
    }

    @Override
    public Response delete(String token, Notification data) {
        Claims jws = TOKEN.verifyToken(token);

        if (jws == null){
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Token").build();
        }

        Result<Void> res = notificationImplementation.delete(data);
        if (res.isOK())
            return Response.ok().build();
        else
            return Response.status(res.error()).entity(res.statusMessage()).build();
    }
}
