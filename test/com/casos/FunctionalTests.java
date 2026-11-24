package com.casos;

import com.casos.analytics.DateRange;
import com.casos.analytics.EventRecord;
import com.casos.app.CasosSystem;
import com.casos.app.SeedData;
import com.casos.board.BoardPage;
import com.casos.board.CaseCard;
import com.casos.board.CaseNoLongerOpenException;
import com.casos.board.InterestRequest;
import com.casos.compliance.FreeTextGuard;
import com.casos.domain.AgencyId;
import com.casos.domain.AgeBand;
import com.casos.domain.Case;
import com.casos.domain.CaseDraft;
import com.casos.domain.CaseStatus;
import com.casos.domain.Contact;
import com.casos.domain.County;
import com.casos.domain.CoverageType;
import com.casos.domain.Credential;
import com.casos.domain.FilterCriteria;
import com.casos.domain.GeoPoint;
import com.casos.domain.Interest;
import com.casos.domain.InterestStatus;
import com.casos.domain.TimeBlock;
import com.casos.domain.WeeklySchedule;
import com.casos.domain.ZipCode;
import com.casos.identity.InvalidLinkException;
import com.casos.identity.MagicLink;
import com.casos.identity.Session;
import com.casos.identity.UnauthorizedException;
import com.casos.lifecycle.ValidationException;
import com.casos.web.WebServer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deliverable 5 functional test suite. Each test drives the system the way a user would, through the
 * public services or the running web server, and checks the behaviour a requirement promises.
 * Run: java -cp out com.casos.FunctionalTests
 */
public final class FunctionalTests {

    private record Result(String id, String title, String requirements, String expected, String actual, boolean pass) {
    }

    private static final List<Result> RESULTS = new ArrayList<>();
    private static final Instant START = Instant.parse("2026-11-23T14:00:00Z");

    public static void main(String[] args) throws Exception {
        run("TC-01", "Anonymous visitor sees the open board with no sign-in", "U1.0, S1.1, S1.5",
                "HTTP 200, no redirect to sign-in, all 6 seeded open cases listed", FunctionalTests::tc01);
        run("TC-02", "Filters narrow the board (coverage only, credential, hours, time of day)", "U2.0, S2.1 to S2.7",
                "Each filter returns only matching cases; combined filters intersect", FunctionalTests::tc02);
        run("TC-03", "ZIP gives each card a distance, sorts nearest first, and the distance filter excludes far cases",
                "U3.0, S3.1, S3.4", "Distances present, coverage first then ascending distance, 10-mile filter drops Broward cases",
                FunctionalTests::tc03);
        run("TC-04", "No case exposes its ZIP or exact location", "U14.0, S14.3, S14.4",
                "Case has no ZIP or exact-pin accessor; detail page omits the ZIP; display pin is 0.3 to 0.8 mi from the real one",
                FunctionalTests::tc04);
        run("TC-05", "Clinician expresses interest without an account and the agency sees it", "U4.0, U12.0, S4.1 to S4.3, S12.1",
                "Interest saved with status New, listed in the owning agency's inbox, count shown per case",
                FunctionalTests::tc05);
        run("TC-06", "Interest note containing a client date of birth or member ID is refused", "U14.0, S14.6 (UX gap)",
                "Submission rejected with a plain-language message; nothing saved", FunctionalTests::tc06);
        run("TC-07", "Interest in a case filled mid-visit is refused with an explanation", "S4.4 (UX gap)",
                "CaseNoLongerOpenException with a human message; nothing saved", FunctionalTests::tc07);
        run("TC-08", "Magic-link sign-in claims the invitation; the link is single-use and expires", "U9.0, S9.1, S9.3",
                "First sign-in creates the membership; reuse and 16-minute-old links are rejected", FunctionalTests::tc08);
        run("TC-09", "Coordinator posts a case: numbered per agency, county derived from ZIP, live on the board",
                "U10.0, S10.1, S10.5, S14.5", "Case #1005 for Sunrise, county Miami-Dade despite form saying Broward, visible on board",
                FunctionalTests::tc09);
        run("TC-10", "Invalid case drafts are rejected with every problem listed", "S10.2, S14.1",
                "Out-of-area ZIP, 60 hours, empty schedule and unknown payer all reported; no case created",
                FunctionalTests::tc10);
        run("TC-11", "Mark filled removes the case from the board; renew resets the seven-day window", "U11.0, S11.3, S11.4",
                "Filled case gone from board and detail; second fill refused; renew moves expiry to now + 7 days",
                FunctionalTests::tc11);
        run("TC-12", "A case expires after seven days even if the nightly job has not run", "S11.1, S1.5",
                "Invisible at day 7 before the job; job then marks it Expired and logs CaseExpired", FunctionalTests::tc12);
        run("TC-13", "Agencies are isolated: no cross-agency actions, no anonymous dashboard", "S9.4, S12.4",
                "Filling another agency's case and reading its inbox throw Unauthorized; /agency redirects to sign-in",
                FunctionalTests::tc13);
        run("TC-14", "Every interaction is logged with server-side attribution and exported as CSV", "U15.0, S15.1 to S15.4",
                "BoardViewed, CaseDetailViewed, InterestSubmitted recorded; detail view attributed to case owner; CSV row per case",
                FunctionalTests::tc14);

        report();
    }

