package com.casos.domain;

/** Which emails a clinician wants to receive. Owned by Clinician (Round 4, W7). */
public class NotificationPreferences {

    private boolean countyAlerts;
    private boolean weeklyDigest;
    private boolean inServiceReminders;

    public NotificationPreferences() {
        this.countyAlerts = true;
        this.weeklyDigest = true;
        this.inServiceReminders = true;
    }

    public boolean countyAlerts() {
        return countyAlerts;
    }

    public boolean weeklyDigest() {
        return weeklyDigest;
    }

    public boolean inServiceReminders() {
        return inServiceReminders;
    }

    public void setCountyAlerts(boolean on) {
        this.countyAlerts = on;
    }

    public void setWeeklyDigest(boolean on) {
        this.weeklyDigest = on;
    }

    public void setInServiceReminders(boolean on) {
        this.inServiceReminders = on;
    }
}
