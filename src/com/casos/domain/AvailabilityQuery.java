package com.casos.domain;

import java.util.Optional;

/** Port the board uses to fetch a clinician's availability for overlap ranking (S5.5). */
public interface AvailabilityQuery {

    Optional<WeeklySchedule> availabilityOf(ClinicianId id);
}
