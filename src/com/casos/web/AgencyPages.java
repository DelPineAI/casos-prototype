package com.casos.web;

import static com.casos.web.Html.checked;
import static com.casos.web.Html.esc;
import static com.casos.web.Html.selected;

import com.casos.analytics.DateRange;
import com.casos.analytics.InterestFunnelReport;
import com.casos.app.CasosSystem;
import com.casos.domain.Agency;
import com.casos.domain.AgencyId;
import com.casos.domain.AgeBand;
import com.casos.domain.Case;
import com.casos.domain.CaseDraft;
import com.casos.domain.CaseId;
import com.casos.domain.CaseStatus;
import com.casos.domain.Competency;
import com.casos.domain.CoverageType;
import com.casos.domain.Credential;
import com.casos.domain.InsurancePlan;
import com.casos.domain.Interest;
import com.casos.domain.InterestId;
import com.casos.domain.InterestStatus;
import com.casos.domain.TimeBlock;
import com.casos.domain.WeeklySchedule;
import com.casos.domain.ZipCode;
import com.casos.identity.Session;
import com.casos.lifecycle.ValidationException;
import com.casos.web.WebServer.Reply;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** SCR-06 Agency Dashboard, SCR-07 Post a Case, SCR-08 Interest Inbox. Signed-in agency members only. */
final class AgencyPages {

    private final CasosSystem system;

    AgencyPages(CasosSystem system) {
        this.system = system;
    }

    // ---------- SCR-06 ----------

    Reply dashboard(Request req, Session session, String header) {
        AgencyId agency = session.activeAgencyId().orElseThrow();
        String agencyName = system.agencies.findById(agency).map(Agency::name).orElse("Your agency");
        List<Case> cases = system.caseService.casesFor(session);
        Map<CaseId, Integer> interestCounts = system.inbox.countsByCase(agency, session);
        Instant now = system.clock.instant();
        DateRange allTime = new DateRange(Instant.EPOCH, now.plusSeconds(1));
        InterestFunnelReport funnel = system.reports.interestFunnel(agency, session, allTime);

        StringBuilder b = new StringBuilder();
        b.append(Html.banner(req.has("error") ? "error" : "ok", req.has("error") ? req.get("error") : req.get("msg")));
        b.append("<h1>").append(esc(agencyName)).append("</h1><p class=\"sub\">Signed in as ")
         .append(esc(session.email())).append("</p>");

        long open = cases.stream().filter(c -> c.isVisibleAt(now)).count();
        int newInterest = (int) system.inbox.forAgency(agency, session).stream()
                .filter(i -> i.status() == InterestStatus.NEW).count();
        b.append("<div class=\"grid\" style=\"margin-bottom:8px\">")
         .append(stat(String.valueOf(open), "open cases on the board"))
         .append(stat(String.valueOf(newInterest), "new expressions of interest"))
         .append(stat(String.valueOf(funnel.detailOpened()), "case detail views (all time)"))
         .append("</div>");

        b.append("<div class=\"row-actions\" style=\"margin:14px 0\"><a class=\"btn\" href=\"/agency/post\">Post a case</a>")
         .append("<a class=\"btn ghost\" href=\"/agency/inbox\">Interest inbox</a>")
         .append("<a class=\"btn ghost\" href=\"/agency/report.csv\">Export case performance (CSV)</a></div>");

        b.append("<h2>Your cases</h2>");
        if (cases.isEmpty()) {
            b.append("<div class=\"panel empty\">No cases yet. Post one in under a minute.</div>");
        } else {
            b.append("<table><thead><tr><th>Case</th><th>Status</th><th>Details</th><th>Posted</th><th>Open until</th>")
             .append("<th>Interest</th><th>Actions</th></tr></thead><tbody>");
            for (Case c : cases) {
                boolean visible = c.isVisibleAt(now);
                String status = c.status() == CaseStatus.OPEN && !visible ? "EXPIRED" : c.status().name();
                b.append("<tr><td><strong>#").append(c.caseNumber()).append("</strong></td>")
                 .append("<td><span class=\"badge ").append(status.toLowerCase()).append("\">").append(Html.title(status)).append("</span></td>")
                 .append("<td>").append(c.coverageType() == CoverageType.COVERAGE ? "<span class=\"badge cov\">Coverage</span> " : "Ongoing ")
                 .append(Html.credential(c.credentialRequired())).append(", ").append(c.weeklyHours()).append(" hrs, ")
                 .append(Html.county(c.county())).append("</td>")
                 .append("<td>").append(Html.day(c.postedAt())).append("</td>")
                 .append("<td>").append(c.status() == CaseStatus.OPEN ? Html.day(c.expiresAt()) : "").append("</td>")
                 .append("<td>").append(interestCounts.getOrDefault(c.caseId(), 0)).append("</td><td><div class=\"row-actions\">");
                if (visible) {
                    b.append(action("/agency/fill", c.caseId(), "Mark filled"))
                     .append(action("/agency/renew", c.caseId(), "Renew 7 days"));
                }
                b.append("</div></td></tr>");
            }
            b.append("</tbody></table>");
        }
        return Reply.ok(Html.page("Dashboard", header, b.toString()));
    }

