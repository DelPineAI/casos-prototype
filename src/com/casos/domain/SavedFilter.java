package com.casos.domain;

/** A named filter the clinician can reapply and optionally receive digests for (S8.1, S8.3). */
public class SavedFilter {

    private final String name;
    private final FilterCriteria criteria;
    private final boolean digestEnabled;

    public SavedFilter(String name, FilterCriteria criteria, boolean digestEnabled) {
        this.name = name;
        this.criteria = criteria;
        this.digestEnabled = digestEnabled;
    }

    public String name() {
        return name;
    }

    public FilterCriteria criteria() {
        return criteria;
    }

    public boolean digestEnabled() {
        return digestEnabled;
    }
}
