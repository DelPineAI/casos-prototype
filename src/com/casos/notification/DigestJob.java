package com.casos.notification;

import com.casos.domain.ClinicianRepository;

/** Weekly scheduled digest send. */
public class DigestJob {

    private final ClinicianRepository clinicians;
    private final DigestBuilder builder;
    private final EmailSender sender;

    public DigestJob(ClinicianRepository clinicians, DigestBuilder builder, EmailSender sender) {
        this.clinicians = clinicians;
        this.builder = builder;
        this.sender = sender;
    }

    /** Returns the number of digests sent. */
    public int runWeekly() {
        return 0;
    }
}
