package com.casos.compliance;

import com.casos.domain.Competency;
import com.casos.domain.InsurancePlan;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** The closed lists every case field must be drawn from (S14.1). No field on a case is free text. */
public class ControlledVocabulary {

    private final Map<String, Competency> competencies;
    private final Map<String, InsurancePlan> plans;

    public ControlledVocabulary(Map<String, Competency> competencies, Map<String, InsurancePlan> plans) {
        this.competencies = new LinkedHashMap<>(competencies);
        this.plans = new LinkedHashMap<>(plans);
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

    public List<Competency> allCompetencies() {
        return new ArrayList<>(competencies.values());
    }

    public List<InsurancePlan> allPlans() {
        return new ArrayList<>(plans.values());
    }

    /** Seed vocabulary used by the prototype. */
    public static ControlledVocabulary standard() {
        Map<String, Competency> skills = new LinkedHashMap<>();
        for (String[] row : new String[][] {
                {"SPANISH", "Spanish speaking"}, {"TOILET", "Toilet training"},
                {"VERBAL", "Verbal behavior / manding"}, {"FEEDING", "Feeding protocols"},
                {"AGGRESSION", "Aggression / SIB plans"}, {"SCHOOL", "School shadowing"},
                {"TEENS", "Experience with teens"}, {"AAC", "AAC devices"}}) {
            skills.put(row[0], new Competency(row[0], row[1]));
        }
        Map<String, InsurancePlan> payers = new LinkedHashMap<>();
        for (String[] row : new String[][] {
                {"MEDICAID", "Florida Medicaid"}, {"SUNSHINE", "Sunshine Health"},
                {"AETNA", "Aetna"}, {"CIGNA", "Cigna"}, {"FLBLUE", "Florida Blue"},
                {"UHC", "UnitedHealthcare"}, {"TRICARE", "TRICARE"}}) {
            payers.put(row[0], new InsurancePlan(row[0], row[1]));
        }
        return new ControlledVocabulary(skills, payers);
    }
}
