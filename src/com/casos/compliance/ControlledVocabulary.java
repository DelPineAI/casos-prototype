package com.casos.compliance;

import com.casos.domain.Competency;
import com.casos.domain.InsurancePlan;
import java.util.Map;
import java.util.Optional;

/** The closed lists every case field must be drawn from (S14.1). */
public class ControlledVocabulary {

    private final Map<String, Competency> competencies;
    private final Map<String, InsurancePlan> plans;

    public ControlledVocabulary(Map<String, Competency> competencies, Map<String, InsurancePlan> plans) {
        this.competencies = competencies;
        this.plans = plans;
    }

    public boolean containsCompetency(String code) {
        return competencies.containsKey(code);
    }

    public boolean containsPlan(String code) {
        return plans.containsKey(code);
    }

    public Optional<Competency> competency(String code) {
        return Optional.ofNullable(competencies.get(code));
    }

    public Optional<InsurancePlan> plan(String code) {
        return Optional.ofNullable(plans.get(code));
    }
}