    private static String stat(String value, String label) {
        return "<div class=\"panel\"><div class=\"stat\">" + value + "</div><div class=\"meta\">" + label + "</div></div>";
    }

    private static String action(String path, CaseId id, String label) {
        return "<form class=\"inline\" method=\"post\" action=\"" + path + "\"><input type=\"hidden\" name=\"id\" value=\""
                + id.value() + "\"><button class=\"btn small" + (label.startsWith("Renew") ? " ghost" : "")
                + "\" type=\"submit\">" + label + "</button></form>";
    }

    Reply fill(Request req, Session session) {
        return transition(req, session, true);
    }

    Reply renew(Request req, Session session) {
        return transition(req, session, false);
    }

    private Reply transition(Request req, Session session, boolean fill) {
        Optional<CaseId> id = caseId(req.get("id"));
        if (id.isEmpty()) {
            return Reply.redirect("/agency");
        }
        try {
            Case c = fill ? system.caseService.markFilled(id.get(), session) : system.caseService.renew(id.get(), session);
            String msg = fill ? "Case #" + c.caseNumber() + " marked filled and removed from the public board."
                    : "Case #" + c.caseNumber() + " renewed. It stays open until " + Html.day(c.expiresAt()) + ".";
            return Reply.redirect("/agency?msg=" + enc(msg));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return Reply.redirect("/agency?error=" + enc(e.getMessage()));
        }
    }

    // ---------- SCR-07 ----------