    // ------------------------------------------------------------------ tests

    private static String tc01() throws Exception {
        try (Running app = Running.start()) {
            HttpResponse<String> r = app.get("/");
            check(r.statusCode() == 200, "status " + r.statusCode());
            int count = count(r.body(), "class=\"card");
            check(count == 6, "cards " + count);
            check(!r.body().contains("Agency sign in</h1>"), "sign-in wall shown");
            return "HTTP 200, " + count + " cards, no sign-in wall";
        }
    }

    private static String tc02() {
        CasosSystem s = seeded();
        int all = board(s, FilterCriteria.none(), Optional.empty()).total();
        BoardPage cov = board(s, criteria(true, null, null, null, Set.of()), Optional.empty());
        check(cov.cards().stream().allMatch(c -> c.coverageType() == CoverageType.COVERAGE), "coverage filter leaked");
        BoardPage bcba = board(s, criteria(false, null, null, Credential.BCBA, Set.of()), Optional.empty());
        check(bcba.cards().stream().allMatch(c -> c.credentialRequired() == Credential.BCBA), "credential filter leaked");
        BoardPage hours = board(s, criteria(false, null, 15, null, Set.of()), Optional.empty());
        check(hours.cards().stream().allMatch(c -> c.weeklyHours() >= 15), "hours filter leaked");
        BoardPage evening = board(s, criteria(false, null, null, null, Set.of(TimeBlock.EVENING)), Optional.empty());
        check(evening.cards().stream().allMatch(c -> c.schedule().includes(TimeBlock.EVENING)), "time filter leaked");
        BoardPage combined = board(s, criteria(true, null, null, Credential.RBT, Set.of()), Optional.empty());
        check(combined.cards().stream().allMatch(c -> c.coverageType() == CoverageType.COVERAGE
                && c.credentialRequired() == Credential.RBT), "combined filter leaked");
        check(cov.total() < all && bcba.total() < all, "filters did not narrow");
        return "all " + all + ", coverage " + cov.total() + ", BCBA " + bcba.total() + ", 15+ hrs " + hours.total()
                + ", evening " + evening.total() + ", coverage+RBT " + combined.total();
    }

    private static String tc03() {
        CasosSystem s = seeded();
        GeoPoint fiu = s.geo.resolve(new ZipCode("33199")).orElseThrow();
        BoardPage page = board(s, FilterCriteria.none(), Optional.of(fiu));
        check(page.cards().stream().allMatch(c -> c.distanceMiles().isPresent()), "missing distance");
        List<CaseCard> cards = page.cards();
        boolean seenOngoing = false;
        double last = -1;
        for (CaseCard c : cards) {
            if (c.coverageType() == CoverageType.ONGOING) {
                if (!seenOngoing) {
                    last = -1;
                }
                seenOngoing = true;
            } else {
                check(!seenOngoing, "coverage case after ongoing");
            }
            check(c.distanceMiles().get() >= last, "not ascending");
            last = c.distanceMiles().get();
        }
        BoardPage near = board(s, criteria(false, 10, null, null, Set.of()), Optional.of(fiu));
        check(near.cards().stream().allMatch(c -> c.distanceMiles().get() <= 10), "distance filter leaked");
        check(near.cards().stream().noneMatch(c -> c.county() == County.BROWARD), "Broward case within 10 mi of FIU");
        double nearest = cards.stream().mapToDouble(c -> c.distanceMiles().get()).min().orElseThrow();
        return String.format("%d cards with distance, nearest %.1f mi, coverage-first order held, within 10 mi: %d",
                cards.size(), nearest, near.total());
    }

