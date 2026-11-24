package com.casos.clinician;

import com.casos.domain.ClinicianRepository;
import com.casos.domain.DomainEventBus;
import java.time.Clock;

/** Daily scan that publishes InServiceExpiring 30 days ahead (S5.3). */
public class InServiceExpiryScanner {

    private final ClinicianRepository clinicians;
    private final DomainEventBus bus;
    private final Clock clock;
    private final int leadDays;

    public InServiceExpiryScanner(ClinicianRepository clinicians, DomainEventBus bus, Clock clock) {
        this.clinicians = clinicians;
        this.bus = bus;
        this.clock = clock;
        this.leadDays = 30;
    }

    /** Returns the number of events published. */
    public int runDaily() {
        return 0;
    }
}
