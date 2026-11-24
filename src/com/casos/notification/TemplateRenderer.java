package com.casos.notification;

import com.casos.board.CaseCard;
import com.casos.domain.ApplicationStatus;
import com.casos.domain.County;
import com.casos.domain.InServiceType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/** Builds every outbound email. Owns the base URL so domain events can carry tokens, not URLs (Round 4, W8). */
public class TemplateRenderer {

    private final String baseUrl;

    public TemplateRenderer(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public EmailMessage magicLink(String to, String token) {
        return null;
    }

    public EmailMessage expiryNudge(List<String> to, int caseNumber, Instant expiresAt,
                                    String fillToken, String renewToken) {
        return null;
    }

    public EmailMessage applicationDecided(String to, String agencyName, ApplicationStatus outcome) {
        return null;
    }

    public EmailMessage inServiceExpiring(String to, InServiceType type, LocalDate expiresOn) {
        return null;
    }

    public EmailMessage newCaseInCounty(String to, County c, int caseNumber) {
        return null;
    }

    public EmailMessage weeklyDigest(String to, List<CaseCard> matches) {
        return null;
    }
}
