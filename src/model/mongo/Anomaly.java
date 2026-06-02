package model.mongo;

/** POJO for a MongoDB document in the 'anomaly_log' collection. */
public class Anomaly {
    private String id;
    private String anomalyType;
    private String riskLevel;   // Low / Medium / High / Critical
    private String towerId;
    private String userId;
    private String anomalyTime;
    private String description;

    public Anomaly() {}

    public Anomaly(String id, String anomalyType, String riskLevel,
                   String towerId, String userId, String anomalyTime, String description) {
        this.id          = id;
        this.anomalyType = anomalyType;
        this.riskLevel   = riskLevel;
        this.towerId     = towerId;
        this.userId      = userId;
        this.anomalyTime = anomalyTime;
        this.description = description;
    }

    public String getId()          { return id; }
    public String getAnomalyType() { return anomalyType; }
    public String getRiskLevel()   { return riskLevel; }
    public String getTowerId()     { return towerId; }
    public String getUserId()      { return userId; }
    public String getAnomalyTime() { return anomalyTime; }
    public String getDescription() { return description; }

    public void setId(String id)               { this.id          = id; }
    public void setAnomalyType(String t)       { this.anomalyType = t; }
    public void setRiskLevel(String r)         { this.riskLevel   = r; }
    public void setTowerId(String tid)         { this.towerId     = tid; }
    public void setUserId(String uid)          { this.userId      = uid; }
    public void setAnomalyTime(String at)      { this.anomalyTime = at; }
    public void setDescription(String d)       { this.description = d; }

    public String toDisplayJson() {
        return "{\n" +
               "  \"_id\"         : \"" + id          + "\",\n" +
               "  \"anomaly_type\": \"" + anomalyType + "\",\n" +
               "  \"risk_level\"  : \"" + riskLevel   + "\",\n" +
               "  \"tower_id\"    : \"" + towerId     + "\",\n" +
               "  \"user_id\"     : \"" + userId      + "\",\n" +
               "  \"time\"        : \"" + anomalyTime + "\",\n" +
               "  \"description\" : \"" + description + "\"\n" +
               "}";
    }
}
