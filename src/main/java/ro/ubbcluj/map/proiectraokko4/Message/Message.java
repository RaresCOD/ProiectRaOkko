package ro.ubbcluj.map.proiectraokko4.Message;


import ro.ubbcluj.map.proiectraokko4.domain.Entity;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    private Utilizator from;
    private List<Utilizator> to;
    private String msg;
    private Message replyMsg;
    private LocalDateTime data;

    public Message(Utilizator from, List<Utilizator> to, String msg) {
        this.from = from;
        this.to = to;
        this.msg = msg;
    }

    public Message getReplyMsg() {
        return replyMsg;
    }

    public void setReplyMsg(Message replyMsg) {
        this.replyMsg = replyMsg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public LocalDateTime getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", msg=" + msg +
                '}';
    }
}
