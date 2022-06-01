package pt.unl.fct.di.adc.silvanus.api.impl;

import pt.unl.fct.di.adc.silvanus.data.notification.Notification;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public interface Notifications {

    Result<Void> sendNotification(Notification data);

    Result<String> listNotificationOfUser(String userID);

    Result<Void> deleteNotification(Notification data);

    boolean canSendMoreNotifications(String senderID, String receiverID);

    boolean userExists(String user_username);
}
