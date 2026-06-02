package model.sql;

/** POJO representing a row in the 'tower' table. */
public class Tower {
    private int    towerId;
    private int    regionId;
    private String towerType;    // 4G / 5G / 4G/5G
    private String installDate;
    private int    maxCapacity;
    private String towerLoc;
    private String towerStatus;  // Active / Inactive / Maintenance / Destroyed

    public Tower() {}

    public Tower(int towerId, int regionId, String towerType, String installDate,
                 int maxCapacity, String towerLoc, String towerStatus) {
        this.towerId     = towerId;
        this.regionId    = regionId;
        this.towerType   = towerType;
        this.installDate = installDate;
        this.maxCapacity = maxCapacity;
        this.towerLoc    = towerLoc;
        this.towerStatus = towerStatus;
    }

    public int    getTowerId()     { return towerId; }
    public int    getRegionId()    { return regionId; }
    public String getTowerType()   { return towerType; }
    public String getInstallDate() { return installDate; }
    public int    getMaxCapacity() { return maxCapacity; }
    public String getTowerLoc()    { return towerLoc; }
    public String getTowerStatus() { return towerStatus; }

    public void setTowerId(int id)          { this.towerId     = id; }
    public void setRegionId(int rid)        { this.regionId    = rid; }
    public void setTowerType(String t)      { this.towerType   = t; }
    public void setInstallDate(String d)    { this.installDate = d; }
    public void setMaxCapacity(int c)       { this.maxCapacity = c; }
    public void setTowerLoc(String l)       { this.towerLoc    = l; }
    public void setTowerStatus(String s)    { this.towerStatus = s; }

    @Override
    public String toString() {
        return "[" + towerId + "] " + towerType + " | Region:" + regionId + " | " + towerStatus;
    }
}
