package runningtracker.data.model;

public class Notification {
    private String from;
    private String fromName;
    private String message;

    public Notification() {
    }

    public Notification(String from, String fromName, String message) {
        this.from = from;
        this.fromName = fromName;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public String getFromName() {
        return fromName;
    }

    public String getMessage() {
        return message;
    }
}
