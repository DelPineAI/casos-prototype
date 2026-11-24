package com.casos.web;

import static com.casos.web.Html.checked;
import static com.casos.web.Html.esc;
import static com.casos.web.Html.selected;

import com.casos.app.CasosSystem;
import com.casos.board.BoardPage;
import com.casos.board.CaseCard;
import com.casos.board.CaseDetail;
import com.casos.board.CaseNoLongerOpenException;
import com.casos.board.InterestRequest;
import com.casos.domain.CaseId;
import com.casos.domain.Competency;
import com.casos.domain.Contact;
import com.casos.domain.CoverageType;
import com.casos.domain.Credential;
import com.casos.domain.FilterCriteria;
import com.casos.domain.GeoPoint;
import com.casos.domain.InsurancePlan;
import com.casos.domain.TimeBlock;
import com.casos.domain.ZipCode;
import com.casos.web.WebServer.Reply;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** SCR-01 Public Case Board, SCR-02 Case Detail, SCR-03 Set Location (inline). No account required. */
final class BoardPages {

    private final CasosSystem system;

    BoardPages(CasosSystem system) {
        this.system = system;
    }

    // ---------- SCR-01 ----------

    Reply board(Request req, String visitor, String header) {
        String zipText = req.get("zip");
        Optional<GeoPoint> viewer = location(zipText);
        String locationMessage = "";
        if (!zipText.isEmpty() && viewer.isEmpty()) {
            locationMessage = Html.banner("info", "We don't have ZIP " + zipText
                    + " in our Miami-Dade and Broward list yet, so distances are hidden. Try a nearby ZIP.");
        }

        FilterCriteria criteria = criteria(req);
        BoardPage page = system.board.openCases(criteria, viewer, Optional.empty(), visitor);

        StringBuilder b = new StringBuilder();
        b.append("<h1>Open ABA cases in South Florida</h1>")
         .append("<p class=\"sub\">Browse without an account. Every case is de-identified by design.</p>")
         .append(locationMessage)
         .append(filtersForm(req, zipText));

        b.append("<div class=\"counts\"><span><strong>").append(page.total()).append("</strong> open case")
         .append(page.total() == 1 ? "" : "s").append("</span>");
        if (page.urgentCount() > 0) {
            b.append("<span class=\"urgent-count\">").append(page.urgentCount()).append(" urgent coverage</span>");
        }
        b.append(viewer.isPresent() ? "<span class=\"meta\">Sorted: coverage first, then nearest</span>"
                : "<span class=\"meta\">Set a ZIP to see distances and sort by commute</span>");
        b.append("</div>");

        if (page.cards().isEmpty()) {
            b.append("<div class=\"panel empty\">No cases match these filters. Clear a filter to see more.</div>");
        } else {
            b.append("<div class=\"grid\">");
            for (CaseCard card : page.cards()) {
                b.append(card(card, zipText));
            }
            b.append("</div>");
        }
        return Reply.ok(Html.page("Open cases", header, b.toString()));
    }

    private String filtersForm(Request req, String zip) {
        StringBuilder f = new StringBuilder("<form class=\"panel\" method=\"get\" action=\"/\" aria-label=\"Filters\"><div class=\"filters\">");
        f.append("<div><label for=\"zip\">Your ZIP</label><input id=\"zip\" name=\"zip\" type=\"text\" inputmode=\"numeric\" maxlength=\"5\" placeholder=\"33199\" value=\"")
         .append(esc(zip)).append("\"></div>");

        f.append("<div><label for=\"dist\">Distance</label><select id=\"dist\" name=\"dist\"><option value=\"\">Any</option>");
        for (String d : new String[] {"5", "10", "15", "25"}) {
            f.append("<option value=\"").append(d).append("\"").append(selected(d.equals(req.get("dist"))))
             .append(">Within ").append(d).append(" mi</option>");
        }
        f.append("</select></div>");

        f.append("<div><label for=\"cred\">Credential</label><select id=\"cred\" name=\"cred\"><option value=\"\">Any</option>");
        for (Credential c : Credential.values()) {
            f.append("<option value=\"").append(c.name()).append("\"").append(selected(c.name().equals(req.get("cred"))))
             .append(">").append(Html.credential(c)).append("</option>");
        }
        f.append("</select></div>");

        f.append("<div><label for=\"hours\">Min hours/week</label><select id=\"hours\" name=\"hours\"><option value=\"\">Any</option>");
        for (String h : new String[] {"5", "10", "15", "20"}) {
            f.append("<option value=\"").append(h).append("\"").append(selected(h.equals(req.get("hours"))))
             .append(">").append(h).append("+</option>");
        }
        f.append("</select></div>");

        f.append("<div><label for=\"plan\">Insurance</label><select id=\"plan\" name=\"plan\"><option value=\"\">Any</option>");
        for (InsurancePlan p : system.vocabulary.allPlans()) {
            f.append("<option value=\"").append(p.code()).append("\"").append(selected(p.code().equals(req.get("plan"))))
             .append(">").append(esc(p.name())).append("</option>");
        }
        f.append("</select></div></div>");

        f.append("<div class=\"filters\" style=\"margin-top:12px\"><div><label>Time of day</label><div class=\"checks\">");
        for (TimeBlock t : TimeBlock.values()) {
            f.append("<label><input type=\"checkbox\" name=\"tb\" value=\"").append(t.name()).append("\"")
             .append(checked(req.all("tb").contains(t.name()))).append("> ").append(Html.title(t.name())).append("</label>");
        }
        f.append("</div></div><div><label>Type</label><div class=\"checks\"><label><input type=\"checkbox\" name=\"cov\" value=\"1\"")
         .append(checked(req.has("cov"))).append("> Urgent coverage only</label></div></div>")
         .append("<div class=\"row-actions\"><button class=\"btn\" type=\"submit\">Apply</button>")
         .append("<a class=\"btn ghost\" href=\"/").append(zip.isEmpty() ? "" : "?zip=" + esc(zip)).append("\">Clear</a></div></div></form>");
        return f.toString();
    }

