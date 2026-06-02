package model.sql;

import java.sql.Date;

/** POJO representing a row in the 'user_account' table. */
public class User {

    private int userId;
    private String fullName;
    private Date dateBirth;
    private Date regDate;
    private boolean highUsageFlag;
    private boolean isBlocked;

    public User() {}

    public User(int userId, String fullName, Date dateBirth,
                Date regDate, boolean highUsageFlag, boolean isBlocked) {
        this.userId = userId;
        this.fullName = fullName;
        this.dateBirth = dateBirth;
        this.regDate = regDate;
        this.highUsageFlag = highUsageFlag;
        this.isBlocked = isBlocked;
    }

    public int getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public Date getDateBirth() { return dateBirth; }
    public Date getRegDate() { return regDate; }
    public boolean isHighUsageFlag() { return highUsageFlag; }
    public boolean isBlocked() { return isBlocked; }

    public void setUserId(int id) { this.userId = id; }
    public void setFullName(String n) { this.fullName = n; }
    public void setDateBirth(Date d) { this.dateBirth = d; }
    public void setRegDate(Date r) { this.regDate = r; }
    public void setHighUsageFlag(boolean h) { this.highUsageFlag = h; }
    public void setBlocked(boolean b) { this.isBlocked = b; }

    @Override
    public String toString() {
        return "[" + userId + "] " + fullName + (isBlocked ? " [BLOCKED]" : "");
    }
}