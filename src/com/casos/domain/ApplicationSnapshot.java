package com.casos.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/** Immutable copy of a clinician's profile at the moment of application (S7.7). */
public record ApplicationSnapshot(Credential credential,
                                  Set<InServiceRecord> inServices,
                                  Optional<ZipCode> homeZip,
                                  Instant statedAt) {
}