    private String card(CaseCard c, String zip) {
        boolean cov = c.coverageType() == CoverageType.COVERAGE;
        StringBuilder b = new StringBuilder();
        b.append("<a class=\"card").append(cov ? " cov" : "").append("\" href=\"/case?id=").append(c.caseId().value())
         .append(zip.isEmpty() ? "" : "&zip=" + esc(zip)).append("\">");
        b.append("<div class=\"hero\"><div><b>")
         .append(c.distanceMiles().map(d -> String.format("%.1f", d)).orElse("--"))
         .append("</b><small>").append(c.distanceMiles().isPresent() ? "miles" : "set location").append("</small></div>")
         .append("<div><b>").append(c.weeklyHours()).append("</b><small>hrs / week</small></div></div>");
        b.append("<div><span class=\"badge").append(cov ? " cov" : "").append("\">")
         .append(cov ? "Urgent coverage" : "Ongoing").append("</span> <span class=\"meta\">")
         .append(Html.county(c.county())).append(" | ").append(Html.credential(c.credentialRequired())).append("</span></div>");
        b.append("<div class=\"meta\">").append(esc(c.schedule().summary())).append("</div>");
        b.append("<div class=\"tags\">");
        for (Competency k : c.competencies()) {
            b.append("<span class=\"tag\">").append(esc(k.label())).append("</span>");
        }
        b.append("</div><div class=\"meta\">").append(esc(c.insurancePlan().name()))
         .append(c.hourlyRateCents().map(r -> " | $" + (r / 100) + "/hr").orElse(""))
         .append(" | Case #").append(c.caseNumber()).append("</div></a>");
        return b.toString();
    }

    // ---------- SCR-02 ----------

    Reply detail(Request req, String visitor, String header) {
        return detailPage(req, visitor, header, null);
    }

