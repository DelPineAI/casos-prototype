package com.casos.app;

import com.casos.domain.Agency;
import com.casos.domain.AgencyId;
import com.casos.domain.AgencyMember;
import com.casos.domain.AgeBand;
import com.casos.domain.CaseDraft;
import com.casos.domain.Competency;
import com.casos.domain.County;
import com.casos.domain.CoverageType;
import com.casos.domain.Credential;
import com.casos.domain.MemberId;
import com.casos.domain.Role;
import com.casos.domain.StaffingPreference;
import com.casos.domain.TimeBlock;
import com.casos.domain.WeeklySchedule;
import com.casos.domain.ZipCode;
import com.casos.identity.Session;
import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Demo data: two fictional agencies, their owners, one pending coordinator invitation, and a handful of
 * de-identified cases. Cases are posted through CaseService so they pass the same rules as the UI.
 */
public final class SeedData {

    public static final String DEMO_COORDINATOR = "coordinator@sunrisebh.test";
    public static final String SUNRISE_OWNER = "owner@sunrisebh.test";
    public static final String PALM_OWNER = "owner@palmcoastaba.test";

    private SeedData() {
    }

    public static AgencyId load(CasosSystem s) {
        AgencyId sunrise = agency(s, "Sunrise Behavioral Health (demo)", Set.of(County.MIAMI_DADE, County.BROWARD));
        AgencyId palm = agency(s, "Palm Coast ABA (demo)", Set.of(County.BROWARD));

        Session sunriseOwner = owner(s, sunrise, SUNRISE_OWNER);
        Session palmOwner = owner(s, palm, PALM_OWNER);

        // The demo coordinator is invited, not a member yet: membership is created at first sign-in (S9.3).
        MemberId inviter = sunriseOwner.memberId().orElseThrow();
        s.invitations.invite(sunrise, DEMO_COORDINATOR, Role.COORDINATOR, inviter);

        post(s, sunriseOwner, CoverageType.COVERAGE, Credential.RBT, "33174", AgeBand.AGE_3_5, 12,
                days(TimeBlock.AFTERNOON, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY, DayOfWeek.TUESDAY),
                "MEDICAID", Set.of("SPANISH", "TOILET"), 2400);
        post(s, sunriseOwner, CoverageType.ONGOING, Credential.RBT, "33186", AgeBand.AGE_6_9, 20,
                weekdays(TimeBlock.AFTERNOON), "SUNSHINE", Set.of("VERBAL"), 2300);
        post(s, sunriseOwner, CoverageType.ONGOING, Credential.BCBA, "33135", AgeBand.AGE_10_13, 6,
                days(TimeBlock.EVENING, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY), "AETNA", Set.of("AGGRESSION"), 6500);
        post(s, sunriseOwner, CoverageType.COVERAGE, Credential.RBT, "33012", AgeBand.UNDER_3, 9,
                days(TimeBlock.MORNING, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), "FLBLUE",
                Set.of("FEEDING"), 2500);
        post(s, palmOwner, CoverageType.ONGOING, Credential.RBT, "33024", AgeBand.AGE_14_17, 15,
                days(TimeBlock.AFTERNOON, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY), "MEDICAID", Set.of("TEENS", "SCHOOL"), 2200);
        post(s, palmOwner, CoverageType.COVERAGE, Credential.BCABA, "33301", AgeBand.AGE_6_9, 8,
                days(TimeBlock.MORNING, DayOfWeek.SATURDAY), "CIGNA", Set.of("AAC"), 4000);
        return sunrise;
    }

    private static AgencyId agency(CasosSystem s, String name, Set<County> area) {
        AgencyId id = AgencyId.newId();
        s.agencies.save(new Agency(id, name, area));
        return id;
    }

    private static Session owner(CasosSystem s, AgencyId agency, String email) {
        AgencyMember member = new AgencyMember(MemberId.newId(), agency, email, Role.OWNER, s.clock.instant());
        s.members.save(member);
        return new Session("seed-" + email, email, Optional.of(member.memberId()), Optional.of(agency), s.clock.instant());
    }

    private static void post(CasosSystem s, Session actor, CoverageType type, Credential credential, String zip,
                             AgeBand age, int hours, WeeklySchedule schedule, String plan, Set<String> skills,
                             int rateCents) {
        Set<Competency> competencies = new LinkedHashSet<>();
        skills.forEach(code -> s.vocabulary.competency(code).ifPresent(competencies::add));
        CaseDraft draft = new CaseDraft(actor.activeAgencyId().orElseThrow(), type, credential, new ZipCode(zip),
                null, age, hours, schedule, s.vocabulary.plan(plan).orElseThrow(), competencies,
                Optional.of(rateCents), Set.<StaffingPreference>of());
        s.caseService.post(draft, actor);
    }

    static WeeklySchedule days(TimeBlock block, DayOfWeek... days) {
        Map<DayOfWeek, Set<TimeBlock>> map = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : days) {
            map.put(d, Set.of(block));
        }
        return new WeeklySchedule(map);
    }

    static WeeklySchedule weekdays(TimeBlock block) {
        return days(block, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
    }
}
