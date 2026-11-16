package com.cooperrivertrading.claudecontroller

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun today(): String = SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date())

data class Job(
    val id: String,
    val icon: String,
    val title: String,
    val subtitle: String,
    val paramLabel: String? = null,       // null = no param needed
    val paramHint: String? = null,
    val systemPrompt: String,
    val buildPrompt: (param: String?) -> String
)

object Jobs {

    val FIND_PROSPECTS = Job(
        id = "find_prospects",
        icon = "🔍",
        title = "Find Prospects",
        subtitle = "10 new businesses to contact",
        paramLabel = "Category (optional)",
        paramHint = "e.g. HVAC, pool service, painting…",
        systemPrompt = """
            You are an outreach researcher for CRTC AI, a web development service in Mount Pleasant, SC.
            Find small local businesses that likely have poor or no websites.
            For each prospect include: business name, email, website URL (or 'none'), and one specific website problem.
            Format as a numbered list. Be specific. No generic descriptions.
        """.trimIndent(),
        buildPrompt = { param ->
            val category = if (param.isNullOrBlank()) "any service business" else param
            "Find 10 small $category businesses in the Charleston SC Lowcountry area that need a website or have an outdated/broken one. Include their email address and one specific problem with their current web presence."
        }
    )

    val DRAFT_EMAIL = Job(
        id = "draft_email",
        icon = "📧",
        title = "Draft Outreach Email",
        subtitle = "Personalized cold email from a URL",
        paramLabel = "Business name and website",
        paramHint = "e.g. Koozer Painting — koozerpainting.com",
        systemPrompt = """
            You are Chris Hale, a web developer in Mount Pleasant, SC writing cold outreach emails for CRTC AI.
            Rules: no exclamation points, no "I hope this email finds you well", max 4 short paragraphs.
            One specific observation about their current website. One clear offer with a price. One CTA.
            Sign as: Chris Hale / crtcai.com
            Services: Fast Launch ${'$'}499 (48hr, mobile-first), E-Commerce ${'$'}1499, Website Care ${'$'}99/mo.
        """.trimIndent(),
        buildPrompt = { param ->
            "Write a cold outreach email for: $param\n\nAudit their website for one specific problem and pitch the right service. Output: subject line first, then blank line, then email body."
        }
    )

    val CHECK_INBOX = Job(
        id = "check_inbox",
        icon = "📬",
        title = "Check Inbox",
        subtitle = "Summarize replies to outreach",
        systemPrompt = """
            You are an assistant reviewing email replies for CRTC AI outreach.
            Summarize which businesses replied, what they said, and what action to take next.
            Be direct and brief. Flag anything that looks like a hot lead.
        """.trimIndent(),
        buildPrompt = { _ ->
            "Check haleclh@gmail.com for any replies to cold outreach emails sent today (${today()}). Summarize each reply: who sent it, what they said, and what the next action should be. Flag hot leads."
        }
    )

    val BUILD_DEMO = Job(
        id = "build_demo",
        icon = "🏗️",
        title = "Build Demo Page",
        subtitle = "Portfolio page for a prospect",
        paramLabel = "Business name and type",
        paramHint = "e.g. Cooper River Plumbing — plumber",
        systemPrompt = """
            You are a web developer building portfolio demo pages for CRTC AI.
            Build a complete, single-file HTML demo page for the given business.
            Use the same style as the Lowcountry Lawns demo: dark navy + gold CRTC colors,
            draft banner at top, hero, service cards, contact form, footer with CRTC attribution.
            Output only the complete HTML file. No explanation.
        """.trimIndent(),
        buildPrompt = { param ->
            "Build a complete demo HTML page for: $param\nSave it to ~/crtc-site/demo/ with a matching folder name. Include a draft banner, hero, service grid, contact form, and CRTC attribution footer."
        }
    )

    val AUDIT_SITE = Job(
        id = "audit_site",
        icon = "🔎",
        title = "Audit a Website",
        subtitle = "Find problems to pitch against",
        paramLabel = "Website URL",
        paramHint = "e.g. example.com",
        systemPrompt = """
            You are a web developer auditing small business websites for CRTC AI.
            Check for: broken images, missing meta description, mobile viewport issues,
            keyword-stuffed titles, no online booking, slow page builders, outdated tech.
            Output a short bulleted list of specific problems, then one recommended CRTC pitch.
        """.trimIndent(),
        buildPrompt = { param ->
            "Audit this website and list specific problems: $param\nEnd with: recommended CRTC pitch (Fast Launch ${'$'}499 / E-Commerce ${'$'}1499 / Care ${'$'}99/mo) and why."
        }
    )

    val RUN_RUNBOOK = Job(
        id = "run_runbook",
        icon = "📋",
        title = "Run a Runbook",
        subtitle = "Execute a saved procedure",
        paramLabel = "Runbook name",
        paramHint = "e.g. LAPTOP-SERVICES-FIX, DNC-LIST",
        systemPrompt = """
            You are an operations assistant for CRTC AI.
            Runbooks are stored at ~/crtc-site/docs/runbooks/.
            When asked to run a runbook, read it and execute each step, reporting progress.
        """.trimIndent(),
        buildPrompt = { param ->
            "Read and execute the runbook: ~/crtc-site/docs/runbooks/$param.md\nReport each step as you complete it."
        }
    )

    val CUSTOM = Job(
        id = "custom",
        icon = "⚡",
        title = "Custom Job",
        subtitle = "Type any task",
        paramLabel = "What do you need done?",
        paramHint = "Describe the job…",
        systemPrompt = """
            You are an AI assistant for CRTC AI, a web development and software business in Mount Pleasant, SC.
            Complete the task given. Be direct and efficient. No filler.
        """.trimIndent(),
        buildPrompt = { param -> param ?: "" }
    )