    private static String tc04() throws Exception {
        List<String> accessors = Arrays.stream(Case.class.getMethods()).map(Method::getName).toList();
        check(!accessors.contains("zip") && !accessors.contains("exactPin") && !accessors.contains("getZip"),
                "Case exposes a location accessor");
        CasosSystem s = seeded();
        Case c = byNumber(s, 1001);
        GeoPoint exact = s.geo.resolve(new ZipCode("33174")).orElseThrow();
        double offset = exact.milesTo(c.displayPin());
        check(offset >= 0.29 && offset <= 0.81, "offset " + offset);
        GeoPoint again = s.jitter.offset(exact, c.caseId());
        check(again.equals(c.displayPin()), "pin not stable between loads");
        try (Running app = Running.start()) {
            String body = app.get("/case?id=" + firstCaseId(app)).body();
            check(!body.contains("33174") && !body.contains("33186") && !body.contains("33135"), "ZIP rendered");
        }
        return String.format("no ZIP/pin accessor on Case; pin offset %.2f mi and stable; detail HTML has no ZIP", offset);
    }

    private static String tc05() {
        CasosSystem s = seeded();
        Case target = byNumber(s, 1001);
        Interest i = s.interestService.expressInterest(new InterestRequest(target.caseId(),
                new Contact("Ana Lopez", "ana@example.com", ""), Optional.of("Free Mon/Wed afternoons"), Optional.empty()));
        check(i.status() == InterestStatus.NEW, "status " + i.status());
        Session coordinator = signIn(s, SeedData.DEMO_COORDINATOR);
        List<Interest> inbox = s.inbox.forAgency(target.agencyId(), coordinator);
        check(inbox.size() == 1 && inbox.get(0).contact().name().equals("Ana Lopez"), "inbox " + inbox.size());
        Map<com.casos.domain.CaseId, Integer> counts = s.inbox.countsByCase(target.agencyId(), coordinator);
        check(counts.getOrDefault(target.caseId(), 0) == 1, "count");
        s.inbox.advance(i.interestId(), InterestStatus.CONTACTED, coordinator);
        check(s.interests.findById(i.interestId()).orElseThrow().status() == InterestStatus.CONTACTED, "advance");
        return "interest saved (New), 1 item in Sunrise inbox, count 1 on case #1001, advanced to Contacted";
    }

    private static String tc06() {
        CasosSystem s = seeded();
        Case target = byNumber(s, 1001);
        FreeTextGuard guard = new FreeTextGuard();
        check(guard.containsIdentifier("member ID W123456789"), "member id not detected");
        check(!guard.containsIdentifier("Available weekday afternoons, bilingual"), "false positive");
        String message = expectThrows(IllegalArgumentException.class, () -> s.interestService.expressInterest(
                new InterestRequest(target.caseId(), new Contact("Ana", "ana@example.com", ""),
                        Optional.of("Client DOB 04/12/2019"), Optional.empty())));
        check(s.interests.forAgency(target.agencyId()).isEmpty(), "interest was saved");
        return "rejected: \"" + message + "\"";
    }

    private static String tc07() {
        CasosSystem s = seeded();
        Case target = byNumber(s, 1001);
        s.caseService.markFilled(target.caseId(), owner(s, target.agencyId()));
        String message = expectThrows(CaseNoLongerOpenException.class, () -> s.interestService.expressInterest(
                new InterestRequest(target.caseId(), new Contact("Ana", "ana@example.com", ""), Optional.empty(), Optional.empty())));
        check(s.interests.forAgency(target.agencyId()).isEmpty(), "interest was saved");
        return "refused: \"" + message + "\"";
    }

    private static String tc08() {
        MutableClock clock = new MutableClock(START);
        CasosSystem s = new CasosSystem(clock, "http://localhost");
        SeedData.load(s);
        check(s.members.findByEmail(SeedData.DEMO_COORDINATOR).isEmpty(), "member before sign-in");
        MagicLink link = s.auth.requestMagicLink(SeedData.DEMO_COORDINATOR);
        check(s.outbox.outbox().get(0).message().textBody().contains(link.token()), "email not sent");
        Session session = s.auth.verify(link.token());
        check(!session.isAnonymous() && s.members.findByEmail(SeedData.DEMO_COORDINATOR).size() == 1, "not claimed");
        expectThrows(InvalidLinkException.class, () -> s.auth.verify(link.token()));
        MagicLink stale = s.auth.requestMagicLink(SeedData.DEMO_COORDINATOR);
        clock.advance(Duration.ofMinutes(16));
        expectThrows(InvalidLinkException.class, () -> s.auth.verify(stale.token()));
        expectThrows(IllegalArgumentException.class, () -> s.auth.requestMagicLink("not-an-email"));
        return "email delivered, membership created on first sign-in, reused link refused, 16-min link refused";
    }

