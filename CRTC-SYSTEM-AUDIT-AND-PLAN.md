# CRTC System Audit and Plan

Date: 2026-11-17. Read-only review. No code in any repo was changed.

## Scope: what I could and could not audit

| Repo | Reviewed? | What it is |
|---|---|---|
| claude-controller | Yes (full) | Android job runner. Calls the Anthropic API directly. 8 Kotlin files, about 840 lines. |
| crtc-os | Yes (structure, security, tests) | Python OS foundation (installer, vault, dashboard, remote pairing) plus an Android remote. About 2,460 lines of Python. |
| seller-toolbox | Inventory only | Android app plus core module and store kit. Not reviewed in depth. |
| **Deal-analysis system** (deal engine, Stripe webhooks, SKU/inventory, Opportunity Radar) | **NOT FOUND** | None of the three repos contains it. The 17 findings (F-01 to F-17) cannot be fixed or even confirmed until it is located. |
| Receptionist (VoIP line 843-452-5507, booking, owner texts) | **NOT AUDITED** | Not in these repos. Only the earlier test is known. Its repo may be crtc-receptionist or crtc-receptionist-app (unverified). |
| Other repos (about 50) | No | Not reviewed. |

An audit of "the full system" is not possible yet. This covers what I can see.

## Findings so far

### claude-controller
1. **API key on the phone, sent straight to Anthropic.** It is stored encrypted (EncryptedSharedPreferences, AES-256), which is good. The risk is a single long-lived key on every device; if a phone is lost, the key must be revoked. Medium.
2. **Job parameters go directly into prompts** (Job.kt: `$param` is inserted into prompt text for audits, outreach and runbooks). That is untrusted input. A pasted website, email reply or listing could carry instructions. The "review email replies" job is the highest risk. Medium. Fix: mark the input as data, never let it choose tools or actions.
3. **Cleartext HTTP is allowed only for localhost and 127.0.0.1.** This is fine. The forge URL is user-editable in Settings, so a non-local http URL would be blocked by the OS. Low.
4. **allowBackup is false, minify is on, and release signing has no debug-key fallback.** Good.
5. **CI signing secrets come from GitHub Secrets.** Acceptable. Low: the keystore is decoded to a runner temp file; confirm it is deleted after the build.
6. **No tests exist** for the app. Medium.
7. **Pricing text is hard-coded in prompts** ($299 + $99, $499, $1,499, $99). Pricing is unapproved for the receptionist, so this can leak into outreach. Medium.

### crtc-os
1. **Security design is strong:** localhost-only dashboard binding, `hmac.compare_digest` for tokens, `secrets.token_urlsafe(32)`, no shell strings, secrets redacted from logs, stat-only permission checks, and `dependency_audit` reports NOT TESTED rather than faking a pass.
2. **Tests: 83 run, 1 failure, 8 errors, 1 skipped.** All 9 problems trace to the installer refusing to run as root, and this container runs as root. They look environmental, not defects. **Not yet confirmed**: I have not re-run them as a normal user.
3. Remote pairing token travels in a QR/JSON payload and an `X-Console-Token` header. Reasonable for Tailscale or localhost; confirm it is never exposed over plain LAN HTTP. Low.
4. Android remote has `PairingTest`, `RemoteApiTest` and an accessibility test. These were not run (no Android build run here).

### seller-toolbox
Not reviewed beyond file listing. No obvious hard-coded secrets in a quick grep, but that is not an audit.

## Plan

**Step 0: find the code (blocker).** You tell me which repo holds the deal engine, Stripe webhooks, inventory and radar, and whether the receptionist is in crtc-receptionist or crtc-receptionist-app. Appraze-ai was denied earlier; it needs your approval to attach.

**Step 1: baseline (one hour per repo).** Clone, run the test suite as a non-root user, record exact commands and results (TESTS RUN / COMMAND / RESULT / PASS / FAIL / SKIPPED).

**Step 2: P0 (deal engine).** In order: canonical BUY decision, Stripe replay protection, refund accounting, category-mismatch pipeline, input validation, unknown costs never zero. One commit each, with regression tests first.

**Step 3: P1.** Buyer-premium units, webhook ordering, SKU identity, typo detection, shared number normalizer.

**Step 4: P2 and security.** Listing input as untrusted data, AI instruction separation, auth and rate limits, persistence and concurrency, recovery.

**Step 5: readiness review** for Android, iOS, Windows and web, then HANDOFF.md and a final deployment checklist.

**Quick fixes I can do now in claude-controller (small, low risk):** add a prompt-injection guard wrapper for `$param`, remove or gate hard-coded pricing, add unit tests for Job and Prefs. Say the word.

## Known limitations of this audit
- Only three repos looked at, and only one in depth.
- No Android build or app tests were run.
- crtc-os tests were run as root, so 9 results are inconclusive.
- Nothing about the live receptionist, Stripe, or any database was reviewed.
