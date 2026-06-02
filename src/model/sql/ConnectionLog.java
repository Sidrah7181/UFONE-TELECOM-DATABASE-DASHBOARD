package model.sql;

import java.sql.Timestamp;

/**
 * POJO for a row in the 'connection_log' table.
 */
public class ConnectionLog {

    private long logId;
    private int userId;
    private int towerId;

    private Timestamp connectTime;
    private Timestamp disconnectTime;

    private double signalStrength;
    private double dataUsedMb;
    private int durationMins;

    public ConnectionLog() {}

    public ConnectionLog(long logId,
                         int userId,
                         int towerId,
                         Timestamp connectTime,
                         Timestamp disconnectTime,
                         double signalStrength,
                         double dataUsedMb,
                         int durationMins) {

        this.logId = logId;
        this.userId = userId;
        this.towerId = towerId;
        this.connectTime = connectTime;
        this.disconnectTime = disconnectTime;
        this.signalStrength = signalStrength;
        this.dataUsedMb = dataUsedMb;
        this.durationMins = durationMins;
    }

    // ───────── GETTERS ─────────

    public long getLogId() { return logId; }
    public int getUserId() { return userId; }
    public int getTowerId() { return towerId; }

    public Timestamp getConnectTime() { return connectTime; }
    public Timestamp getDisconnectTime() { return disconnectTime; }

    public double getSignalStrength() { return signalStrength; }
    public double getDataUsedMb() { return dataUsedMb; }
    public int getDurationMins() { return durationMins; }

    // ───────── SETTERS ─────────

    public void setLogId(long logId) { this.logId = logId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTowerId(int towerId) { this.towerId = towerId; }

    public void setConnectTime(Timestamp connectTime) {
        this.connectTime = connectTime;
    }

    public void setDisconnectTime(Timestamp disconnectTime) {
        this.disconnectTime = disconnectTime;
    }

    public void setSignalStrength(double signalStrength) {
        this.signalStrength = signalStrength;
    }

    public void setDataUsedMb(double dataUsedMb) {
        this.dataUsedMb = dataUsedMb;
    }

    public void setDurationMins(int durationMins) {
        this.durationMins = durationMins;
    }

    public boolean isActiveSession() {
        return disconnectTime == null;
    }
}