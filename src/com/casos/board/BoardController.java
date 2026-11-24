package com.casos.board;

import com.casos.domain.CaseId;
import com.casos.domain.ClinicianId;
import com.casos.domain.FilterCriteria;
import com.casos.domain.GeoPoint;
import com.casos.domain.Interest;
import java.util.Optional;

/** HTTP-facing entry point for the public board. */
public class BoardController {

    private final BoardService board;
    private final InterestService interests;

    public BoardController(BoardService board, InterestService interests) {
        this.board = board;
        this.interests = interests;
    }

    public BoardPage getBoard(FilterCriteria f, Optional<GeoPoint> viewer,
                              Optional<ClinicianId> viewerId, String sessionId) {
        return board.openCases(f, viewer, viewerId, sessionId);
    }

    public Optional<CaseDetail> getCase(CaseId id, Optional<GeoPoint> viewer,
                                        Optional<ClinicianId> viewerId, String sessionId) {
        return board.detail(id, viewer, viewerId, sessionId);
    }

    public Interest postInterest(InterestRequest req) {
        return interests.expressInterest(req);
    }
}
