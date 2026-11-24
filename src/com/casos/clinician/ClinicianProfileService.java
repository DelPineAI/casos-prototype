package com.casos.clinician;

import com.casos.domain.Clinician;
import com.casos.domain.ClinicianId;
import com.casos.domain.ClinicianRepository;
import com.casos.domain.County;
import com.casos.domain.Credential;
import com.casos.domain.InServiceRecord;
import com.casos.domain.SavedFilter;
import com.casos.domain.SelfReportedRoster;
import com.casos.domain.WeeklySchedule;
import java.time.Clock;

/** Clinician profile use cases (U5.0, U6.0, S5.1 to S5.4, S6.1 to S6.4, S8.1, S8.2). */
public class ClinicianProfileService {

    private final ClinicianRepository clinicians;
    private final Clock clock;

    public ClinicianProfileService(ClinicianRepository clinicians, Clock clock) {
        this.clinicians = clinicians;
        this.clock = clock;
    }

    public Clinician register(String email, Credential credential) {
        return null;
    }

    public void updateCredential(ClinicianId id, Credential c) {
    }

    public void addInService(ClinicianId id, InServiceRecord r) {
    }

    public void setAvailability(ClinicianId id, WeeklySchedule s) {
    }

    public void recordRoster(ClinicianId id, SelfReportedRoster r) {
    }

    public void saveFilter(ClinicianId id, SavedFilter f) {
    }

    public void subscribeCounty(ClinicianId id, County c) {
    }

    public Clinician load(ClinicianId id) {
        return null;
    }
}
