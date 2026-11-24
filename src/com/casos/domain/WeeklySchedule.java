package com.casos.domain;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/** Immutable value object describing which time blocks are covered on each day of the week. */
public final class WeeklySchedule {

    /** Each block (morning, afternoon, evening) counts as three hours of session time. */
    public static final int HOURS_PER_BLOCK = 3;

    private final EnumMap<DayOfWeek, EnumSet<TimeBlock>> blocks;

    public WeeklySchedule(Map<DayOfWeek, Set<TimeBlock>> blocks) {
        this.blocks = new EnumMap<>(DayOfWeek.class);
        blocks.forEach((day, set) -> {
            if (!set.isEmpty()) {
                this.blocks.put(day, EnumSet.copyOf(set));
            }
        });
    }

    public boolean has(DayOfWeek day, TimeBlock block) {
        EnumSet<TimeBlock> set = blocks.get(day);
        return set != null && set.contains(block);
    }

    public boolean isEmpty() {
        return blocks.isEmpty();
    }

    /** True when any day includes the given block (used by the time-of-day filter). */
    public boolean includes(TimeBlock block) {
        return blocks.values().stream().anyMatch(set -> set.contains(block));
    }

    /** Total hours represented by this schedule. */
    public int weeklyHours() {
        return blocks.values().stream().mapToInt(Set::size).sum() * HOURS_PER_BLOCK;
    }

    /** Hours where this schedule and another both have a block (S5.5 overlap ranking). */
    public int overlapHours(WeeklySchedule other) {
        int shared = 0;
        for (Map.Entry<DayOfWeek, EnumSet<TimeBlock>> entry : blocks.entrySet()) {
            for (TimeBlock block : entry.getValue()) {
                if (other.has(entry.getKey(), block)) {
                    shared++;
                }
            }
        }
        return shared * HOURS_PER_BLOCK;
    }

    /** Short label for a card, for example "Mon-Fri Afternoon". */
    public String summary() {
        if (blocks.isEmpty()) {
            return "Schedule not set";
        }
        StringBuilder days = new StringBuilder();
        for (DayOfWeek day : blocks.keySet()) {
            if (days.length() > 0) {
                days.append(" ");
            }
            String name = day.name();
            days.append(name.charAt(0)).append(name.substring(1, 3).toLowerCase());
        }
        EnumSet<TimeBlock> all = EnumSet.noneOf(TimeBlock.class);
        blocks.values().forEach(all::addAll);
        StringBuilder times = new StringBuilder();
        for (TimeBlock block : all) {
            if (times.length() > 0) {
                times.append(", ");
            }
            String name = block.name();
            times.append(name.charAt(0)).append(name.substring(1).toLowerCase());
        }
        return days + " | " + times;
    }

    public static WeeklySchedule empty() {
        return new WeeklySchedule(Map.of());
    }
}
