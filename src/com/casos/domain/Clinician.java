package com.casos.domain;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Clinician profile aggregate: credential, availability, in-services, rosters, saved filters. */
public class Clinician {

    private final ClinicianId clinicianId;
    private final String email;
    private Credential credential;
    private Optional<ZipCode> homeZip;
    private WeeklySchedule availability;
    private final List<InServiceRecord> inServices;
    private final List<SelfReportedRoster> selfReportedRosters;
    private final List<SavedFilter> savedFilters;
    private final Set<County> countySubscriptions;
    private final NotificationPreferences notificationPreferences;

    public Clinician(ClinicianId clinicianId, String email, Credential credential) {
        this.clinicianId = clinicianId;
        this.email = email;
        this.credential = credential;
        this.homeZip = Optional.empty();
        this.availability = WeeklySchedule.empty();
        this.inServices = new ArrayList<>();
        this.selfReportedRosters = new ArrayList<>();
        this.savedFilters = new ArrayList<>();
        this.countySubscriptions = new HashSet<>();
        this.notificationPreferences = new NotificationPreferences();
    }

    public void updateCredential(Credential c) {
        this.credential = c;
    }

    public void setHomeZip(ZipCode z) {
        this.homeZip = Optional.of(z);
    }

    public void setAvailability(WeeklySchedule s) {
        this.availability = s;
    }

    public void addInService(InServiceRecord r) {
        inServices.add(r);
    }

    public void recordRoster(SelfReportedRoster r) {
        selfReportedRosters.add(r);
    }

    public void saveFilter(SavedFilter f) {
        savedFilters.add(f);
    }

    public void subscribe(County c) {
        countySubscriptions.add(c);
    }

    /** In-service types currently held and not expired. */
    public Set<InServiceType> heldInServices() {
        return EnumSet.noneOf(InServiceType.class);
    }

    public ClinicianId clinicianId() {
        return clinicianId;
    }

    public String email() {
        return email;
    }

    public Credential credential() {
        return credential;
    }

    public Optional<ZipCode> homeZip() {
        return homeZip;
    }

    public WeeklySchedule availability() {
        return availability;
    }

    public List<InServiceRecord> inServices() {
        return inServices;
    }

    public List<SelfReportedRoster> selfReportedRosters() {
        return selfReportedRosters;
    }

    public List<SavedFilter> savedFilters() {
        return savedFilters;
    }

    public Set<County> countySubscriptions() {
        return countySubscriptions;
    }

    public NotificationPreferences notificationPreferences() {
        return notificationPreferences;
    }
}
