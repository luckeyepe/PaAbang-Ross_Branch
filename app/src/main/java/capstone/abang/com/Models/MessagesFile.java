package capstone.abang.com.Models;


public class MessagesFile {
    private long Date;
    private String Message;
    private String Notified;
    private String Receiver;
    private String Sender;
    private String Status;

    public MessagesFile() {

    }

    public MessagesFile(long date, String message, String notified, String receiver, String sender, String status) {
        this.Date = date;
        this.Message = message;
        this.Notified = notified;
        this.Receiver = receiver;
        this.Sender = sender;
        this.Status = status;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getNotified() {
        return Notified;
    }

    public void setNotified(String notified) {
        Notified = notified;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