    private static String tc09() {
        CasosSystem s = seeded();
        AgencyId sunrise = byNumber(s, 1001).agencyId();
        Session coordinator = signIn(s, SeedData.DEMO_COORDINATOR);
        CaseDraft draft = draft(s, sunrise, "33165", County.BROWARD, 10, schedule(DayOfWeek.MONDAY, TimeBlock.AFTERNOON), "AETNA");
        Case posted = s.caseService.post(draft, coordinator);
        check(posted.caseNumber() == 1005, "number " + posted.caseNumber());
        check(posted.county() == County.MIAMI_DADE, "county " + posted.county());
        check(posted.expiresAt().equals(posted.postedAt().plus(Case.EXPIRY)), "expiry");
        boolean onBoard = board(s, FilterCriteria.none(), Optional.empty()).cards().stream()
                .anyMatch(c -> c.caseId().equals(posted.caseId()));
        check(onBoard, "not on board");
        check(s.agencies.findById(sunrise).orElseThrow().lastPostedZip().orElseThrow().value().equals("33165"), "S10.3");
        return "case #" + posted.caseNumber() + ", county re-derived Miami-Dade, expires in 7 days, on board, ZIP remembered";
    }

    private static String tc10() {
        CasosSystem s = seeded();
        AgencyId sunrise = byNumber(s, 1001).agencyId();
        Session coordinator = signIn(s, SeedData.DEMO_COORDINATOR);
        int before = s.cases.forAgency(sunrise).size();
        CaseDraft bad = new CaseDraft(sunrise, CoverageType.ONGOING, Credential.RBT, new ZipCode("10001"), null,
                AgeBand.AGE_3_5, 60, WeeklySchedule.empty(), new com.casos.domain.InsurancePlan("ACME", "Acme"),
                Set.of(), Optional.empty(), Set.of());
        ValidationException e = expectThrowsValue(ValidationException.class, () -> s.caseService.post(bad, coordinator));
        check(e.violations().size() == 4, "violations " + e.violations());
        check(s.cases.forAgency(sunrise).size() == before, "case created");
        return e.violations().size() + " problems reported: " + String.join(" | ", e.violations());
    }

    private static String tc11() {
        MutableClock clock = new MutableClock(START);
        CasosSystem s = new CasosSystem(clock, "http://localhost");
        SeedData.load(s);
        Case a = byNumber(s, 1001);
        Case b = byNumber(s, 1002);
        Session coordinator = signIn(s, SeedData.DEMO_COORDINATOR);
        s.caseService.markFilled(a.caseId(), coordinator);
        check(a.status() == CaseStatus.FILLED, "status");
        check(board(s, FilterCriteria.none(), Optional.empty()).cards().stream().noneMatch(c -> c.caseId().equals(a.caseId())), "still listed");
        check(s.board.detail(a.caseId(), Optional.empty(), Optional.empty(), "t").isEmpty(), "detail still open");
        expectThrows(IllegalStateException.class, () -> s.caseService.markFilled(a.caseId(), coordinator));
        clock.advance(Duration.ofDays(5));
        s.caseService.renew(b.caseId(), coordinator);
        check(b.expiresAt().equals(clock.instant().plus(Duration.ofDays(7))), "renew window");
        return "#1001 Filled and removed; refill refused; #1002 renewed on day 5, now expires day 12";
    }

    private static String tc12() {
        MutableClock clock = new MutableClock(START);
        CasosSystem s = new CasosSystem(clock, "http://localhost");
        SeedData.load(s);
        int open = s.cases.findOpen(clock.instant()).size();
        clock.advance(Duration.ofDays(7).plusMinutes(1));
        check(s.cases.findOpen(clock.instant()).isEmpty(), "still visible after 7 days");
        int expired = s.expiryJob.runNightlyExpiry();
        check(expired == open, "expired " + expired);
        check(s.cases.findById(byNumberAny(s, 1001).caseId()).orElseThrow().status() == CaseStatus.EXPIRED, "status");
        long logged = s.eventStore.query(Optional.empty(), START, clock.instant().plusSeconds(1), Optional.of("CaseExpired")).size();
        check(logged == open, "logged " + logged);
        return open + " cases hidden at day 7 before the job ran; job marked " + expired + " Expired and logged them";
    }

