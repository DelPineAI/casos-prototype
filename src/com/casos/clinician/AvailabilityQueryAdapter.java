package com.casos.clinician;

import com.casos.domain.AvailabilityQuery;
import com.casos.domain.ClinicianId;
import com.casos.domain.ClinicianRepository;
import com.casos.domain.WeeklySchedule;
import java.util.Optional;

/** Implements the AvailabilityQuery port so the board never imports this module (Round 2, F4). */
public class AvailabilityQueryAdapter implements AvailabilityQuery {

    private final ClinicianRepository clinicians;

    public AvailabilityQueryAdapter(ClinicianRepository clinicians) {
        this.clinicians = clinicians;
    }

    @Override
    public Optional<WeeklySchedule> availabilityOf(ClinicianId id) {
        return clinicians.findById(id).map(c -> c.availability());
    }
}
