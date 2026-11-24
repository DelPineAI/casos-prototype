# Casos

A de-identified case board and pre-credentialing roster pipeline for ABA therapy staffing in South Florida.
Agencies post open therapy cases without any client-identifying information; clinicians (RBTs, BCaBAs, BCBAs)
browse, filter by commute and schedule, and express interest without creating an account.

CEN 4021 Software Engineering II, Fall 2026. Edward Del Pino. Deliverable 5 prototype.

## Run it

Requires a JDK, version 17 or newer (`java -version`). No other installs, no build tool, no database.

```
./run.sh        # starts the app at http://localhost:8080
./test.sh       # runs the 14 functional tests and writes test-results.tsv
```

On Windows, run the two commands inside each script by hand.

## The journey the prototype implements

1. **Browse (SCR-01).** Open http://localhost:8080. No sign-in. Enter a ZIP such as `33199` to see distances.
   Filter by distance, credential, hours, insurance, time of day, or urgent coverage only.
2. **Inspect (SCR-02).** Open a case. Age is a band, location is approximate, and the ZIP is never shown.
3. **Express interest.** Fill in name and email. No account is created.
4. **Agency sign in (SCR-05).** Click *Agency sign in*, enter `coordinator@sunrisebh.test`. The magic-link email
   lands in the development outbox at `/dev/outbox`. Click the link. The pending invitation is claimed on first sign-in.
5. **Dashboard and inbox (SCR-06, SCR-08).** See the case, its interest count, and the clinician's details.
   Mark the interest Contacted.
6. **Post a case (SCR-07).** Every field is a selection. Try ZIP `10001` or 60 hours to see validation.
7. **Resolve.** *Mark filled* removes the case from the public board. *Renew* restarts the seven-day window.
8. **Report.** *Export case performance (CSV)* downloads RPT-01.

## Architecture

The code follows the Assignment 2 structure: one package per Deliverable 1 module.

| Package | Module | Prototype status |
|---|---|---|
| `com.casos.domain` | Shared kernel: entities, value objects, events, ports | Implemented |
| `com.casos.board` | M1 Case Discovery and Browse | Implemented |
| `com.casos.lifecycle` | M2 Case Lifecycle Management | Implemented (one-click email actions specified only) |
| `com.casos.identity` | M3 Identity and Access Control | Implemented |
| `com.casos.clinician` | M4 Clinician Profile and Availability | Structural (Phase 3) |
| `com.casos.roster` | M5 Roster Pipeline | Structural (Phase 4) |
| `com.casos.geo` | M6 Geospatial and Commute | Implemented (list view; map tiles not included) |
| `com.casos.notification` | M7 Notification and Scheduling | Magic-link email to dev outbox; nightly expiry job |
| `com.casos.analytics` | M8 Analytics and Reporting | Event log, attribution, RPT-01, RPT-02 |
| `com.casos.compliance` | M9 De-identification Enforcement | Implemented |
| `com.casos.app` | Composition root, event bus, demo data | Implemented |
| `com.casos.web` | Presentation layer (JDK HTTP server, server-rendered HTML) | Implemented |

Data is held in memory behind the same repository interfaces a database adapter would implement,
so the app resets every time it restarts.

## Branches

Work was done on feature branches merged into `main` with merge commits:
`feature/domain-compliance-geo`, `feature/identity-access`, `feature/case-lifecycle`, `feature/public-board`,
`feature/analytics`, `feature/web-ui`, `feature/functional-tests`, `docs/readme`.
