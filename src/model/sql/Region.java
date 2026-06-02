package model.sql;

/** Simple POJO representing a row in the 'region' table. */
public class Region {
    private int    regionId;
    private String regionName;
    private String networkMode;  // 4G / 5G / 4G/5G / Unknown
    private String regionType;   // Urban / Rural / Sub-urban
    private String status;

    public Region() {}

    public Region(int regionId, String regionName, String networkMode,
                  String regionType, String status) {
        this.regionId   = regionId;
        this.regionName = regionName;
        this.networkMode = networkMode;
        this.regionType = regionType;
        this.status     = status;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int    getRegionId()    { return regionId; }
    public String getRegionName()  { return regionName; }
    public String getNetworkMode() { return networkMode; }
    public String getRegionType()  { return regionType; }
    public String getStatus()      { return status; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setRegionId(int id)          { this.regionId   = id; }
    public void setRegionName(String n)      { this.regionName = n; }
    public void setNetworkMode(String m)     { this.networkMode = m; }
    public void setRegionType(String t)      { this.regionType = t; }
    public void setStatus(String s)          { this.status     = s; }

    @Override
    public String toString() {
        return "[" + regionId + "] " + regionName + " (" + networkMode + ")";
    }
}