    private static String tc13() throws Exception {
        CasosSystem s = seeded();
        Case palmCase = byNumber(s, 1001, County.BROWARD);
        Session sunriseCoordinator = signIn(s, SeedData.DEMO_COORDINATOR);
        expectThrows(UnauthorizedException.class, () -> s.caseService.markFilled(palmCase.caseId(), sunriseCoordinator));
        expectThrows(UnauthorizedException.class, () -> s.inbox.forAgency(palmCase.agencyId(), sunriseCoordinator));
        expectThrows(UnauthorizedException.class, () -> s.caseService.casesFor(null));
        check(palmCase.status() == CaseStatus.OPEN, "case changed");
        try (Running app = Running.start()) {
            HttpResponse<String> r = app.get("/agency");
            check(r.statusCode() == 303 && r.headers().firstValue("Location").orElse("").startsWith("/signin"), "no redirect");
        }
        return "cross-agency fill and inbox refused, no-session call refused, /agency 303 to /signin";
    }

    private static String tc14() throws Exception {
        CasosSystem s = seeded();
        Case target = byNumber(s, 1001);
        board(s, FilterCriteria.none(), Optional.empty());
        s.board.detail(target.caseId(), Optional.empty(), Optional.empty(), "visitor-1");
        s.interestService.expressInterest(new InterestRequest(target.caseId(),
                new Contact("Ana", "ana@example.com", ""), Optional.empty(), Optional.empty()));
        Instant to = s.clock.instant().plusSeconds(1);
        List<EventRecord> detail = s.eventStore.query(Optional.empty(), Instant.EPOCH, to, Optional.of("CaseDetailViewed"));
        check(detail.size() == 1 && detail.get(0).agencyId().equals(Optional.of(target.agencyId())), "attribution");
        check(s.eventStore.query(Optional.empty(), Instant.EPOCH, to, Optional.of("BoardViewed")).size() == 1, "board view");
        Session coordinator = signIn(s, SeedData.DEMO_COORDINATOR);
        String csv = s.reports.exportCsv(s.reports.casePerformance(target.agencyId(), coordinator,
                new DateRange(Instant.EPOCH, s.clock.instant().plusSeconds(1))));
        check(csv.startsWith("case_number,detail_views,interests,outcome"), "csv header");
        check(csv.contains("1001,1,1,OPEN"), "csv row: " + csv);
        return "events logged (" + s.eventStore.size() + " total), detail view attributed to Sunrise, CSV row 1001,1,1,OPEN";
    }

    // ------------------------------------------------------------------ helpers

    private static CasosSystem seeded() {
        CasosSystem s = new CasosSystem(new MutableClock(START), "http://localhost");
        SeedData.load(s);
        return s;
    }

    private static Session signIn(CasosSystem s, String email) {
        return s.auth.verify(s.auth.requestMagicLink(email).token());
    }

    private static Session owner(CasosSystem s, AgencyId agency) {
        var member = s.members.membersOf(agency).get(0);
        return new Session("test", member.email(), Optional.of(member.memberId()), Optional.of(agency), s.clock.instant());
    }

    private static Case byNumber(CasosSystem s, int number) {
        return byNumber(s, number, County.MIAMI_DADE);
    }

    /** Case numbers restart per agency, so the county tells the two seeded agencies apart. */
    private static Case byNumber(CasosSystem s, int number, County county) {
        return s.agencies.findAll().stream()
                .flatMap(a -> s.cases.forAgency(a.agencyId()).stream())
                .filter(c -> c.caseNumber() == number && c.county() == county)
                .findFirst().orElseThrow();
    }

    private static Case byNumberAny(CasosSystem s, int number) {
        return byNumber(s, number);
    }

    private static BoardPage board(CasosSystem s, FilterCriteria f, Optional<GeoPoint> viewer) {
        return s.board.openCases(f, viewer, Optional.empty(), "test");
    }

