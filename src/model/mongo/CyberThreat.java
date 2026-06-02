package model.mongo;

/** POJO for a MongoDB document in the 'cyber_threat' collection. */
public class CyberThreat {
    private String id;
    private String attackType;
    private String severityLevel;  // Low / Medium / High / Critical
    private String status;         // Detected / Investigating / Mitigated / Resolved
    private int    affectedTowers;
    private String description;
    private String detectedAt;

    public CyberThreat() {}

    public CyberThreat(String id, String attackType, String severityLevel,
                       String status, int affectedTowers, String description, String detectedAt) {
        this.id             = id;
        this.attackType     = attackType;
        this.severityLevel  = severityLevel;
        this.status         = status;
        this.affectedTowers = affectedTowers;
        this.description    = description;
        this.detectedAt     = detectedAt;
    }

    public String getId()             { return id; }
    public String getAttackType()     { return attackType; }
    public String getSeverityLevel()  { return severityLevel; }
    public String getStatus()         { return status; }
    public int    getAffectedTowers() { return affectedTowers; }
    public String getDescription()    { return description; }
    public String getDetectedAt()     { return detectedAt; }

    public void setId(String id)                  { this.id             = id; }
    public void setAttackType(String a)           { this.attackType     = a; }
    public void setSeverityLevel(String s)        { this.severityLevel  = s; }
    public void setStatus(String s)               { this.status         = s; }
    public void setAffectedTowers(int n)          { this.affectedTowers = n; }
    public void setDescription(String d)          { this.description    = d; }
    public void setDetectedAt(String dt)          { this.detectedAt     = dt; }

    public String toDisplayJson() {
        return "{\n" +
               "  \"_id\"            : \"" + id             + "\",\n" +
               "  \"attack_type\"    : \"" + attackType     + "\",\n" +
               "  \"severity\"       : \"" + severityLevel  + "\",\n" +
               "  \"status\"         : \"" + status         + "\",\n" +
               "  \"affected_towers\": "  + affectedTowers  + ",\n" +
               "  \"detected_at\"    : \"" + detectedAt     + "\",\n" +
               "  \"description\"    : \"" + description    + "\"\n" +
               "}";
    }
}
