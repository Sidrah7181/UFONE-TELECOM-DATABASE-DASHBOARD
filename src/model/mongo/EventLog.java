package model.mongo;

/** POJO for a MongoDB document in the 'event_log' collection. */
public class EventLog {
    private String id;          // MongoDB ObjectId as string
    private String eventType;
    private String towerId;
    private String userId;
    private String timestamp;
    private String details;

    public EventLog() {}

    public EventLog(String id, String eventType, String towerId,
                    String userId, String timestamp, String details) {
        this.id        = id;
        this.eventType = eventType;
        this.towerId   = towerId;
        this.userId    = userId;
        this.timestamp = timestamp;
        this.details   = details;
    }

    public String getId()        { return id; }
    public String getEventType() { return eventType; }
    public String getTowerId()   { return towerId; }
    public String getUserId()    { return userId; }
    public String getTimestamp() { return timestamp; }
    public String getDetails()   { return details; }

    public void setId(String id)              { this.id        = id; }
    public void setEventType(String et)       { this.eventType = et; }
    public void setTowerId(String tid)        { this.towerId   = tid; }
    public void setUserId(String uid)         { this.userId    = uid; }
    public void setTimestamp(String ts)       { this.timestamp = ts; }
    public void setDetails(String d)          { this.details   = d; }

    /** Returns a formatted JSON-like string for display cards. */
    public String toDisplayJson() {
        return "{\n" +
               "  \"_id\"       : \"" + id        + "\",\n" +
               "  \"event_type\": \"" + eventType + "\",\n" +
               "  \"tower_id\"  : \"" + towerId   + "\",\n" +
               "  \"user_id\"   : \"" + userId    + "\",\n" +
               "  \"timestamp\" : \"" + timestamp + "\",\n" +
               "  \"details\"   : \"" + details   + "\"\n" +
               "}";
    }
}