    private static FilterCriteria criteria(boolean cov, Integer dist, Integer hours, Credential cred, Set<TimeBlock> blocks) {
        return new FilterCriteria(cov, Optional.ofNullable(dist), Optional.ofNullable(hours), Optional.ofNullable(cred),
                Optional.empty(), blocks.isEmpty() ? EnumSet.noneOf(TimeBlock.class) : EnumSet.copyOf(blocks), Set.of(), false);
    }

    private static CaseDraft draft(CasosSystem s, AgencyId agency, String zip, County claimedCounty, int hours,
                                   WeeklySchedule schedule, String plan) {
        return new CaseDraft(agency, CoverageType.COVERAGE, Credential.RBT, new ZipCode(zip), claimedCounty,
                AgeBand.AGE_6_9, hours, schedule, s.vocabulary.plan(plan).orElseThrow(),
                Set.of(s.vocabulary.competency("SPANISH").orElseThrow()), Optional.of(2500), Set.of());
    }

    private static WeeklySchedule schedule(DayOfWeek day, TimeBlock block) {
        return new WeeklySchedule(Map.of(day, Set.of(block)));
    }

    private static String firstCaseId(Running app) throws Exception {
        Matcher m = Pattern.compile("case\\?id=([0-9a-f-]{36})").matcher(app.get("/").body());
        check(m.find(), "no case link");
        return m.group(1);
    }

    private static int count(String haystack, String needle) {
        int n = 0;
        for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + 1)) {
            n++;
        }
        return n;
    }

    // ------------------------------------------------------------------ tiny harness

    @FunctionalInterface
    private interface Body {
        String run() throws Exception;
    }

    @FunctionalInterface
    private interface Action {
        Object run() throws Exception;
    }

    private static final class Failure extends RuntimeException {
        Failure(String message) {
            super(message);
        }
    }

    private static void check(boolean condition, String detail) {
        if (!condition) {
            throw new Failure(detail);
        }
    }

    private static <T extends Throwable> String expectThrows(Class<T> type, Action action) {
        return expectThrowsValue(type, action).getMessage();
    }

    private static <T extends Throwable> T expectThrowsValue(Class<T> type, Action action) {
        try {
            action.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) {
                return type.cast(t);
            }
            throw new Failure("expected " + type.getSimpleName() + " but got " + t);
        }
        throw new Failure("expected " + type.getSimpleName() + " but nothing was thrown");
    }

    private static void run(String id, String title, String reqs, String expected, Body body) {
        String actual;
        boolean pass;
        try {
            actual = body.run();
            pass = true;
        } catch (Throwable t) {
            actual = "FAILED: " + t.getMessage();
            pass = false;
        }
        RESULTS.add(new Result(id, title, reqs, expected, actual, pass));
        System.out.printf("%s  %-4s  %s%n        %s%n", id, pass ? "PASS" : "FAIL", title, actual);
    }

    private static void report() throws IOException {
        long passed = RESULTS.stream().filter(Result::pass).count();
        System.out.printf("%n%d of %d functional tests passed.%n", passed, RESULTS.size());
        try (PrintWriter out = new PrintWriter(new FileWriter("test-results.tsv"))) {
            out.println("id\ttitle\trequirements\texpected\tactual\tresult");
            for (Result r : RESULTS) {
                out.println(String.join("\t", r.id, r.title, r.requirements, r.expected,
                        r.actual.replace('\t', ' '), r.pass ? "PASS" : "FAIL"));
            }
        }
        if (passed != RESULTS.size()) {
            System.exit(1);
        }
    }

    /** Starts the real web server on a free port for HTTP-level tests. */
    private static final class Running implements AutoCloseable {
        private final WebServer server;
        private final HttpClient client;

        private Running(WebServer server) {
            this.server = server;
            this.client = HttpClient.newBuilder().cookieHandler(new CookieManager())
                    .followRedirects(HttpClient.Redirect.NEVER).proxy(java.net.ProxySelector.of(null)).build();
        }

        static Running start() throws IOException {
            CasosSystem s = new CasosSystem(java.time.Clock.systemUTC(), "http://localhost");
            SeedData.load(s);
            WebServer server = new WebServer(s, 0);
            server.start();
            return new Running(server);
        }

        HttpResponse<String> get(String path) throws Exception {
            return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + server.port() + path)).GET().build(),
                    HttpResponse.BodyHandlers.ofString());
        }

        @Override
        public void close() {
            server.stop();
        }
    }
}
