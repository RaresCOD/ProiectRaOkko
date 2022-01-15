package ro.ubbcluj.map.proiectraokko4.domain;


import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    private User from;
    private List<User> to;
    private String msg;
    private Message replyMsg;
    private LocalDateTime data;

    public Message(User from, List<User> to, String msg) {
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

    public User getFrom() {
        return from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public void setTo(List<User> to) {
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
