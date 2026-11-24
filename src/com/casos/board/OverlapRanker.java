package com.casos.board;

import com.casos.domain.Case;
import com.casos.domain.WeeklySchedule;

/** Scores a case by overlap between its schedule and a clinician's availability (S5.5). */
public class OverlapRanker {

    public int score(Case c, WeeklySchedule availability) {
        return c.schedule().overlapHours(availability);
    }
}
