package com.casos.domain;

import java.util.List;
import java.util.Optional;

public interface ClinicianRepository {

    void save(Clinician c);

    Optional<Clinician> findById(ClinicianId id);

    Optional<Clinician> findByEmail(String email);

    /** Clinicians subscribed to new-case alerts in a county (S8.2). */
    List<Clinician> subscribedTo(County county);
}