    Reply postForm(Request req, Session session, String header, List<String> errors, Request submitted) {
        Request v = submitted == null ? req : submitted;
        AgencyId agency = session.activeAgencyId().orElseThrow();
        String defaultZip = system.agencies.findById(agency).flatMap(Agency::lastPostedZip).map(ZipCode::value).orElse("");
        String zip = submitted == null ? defaultZip : v.get("zip");

        StringBuilder b = new StringBuilder("<h1>Post a case</h1><p class=\"sub\">Every field is a selection. "
                + "There is nowhere to type a client's name, date of birth, member ID or diagnosis.</p>");
        if (errors != null && !errors.isEmpty()) {
            b.append("<div class=\"banner error\" role=\"alert\"><strong>Please fix the following:</strong><ul>");
            errors.forEach(e -> b.append("<li>").append(esc(e)).append("</li>"));
            b.append("</ul></div>");
        }
        b.append("<form method=\"post\" action=\"/agency/post\"><div class=\"panel\"><div class=\"filters\">");

        b.append("<div><label>Case type</label><div class=\"checks\">")
         .append(radio("type", "COVERAGE", "Urgent coverage", v.get("type").equals("COVERAGE")))
         .append(radio("type", "ONGOING", "Ongoing", !v.get("type").equals("COVERAGE"))).append("</div></div>");

        b.append("<div><label for=\"cred\">Credential required</label><select id=\"cred\" name=\"cred\">");
        for (Credential c : Credential.values()) {
            b.append("<option value=\"").append(c.name()).append("\"").append(selected(c.name().equals(v.get("cred"))))
             .append(">").append(Html.credential(c)).append("</option>");
        }
        b.append("</select></div>");

        b.append("<div><label for=\"zip\">Session ZIP (never shown publicly)</label><input id=\"zip\" name=\"zip\" type=\"text\" ")
         .append("inputmode=\"numeric\" maxlength=\"5\" required value=\"").append(esc(zip)).append("\"></div>");

        b.append("<div><label for=\"age\">Client age band</label><select id=\"age\" name=\"age\">");
        for (AgeBand a : AgeBand.values()) {
            b.append("<option value=\"").append(a.name()).append("\"").append(selected(a.name().equals(v.get("age"))))
             .append(">").append(Html.ageBand(a)).append("</option>");
        }
        b.append("</select></div>");

        b.append("<div><label for=\"hours\">Weekly hours</label><input id=\"hours\" name=\"hours\" type=\"number\" min=\"1\" max=\"40\" required value=\"")
         .append(esc(v.get("hours"))).append("\"></div>");

        b.append("<div><label for=\"plan\">Insurance</label><select id=\"plan\" name=\"plan\">");
        for (InsurancePlan p : system.vocabulary.allPlans()) {
            b.append("<option value=\"").append(p.code()).append("\"").append(selected(p.code().equals(v.get("plan"))))
             .append(">").append(esc(p.name())).append("</option>");
        }
        b.append("</select></div>");

        b.append("<div><label for=\"rate\">Rate, $/hr (optional)</label><input id=\"rate\" name=\"rate\" type=\"number\" min=\"15\" max=\"150\" value=\"")
         .append(esc(v.get("rate"))).append("\"></div></div></div>");

        b.append("<div class=\"panel\"><label>Schedule</label><table class=\"week\"><tr><th></th>");
        for (DayOfWeek d : DayOfWeek.values()) {
            b.append("<th>").append(Html.title(d.name()).substring(0, 3)).append("</th>");
        }
        b.append("</tr>");
        for (TimeBlock t : TimeBlock.values()) {
            b.append("<tr><th>").append(Html.title(t.name())).append("</th>");
            for (DayOfWeek d : DayOfWeek.values()) {
                String val = d.name() + ":" + t.name();
                b.append("<td><input type=\"checkbox\" name=\"slot\" value=\"").append(val).append("\" aria-label=\"")
                 .append(Html.title(d.name())).append(" ").append(Html.title(t.name())).append("\"")
                 .append(checked(v.all("slot").contains(val))).append("></td>");
            }
            b.append("</tr>");
        }
        b.append("</table></div>");

        b.append("<div class=\"panel\"><label>Skills (from the controlled list)</label><div class=\"checks\">");
        for (Competency c : system.vocabulary.allCompetencies()) {
            b.append("<label><input type=\"checkbox\" name=\"skill\" value=\"").append(c.code()).append("\"")
             .append(checked(v.all("skill").contains(c.code()))).append("> ").append(esc(c.label())).append("</label>");
        }
        b.append("</div></div><div class=\"row-actions\"><button class=\"btn\" type=\"submit\">Publish case</button>")
         .append("<a class=\"btn ghost\" href=\"/agency\">Cancel</a></div></form>");
        return Reply.ok(Html.page("Post a case", header, b.toString()));
    }

    Reply submitCase(Request req, Session session, String header) {
        Map<DayOfWeek, Set<TimeBlock>> slots = new EnumMap<>(DayOfWeek.class);
        for (String slot : req.all("slot")) {
            String[] parts = slot.split(":");
            if (parts.length == 2) {
                Optional<DayOfWeek> day = BoardPages.enumValue(DayOfWeek.class, parts[0]);
                Optional<TimeBlock> block = BoardPages.enumValue(TimeBlock.class, parts[1]);
                if (day.isPresent() && block.isPresent()) {
                    slots.computeIfAbsent(day.get(), k -> EnumSet.noneOf(TimeBlock.class)).add(block.get());
                }
            }
        }
        Set<Competency> skills = new LinkedHashSet<>();
        for (String code : req.all("skill")) {
            system.vocabulary.competency(code).ifPresent(skills::add);
        }
        int hours = parseInt(req.get("hours")).orElse(0);
        Optional<Integer> rate = parseInt(req.get("rate")).map(dollars -> dollars * 100);

        CaseDraft draft = new CaseDraft(session.activeAgencyId().orElseThrow(),
                BoardPages.enumValue(CoverageType.class, req.get("type")).orElse(CoverageType.ONGOING),
                BoardPages.enumValue(Credential.class, req.get("cred")).orElse(null),
                new ZipCode(req.get("zip")), null,
                BoardPages.enumValue(AgeBand.class, req.get("age")).orElse(null),
                hours, new WeeklySchedule(slots),
                system.vocabulary.plan(req.get("plan")).orElse(new InsurancePlan(req.get("plan"), req.get("plan"))),
                skills, rate, Set.of());
        try {
            Case c = system.caseService.post(draft, session);
            return Reply.redirect("/agency?msg=" + enc("Case #" + c.caseNumber() + " is live on the public board until "
                    + Html.day(c.expiresAt()) + "."));
        } catch (ValidationException e) {
            Reply r = postForm(req, session, header, e.violations(), req);
            return r;
        }
    }

