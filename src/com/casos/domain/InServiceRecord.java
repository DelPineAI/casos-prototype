package com.casos.domain;

import java.time.LocalDate;

/** One completed in-service training with its expiry date (S5.2, S5.3). */
public class InServiceRecord {

    private final InServiceType type;
    private final LocalDate completedOn;
    private final LocalDate expiresOn;

    public InServiceRecord(InServiceType type, LocalDate completedOn, LocalDate expiresOn) {
        this.type = type;
        this.completedOn = completedOn;
        this.expiresOn = expiresOn;
    }

    public boolean isExpired(LocalDate today) {
        return expiresOn.isBefore(today);
    }

    public boolean expiresWithin(int days, LocalDate today) {
        return !isExpired(today) && !expiresOn.isAfter(today.plusDays(days));
    }

    public InServiceType type() {
        return type;
    }

    public LocalDate completedOn() {
        return completedOn;
    }

    public LocalDate expiresOn() {
        return expiresOn;
    }
}
