package pt.unl.fct.di.adc.silvanus.data.notification;

public class Notification {

    private String sender;
    private String receiver;
    private String description;

    public Notification() {
    }

    public Notification(String sender, String receiver){
        this(sender, receiver, "");
    }

    public Notification(String sender, String receiver, String description) {
        this.sender = sender;
        this.receiver = receiver;
        this.description = description;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDescription() {
        return description;
    }
}