    private Reply detailPage(Request req, String visitor, String header, String error) {
        Optional<CaseId> id = caseId(req.get("id"));
        String zip = req.get("zip");
        Optional<CaseDetail> detail = id.flatMap(i -> system.board.detail(i, location(zip), Optional.empty(), visitor));
        if (detail.isEmpty()) {
            return new Reply(404, Html.page("Case closed", header,
                    Html.banner("info", "This case was filled or has expired, so it is no longer on the board.")
                            + "<p><a href=\"/" + (zip.isEmpty() ? "" : "?zip=" + esc(zip)) + "\">Back to open cases</a></p>"));
        }
        CaseDetail c = detail.get();
        boolean cov = c.coverageType() == CoverageType.COVERAGE;
        StringBuilder b = new StringBuilder();
        b.append("<p><a href=\"/").append(zip.isEmpty() ? "" : "?zip=" + esc(zip)).append("\">&larr; Back to open cases</a></p>");
        b.append("<div class=\"two\"><section class=\"panel\">");
        b.append("<span class=\"badge").append(cov ? " cov" : "").append("\">").append(cov ? "Urgent coverage" : "Ongoing")
         .append("</span><h1>Case #").append(c.caseNumber()).append("</h1>")
         .append("<p class=\"sub\">").append(Html.county(c.county())).append(" | posted ").append(Html.day(c.postedAt()))
         .append(" | open until ").append(Html.day(c.expiresAt())).append("</p>");
        b.append("<div class=\"hero\" style=\"margin-bottom:12px\"><div><b>")
         .append(c.distanceMiles().map(d -> String.format("%.1f", d)).orElse("--")).append("</b><small>")
         .append(c.distanceMiles().isPresent() ? "miles from " + esc(zip) : "set a ZIP on the board").append("</small></div><div><b>")
         .append(c.weeklyHours()).append("</b><small>hrs / week</small></div></div>");
        b.append("<dl><dt>Credential</dt><dd>").append(Html.credential(c.credentialRequired())).append("</dd>")
         .append("<dt>Client age</dt><dd>").append(Html.ageBand(c.ageBand())).append("</dd>")
         .append("<dt>Schedule</dt><dd>").append(esc(c.schedule().summary())).append("</dd>")
         .append("<dt>Insurance</dt><dd>").append(esc(c.insurancePlan().name())).append("</dd>")
         .append("<dt>Rate</dt><dd>").append(c.hourlyRateCents().map(r -> "$" + (r / 100) + " per hour").orElse("Discuss with agency")).append("</dd>")
         .append("<dt>Skills</dt><dd>").append(c.competencies().isEmpty() ? "None listed"
                 : esc(c.competencies().stream().map(Competency::label).collect(Collectors.joining(", ")))).append("</dd>");
        if (!c.preferences().isEmpty()) {
            b.append("<dt>Preferences</dt><dd>").append(esc(c.preferences().stream()
                    .map(p -> Html.title(p.name())).collect(Collectors.joining(", ")))).append("</dd>");
        }
        b.append("</dl><p class=\"meta\" style=\"margin-top:12px\">The map location for this case is offset by up to a mile, "
                + "and the exact address and ZIP are never shown.</p></section>");

        b.append("<section class=\"panel\"><h2 style=\"margin-top:0\">I'm interested</h2>")
         .append("<p class=\"sub\">No account needed. The agency gets your details and contacts you directly.</p>")
         .append(error == null ? "" : Html.banner("error", error))
         .append("<form method=\"post\" action=\"/interest\">")
         .append("<input type=\"hidden\" name=\"id\" value=\"").append(c.caseId().value()).append("\">")
         .append("<input type=\"hidden\" name=\"zip\" value=\"").append(esc(zip)).append("\">")
         .append(field("name", "Your name", "text", req.get("name"), true))
         .append(field("email", "Email", "email", req.get("email"), false))
         .append(field("phone", "Phone (optional if you give an email)", "tel", req.get("phone"), false))
         .append("<p><label for=\"note\">Note to the agency (optional)</label><textarea id=\"note\" name=\"note\" rows=\"3\" maxlength=\"400\">")
         .append(esc(req.get("note"))).append("</textarea></p>")
         .append("<button class=\"btn\" type=\"submit\">Send my interest</button></form></section></div>");
        return Reply.ok(Html.page("Case #" + c.caseNumber(), header, b.toString()));
    }

    Reply submitInterest(Request req, String visitor, String header) {
        Optional<CaseId> id = caseId(req.get("id"));
        if (id.isEmpty()) {
            return Reply.redirect("/");
        }
        try {
            system.interestService.expressInterest(new InterestRequest(id.get(),
                    new Contact(req.get("name"), req.get("email"), req.get("phone")),
                    req.optional("note"), Optional.empty()));
        } catch (CaseNoLongerOpenException e) {
            return new Reply(409, Html.page("Case closed", header, Html.banner("info", e.getMessage())
                    + "<p><a href=\"/\">See other open cases</a></p>"));
        } catch (IllegalArgumentException e) {
            return detailPage(req, visitor, header, e.getMessage());
        }
        String zip = req.get("zip");
        return Reply.ok(Html.page("Interest sent", header,
                Html.banner("ok", "Your interest was sent. The agency will contact you directly.")
                        + "<p>You can keep browsing. If you want a profile that remembers your availability, "
                        + "clinician accounts arrive in Phase 3.</p><p><a class=\"btn\" href=\"/"
                        + (zip.isEmpty() ? "" : "?zip=" + URLEncoder.encode(zip, StandardCharsets.UTF_8))
                        + "\">Back to open cases</a></p>"));
    }

    // ---------- helpers ----------

    private FilterCriteria criteria(Request req) {
        Set<TimeBlock> blocks = EnumSet.noneOf(TimeBlock.class);
        for (String v : req.all("tb")) {
            enumValue(TimeBlock.class, v).ifPresent(blocks::add);
        }
        return new FilterCriteria(req.has("cov"),
                integer(req.get("dist")),
                integer(req.get("hours")),
                enumValue(Credential.class, req.get("cred")),
                system.vocabulary.plan(req.get("plan")),
                blocks, new LinkedHashSet<>(), false);
    }

    private Optional<GeoPoint> location(String zip) {
        return ZipCode.isValid(zip) ? system.geo.resolve(new ZipCode(zip)) : Optional.empty();
    }

    private static Optional<CaseId> caseId(String raw) {
        try {
            return Optional.of(new CaseId(UUID.fromString(raw)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static Optional<Integer> integer(String raw) {
        try {
            return raw.isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(raw));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    static <E extends Enum<E>> Optional<E> enumValue(Class<E> type, String raw) {
        try {
            return raw.isEmpty() ? Optional.empty() : Optional.of(Enum.valueOf(type, raw));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static String field(String name, String label, String type, String value, boolean required) {
        return "<p><label for=\"" + name + "\">" + label + "</label><input id=\"" + name + "\" name=\"" + name
                + "\" type=\"" + type + "\" value=\"" + esc(value) + "\"" + (required ? " required" : "") + "></p>";
    }
}
