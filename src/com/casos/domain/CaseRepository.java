package com.casos.domain;

/** Full read/write access to cases. Only the lifecycle module holds this. */
public interface CaseRepository extends CaseQuery {

    void save(Case c);

    /** Every case an agency has posted, in any state (agency dashboard). */
    java.util.List<Case> forAgency(AgencyId agencyId);
}
