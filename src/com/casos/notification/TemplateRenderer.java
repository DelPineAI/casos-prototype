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
        String url = baseUrl + "/auth?token=" + token;
        return new EmailMessage(List.of(to), "Your Casos sign-in link",
                "<p>Use this link to sign in to Casos. It works once and expires in 15 minutes.</p>"
                        + "<p><a href=\"" + url + "\">Sign in to Casos</a></p>",
                "Sign in to Casos (works once, expires in 15 minutes): " + url);
    }

    public String baseUrl() {
        return baseUrl;
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
        String county = c == County.MIAMI_DADE ? "Miami-Dade" : "Broward";
        return new EmailMessage(List.of(to), "New case in " + county,
                "<p>A new case was posted in " + county + ". <a href=\"" + baseUrl + "/\">View the board</a></p>",
                "A new case was posted in " + county + ": " + baseUrl + "/");
    }

    public EmailMessage weeklyDigest(String to, List<CaseCard> matches) {
        return null;
    }
}
