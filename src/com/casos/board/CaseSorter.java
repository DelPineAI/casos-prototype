package com.casos.board;

import com.casos.domain.CoverageType;
import java.util.Comparator;
import java.util.List;

/** Coverage cases first, then nearest (when a location is set) or newest (D4, M1 key functions). */
public class CaseSorter {

    public List<CaseCard> sort(List<CaseCard> cards, boolean byDistance) {
        Comparator<CaseCard> coverageFirst = Comparator.comparing(c -> c.coverageType() != CoverageType.COVERAGE);
        Comparator<CaseCard> then = byDistance
                ? Comparator.comparing(c -> c.distanceMiles().orElse(Double.MAX_VALUE))
                : Comparator.comparing(CaseCard::postedAt).reversed();
        return cards.stream().sorted(coverageFirst.thenComparing(then)).toList();
    }
}
