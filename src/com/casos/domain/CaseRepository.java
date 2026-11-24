package com.casos.domain;

/** Full read/write access to cases. Only the lifecycle module holds this. */
public interface CaseRepository extends CaseQuery {

    void save(Case c);
}