    // ---------- SCR-08 ----------

    Reply inbox(Request req, Session session, String header) {
        AgencyId agency = session.activeAgencyId().orElseThrow();
        List<Interest> all = system.inbox.forAgency(agency, session);
        Map<CaseId, Integer> numbers = new HashMap<>();
        system.caseService.casesFor(session).forEach(c -> numbers.put(c.caseId(), c.caseNumber()));

        StringBuilder b = new StringBuilder(Html.banner("ok", req.get("msg")));
        b.append("<h1>Interest inbox</h1><p class=\"sub\">Clinicians who asked about your cases. "
                + "Only your agency can see this list.</p>");
        if (all.isEmpty()) {
            b.append("<div class=\"panel empty\">No interest yet. New expressions of interest appear here.</div>");
        } else {
            b.append("<table><thead><tr><th>Case</th><th>Clinician</th><th>Contact</th><th>Note</th><th>Received</th>")
             .append("<th>Status</th><th>Update</th></tr></thead><tbody>");
            for (Interest i : all) {
                b.append("<tr><td>#").append(numbers.getOrDefault(i.caseId(), 0)).append("</td><td>")
                 .append(esc(i.contact().name())).append("</td><td>").append(esc(i.contact().email()))
                 .append(i.contact().phone().isEmpty() ? "" : "<br>" + esc(i.contact().phone())).append("</td><td>")
                 .append(esc(i.note().orElse(""))).append("</td><td>").append(Html.when(i.submittedAt())).append("</td><td>")
                 .append("<span class=\"badge").append(i.status() == InterestStatus.NEW ? " cov" : "").append("\">")
                 .append(Html.title(i.status().name())).append("</span></td><td><div class=\"row-actions\">");
                for (InterestStatus next : InterestStatus.values()) {
                    if (next != i.status() && next != InterestStatus.NEW) {
                        b.append("<form class=\"inline\" method=\"post\" action=\"/agency/inbox/advance\">")
                         .append("<input type=\"hidden\" name=\"id\" value=\"").append(i.interestId().value()).append("\">")
                         .append("<input type=\"hidden\" name=\"to\" value=\"").append(next.name()).append("\">")
                         .append("<button class=\"btn small ghost\" type=\"submit\">").append(Html.title(next.name()))
                         .append("</button></form>");
                    }
                }
                b.append("</div></td></tr>");
            }
            b.append("</tbody></table>");
        }
        return Reply.ok(Html.page("Interest inbox", header, b.toString()));
    }

    Reply advance(Request req, Session session) {
        try {
            InterestId id = new InterestId(UUID.fromString(req.get("id")));
            InterestStatus to = InterestStatus.valueOf(req.get("to"));
            system.inbox.advance(id, to, session);
            return Reply.redirect("/agency/inbox?msg=" + enc("Marked " + Html.title(to.name()).toLowerCase() + "."));
        } catch (IllegalArgumentException e) {
            return Reply.redirect("/agency/inbox");
        }
    }

    Reply reportCsv(Session session) {
        AgencyId agency = session.activeAgencyId().orElseThrow();
        DateRange allTime = new DateRange(Instant.EPOCH, system.clock.instant().plusSeconds(1));
        String csv = system.reports.exportCsv(system.reports.casePerformance(agency, session, allTime));
        return new Reply(200, csv, "text/csv; charset=utf-8")
                .header("Content-Disposition", "attachment; filename=\"case-performance.csv\"");
    }

    // ---------- helpers ----------

    private static String radio(String name, String value, String label, boolean on) {
        return "<label><input type=\"radio\" name=\"" + name + "\" value=\"" + value + "\"" + checked(on) + "> " + label + "</label>";
    }

    private static Optional<CaseId> caseId(String raw) {
        try {
            return Optional.of(new CaseId(UUID.fromString(raw)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static Optional<Integer> parseInt(String raw) {
        try {
            return raw.isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(raw.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
