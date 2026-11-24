package com.casos.domain;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/** Immutable value object describing which time blocks are covered on each day of the week. */
public final class WeeklySchedule {

    private final EnumMap<DayOfWeek, EnumSet<TimeBlock>> blocks;

    public WeeklySchedule(Map<DayOfWeek, Set<TimeBlock>> blocks) {
        this.blocks = new EnumMap<>(DayOfWeek.class);
    }

    public boolean has(DayOfWeek day, TimeBlock block) {
        return false;
    }

    /** Total hours represented by this schedule. */
    public int weeklyHours() {
        return 0;
    }

    /** Hours where this schedule and another both have a block (S5.5 overlap ranking). */
    public int overlapHours(WeeklySchedule other) {
        return 0;
    }

    public static WeeklySchedule empty() {
        return new WeeklySchedule(Map.of());
    }
}
