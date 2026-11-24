package com.casos.clinician;

import com.casos.domain.Clinician;
import com.casos.domain.ClinicianId;
import com.casos.domain.ClinicianRepository;
import com.casos.domain.County;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Development implementation of ClinicianRepository. */
public class InMemoryClinicianRepository implements ClinicianRepository {

    private final Map<ClinicianId, Clinician> store;

    public InMemoryClinicianRepository() {
        this.store = new HashMap<>();
    }

    @Override
    public void save(Clinician c) {
        store.put(c.clinicianId(), c);
    }

    @Override
    public Optional<Clinician> findById(ClinicianId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Clinician> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<Clinician> subscribedTo(County county) {
        return List.of();
    }
}
