package pt.unl.fct.di.adc.silvanus.api.impl;

import pt.unl.fct.di.adc.silvanus.data.notification.Notification;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Set;

public interface Notifications {

    Result<Void> send(Notification data);

    Result<Set<Notification>> list(String userID);

    Result<Void> delete(Notification data);

    //TODO Review all this support functions
    boolean canSendMoreNotifications(String senderID, String receiverID);

    boolean userExists(String user_username);
}