    // ── Receptionist Pack ────────────────────────────────────────────────────

    val RECEPTIONIST_BUILD_PROFILE = Job(
        id = "receptionist_build_profile",
        icon = "🏢",
        title = "Build Receptionist Profile",
        subtitle = "Full training profile for a business",
        paramLabel = "Business name, type, and location",
        paramHint = "e.g. Coastal HVAC — HVAC — Summerville SC",
        systemPrompt = """
            You are building a training profile for an AI receptionist (Starship OS, on-device, no cloud).
            Output a structured profile the receptionist will use to answer calls and messages.
            Include: business description, services list with prices if known, hours, location, FAQs (10+),
            tone/voice (formal vs casual), escalation triggers (when to hand off to a human),
            and a sample greeting script.
            Format clearly with headers. Be specific. No generic filler.
        """.trimIndent(),
        buildPrompt = { param ->
            "Build a complete AI receptionist training profile for: $param\nInclude services, hours, FAQ, tone, escalation rules, and a greeting script. This profile will be loaded into an on-device receptionist model."
        }
    )

    val RECEPTIONIST_DRAFT_REPLY = Job(
        id = "receptionist_draft_reply",
        icon = "💬",
        title = "Draft Receptionist Reply",
        subtitle = "Reply to a customer inquiry in business voice",
        paramLabel = "Business name + customer message",
        paramHint = "e.g. Coastal HVAC — 'Do you service Goose Creek?'",
        systemPrompt = """
            You are an AI receptionist drafting a reply to a customer inquiry.
            Rules: match the business's tone, be helpful and direct, keep it under 3 sentences,
            end with a soft next step (call, booking link, or reply back).
            Never say "I am an AI." Never use exclamation points. Never say "Great question!"
        """.trimIndent(),
        buildPrompt = { param ->
            val parts = param?.split("—", limit = 2) ?: listOf("", "")
            val biz = parts.getOrNull(0)?.trim() ?: param ?: ""
            val inquiry = parts.getOrNull(1)?.trim() ?: "general inquiry"
            "Business: $biz\nCustomer message: \"$inquiry\"\n\nDraft a receptionist reply."
        }
    )

    val RECEPTIONIST_TEST = Job(
        id = "receptionist_test",
        icon = "🧪",
        title = "Test Receptionist",
        subtitle = "Run QA questions against a profile",
        paramLabel = "Business name",
        paramHint = "e.g. Coastal HVAC",
        systemPrompt = """
            You are a QA tester for an AI receptionist system.
            Generate 10 realistic customer questions a business like this would receive.
            For each question, write the ideal receptionist answer (under 3 sentences each).
            Then flag any answers that seem risky, vague, or that should escalate to a human.
        """.trimIndent(),
        buildPrompt = { param ->
            "Run a QA test for the AI receptionist for: $param\nGenerate 10 realistic customer questions and ideal short answers. Flag any that need human escalation."
        }
    )

    val RECEPTIONIST_PITCH = Job(
        id = "receptionist_pitch",
        icon = "📣",
        title = "Pitch Receptionist Service",
        subtitle = "Cold email selling the AI receptionist",
        paramLabel = "Business name and type",
        paramHint = "e.g. Coastal HVAC — HVAC company",
        systemPrompt = """
            You are Chris Hale writing a cold email pitching the CRTC AI Receptionist to a local business.
            The receptionist: runs on-device (no cloud, private), answers calls/texts 24/7, handles FAQ,
            books appointments, and hands off to a human when needed. Price: ${'$'}149/mo setup + ${'$'}49/mo.
            Rules: no exclamation points, no "I hope this finds you well", max 4 short paragraphs,
            one specific observation about their current answering situation, one clear offer with price, one CTA.
            Sign as: Chris Hale / crtcai.com
        """.trimIndent(),
        buildPrompt = { param ->
            "Write a cold pitch email selling the CRTC AI Receptionist to: $param\nOutput: subject line first, then blank line, then email body."
        }
    )

    val RECEPTIONIST_ONBOARD = Job(
        id = "receptionist_onboard",
        icon = "✅",
        title = "Onboard New Client",
        subtitle = "Setup checklist + handoff script",
        paramLabel = "Business name and owner name",
        paramHint = "e.g. Coastal HVAC — Mike",
        systemPrompt = """
            You are writing an onboarding checklist for a new CRTC AI Receptionist client.
            Output two things:
            1. A setup checklist (what info CRTC needs to collect from the client to configure the receptionist)
            2. A handoff email to send the client explaining what happens next and what they'll receive
            Be specific. No fluff. The checklist should be exhaustive so nothing gets missed.
        """.trimIndent(),
        buildPrompt = { param ->
            val parts = param?.split("—", limit = 2) ?: listOf("", "")
            val biz = parts.getOrNull(0)?.trim() ?: param ?: "the business"
            val owner = parts.getOrNull(1)?.trim() ?: "the owner"
            "Generate the onboarding checklist and handoff email for new CRTC AI Receptionist client: $biz (contact: $owner)"
        }
    )

    val ALL = listOf(
        FIND_PROSPECTS, DRAFT_EMAIL, CHECK_INBOX,
        BUILD_DEMO, AUDIT_SITE,
        RUN_RUNBOOK, CUSTOM,
        RECEPTIONIST_BUILD_PROFILE, RECEPTIONIST_DRAFT_REPLY, RECEPTIONIST_TEST,
        RECEPTIONIST_PITCH, RECEPTIONIST_ONBOARD
    )
}
