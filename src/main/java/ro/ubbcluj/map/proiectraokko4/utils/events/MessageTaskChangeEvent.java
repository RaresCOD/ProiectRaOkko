package ro.ubbcluj.map.proiectraokko4.utils.events;



import ro.ubbcluj.map.proiectraokko4.Message.Message;

public class MessageTaskChangeEvent implements Event {
    private ChangeEventType type;
    private Message data, oldData;

    public MessageTaskChangeEvent(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }
    public MessageTaskChangeEvent(ChangeEventType type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}