package com.casos.board;

import com.casos.domain.FilterCriteria;
import java.util.List;

/** One rendered page of the board. */
public class BoardPage {

    private final List<CaseCard> cards;
    private final int total;
    private final int urgentCount;
    private final FilterCriteria criteria;

    public BoardPage(List<CaseCard> cards, FilterCriteria criteria) {
        this.cards = cards;
        this.total = cards.size();
        this.urgentCount = (int) cards.stream()
                .filter(c -> c.coverageType() == com.casos.domain.CoverageType.COVERAGE).count();
        this.criteria = criteria;
    }

    public List<CaseCard> cards() {
        return cards;
    }

    public int total() {
        return total;
    }

    public int urgentCount() {
        return urgentCount;
    }

    public FilterCriteria criteria() {
        return criteria;
    }
}
