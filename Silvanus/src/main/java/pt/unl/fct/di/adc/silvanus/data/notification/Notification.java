package pt.unl.fct.di.adc.silvanus.data.notification;

public class Notification {

    private String senderOfNotification;
    private String receiverOfNotification;
    private String textOfNotification;

    public Notification() {

    }

    public Notification(String senderOfNotification, String receiverOfNotification, String textOfNotification) {
        this.senderOfNotification = senderOfNotification;
        this.receiverOfNotification = receiverOfNotification;
        this.textOfNotification = textOfNotification;
    }

    public String getSenderOfNotification() {
        return senderOfNotification;
    }

    public String getReceiverOfNotification() {
        return receiverOfNotification;
    }

    public String getTextOfNotification() {
        return textOfNotification;
    }
}
