# Claude Controller

**AI job runner for Android.** Streams Claude API responses, runs automated job queues, includes Receptionist Pack.

$14.99/month · 7-day free trial · Bring your own Anthropic API key

[Download APK](https://github.com/Halefire84/claude-controller/releases/latest) · [Website](https://crtcai.com/apps)

---

## What it does

Tap a job → Claude streams the response live on your screen → Copy or share the output.

Chain jobs into queues that run automatically, passing output from one step to the next.

---

## 12 built-in jobs

| Job | What it does |
|---|---|
| Find Prospects | 10 local businesses to contact, with emails and pain points |
| Draft Outreach Email | Personalized cold email from a business name + URL |
| Check Inbox | Summarizes replies to outreach, flags hot leads |
| Build Demo Page | Complete HTML demo site for a prospect |
| Audit a Website | Specific problems + recommended pitch |
| Run a Runbook | Executes a saved procedure step by step |
| Custom Job | Type any task |
| Build Receptionist Profile | Full training profile for an AI receptionist |
| Draft Receptionist Reply | Customer reply in the business's voice |
| Test Receptionist | 10 QA questions + ideal answers |
| Pitch Receptionist Service | Cold email selling the AI receptionist |
| Onboard New Client | Setup checklist + handoff email |

---

## 4 automated queues

| Queue | Steps |
|---|---|
| Full Outreach Run | Find → Audit → Draft email → Check inbox |
| Receptionist Pitch Run | Find → Build profile → Pitch email |
| Build + Send Demo | Audit → Build demo → Draft pitch |
| Daily Ops | Check inbox → Run runbook |

---

## Setup

1. Download the APK from [Releases](https://github.com/Halefire84/claude-controller/releases/latest)
2. Enable "Install from unknown sources" on your Android device
3. Install and open the app
4. Go to Settings → enter your Anthropic API key
5. Get a key free at [console.anthropic.com](https://console.anthropic.com)

**Requirements:** Android 8.0+ (API 26)

---

## Tech stack

- Kotlin + Material Design 3
- OkHttp SSE streaming
- EncryptedSharedPreferences (API key stored securely)
- ViewBinding
- Kotlin Coroutines + Flow
- GitHub Actions CI → signed release APK on every push

---

## Build from source

```bash
git clone https://github.com/Halefire84/claude-controller.git
cd claude-controller
./gradlew assembleDebug
```

Requires JDK 17 and Android SDK.

---

## License

MIT — built by [CRTC AI](https://crtcai.com), Mount Pleasant, SC.
