package pt.unl.fct.di.adc.silvanus.implementation;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.adc.silvanus.data.notification.Notification;
import pt.unl.fct.di.adc.silvanus.resources.NotificationResource;
import pt.unl.fct.di.adc.silvanus.resources.ParcelaResource;
import pt.unl.fct.di.adc.silvanus.api.impl.Notifications;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class NotificationImplementation implements Notifications {

    private static final Logger LOG = Logger.getLogger(NotificationResource.class.getName());
    private static final String NOTIFICATION_TABLE_NAME = "Notification";
    private static final String PROPERTY_ID_OF_SENDER = "id_of_sender";
    private static final String PROPERTY_ID_OF_RECEIVER = "id_of_receiver";
    private static final String PROPERTY_TEXT_OF_NOTIFICATION = "text_of_notification";
    public static final int MAXIMUM_NUMBER_OF_MESSAGES = 5;

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private final KeyFactory notificationKeyFactory = datastore.newKeyFactory().setKind(NOTIFICATION_TABLE_NAME);

    public NotificationImplementation() {

    }

    @Override
    public Result<Void> send(Notification data) {
        // TODO meter na base de dados uma entidade que controle o numero de mensagens enviadas no sentido sender -> receiver
        LOG.fine("Attempt to send notification from: " + data.getSender() + " to: " + data.getReceiver() + ". ");

        Key notificationKey = notificationKeyFactory.newKey(data.getSender() + "->" + data.getReceiver() + ":" + data.getDescription());

        Transaction txn = datastore.newTransaction();

        try {

            if (!userExists(data.getReceiver())) {
                LOG.severe("O usuario que era suposto receber a notificacao nao foi encontrado.");
                return Result.error(Response.Status.NOT_FOUND, "Destinatario invalido.");
            }
            if (!canSendMoreNotifications(data.getSender(), data.getReceiver())) {
                LOG.warning("O remetente ja nao pode enviar mais mensagens ao destinatario. O destinatario tem de apagar mensagens do remetente "
                        + "para que a operacao corra com sucesso.");
                return Result.error(Response.Status.FORBIDDEN, "Não e possivel enviar mais mensagens neste sentido.");
            }

            Entity transaction = Entity.newBuilder(notificationKey).set(PROPERTY_ID_OF_SENDER, data.getSender()).set(PROPERTY_ID_OF_RECEIVER, data.getReceiver()).set(PROPERTY_TEXT_OF_NOTIFICATION, data.getDescription()).build();
            txn.put(transaction);
            txn.commit();
            LOG.fine("Foi enviada a notificacao.");
            return Result.ok();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @Override
    public Result<String> list(String userID) {
        LOG.fine("Query was started.");
        Query<Entity> query;
        QueryResults<Entity> results;

        query = Query.newEntityQueryBuilder().setKind(NOTIFICATION_TABLE_NAME).setFilter(
                StructuredQuery.CompositeFilter.and(StructuredQuery.PropertyFilter.eq(PROPERTY_ID_OF_RECEIVER, userID))).build();
        results = datastore.run(query);
        List<Entity> entityList = new LinkedList<>();
        while (results.hasNext()) {
            Entity tmp = results.next();
            entityList.add(tmp);
        }
        return Result.ok(g.toJson(entityList));
    }

    @Override
    public Result<Void> delete(Notification data) {
        LOG.fine("Deletion process of notification has started.");
        Transaction txn = datastore.newTransaction();
        try {
            Key notificationKey = notificationKeyFactory.newKey(data.getSender() + "->" + data.getReceiver() + ":" + data.getDescription());
            Entity notificationEntity = txn.get(notificationKey);
            if (notificationEntity == null) {
                LOG.severe("A notificacao pretendida nao foi encontrada. Verifique se todos os campos estao bem feitos.");
                return Result.error(Response.Status.NOT_FOUND, "Notificacao nao encontrada.");
            }
            txn.delete(notificationKey);
            txn.commit();
            return Result.ok();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }


    }

    public boolean canSendMoreNotifications(String senderID, String receiverID) {
        LOG.fine("Query was started.");
        Query<Entity> query;
        QueryResults<Entity> results;

        query = Query.newEntityQueryBuilder().setKind(NOTIFICATION_TABLE_NAME).setFilter(StructuredQuery.CompositeFilter.and(
                StructuredQuery.PropertyFilter.eq(PROPERTY_ID_OF_SENDER, senderID),
                StructuredQuery.PropertyFilter.eq(PROPERTY_ID_OF_RECEIVER, receiverID))).build();
        results = datastore.run(query);
        int i = 0;
        while (results.hasNext()) {
            results.next();
            i++;
        }
        return i < MAXIMUM_NUMBER_OF_MESSAGES;
    }

    public boolean userExists(String user_username) {
        LOG.fine("Query was started.");
        Query<Entity> query;
        QueryResults<Entity> results;

        query = Query.newEntityQueryBuilder().setKind("UserCredentials").setFilter(
                StructuredQuery.CompositeFilter.and(StructuredQuery.PropertyFilter.eq("usr_username", user_username))).build();
        results = datastore.run(query);
        return results.hasNext();
    }

}
