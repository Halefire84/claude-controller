package com.cooperrivertrading.claudecontroller

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
            "Check haleclh@gmail.com for any replies to cold outreach emails sent today (Nov 16, 2026). Summarize each reply: who sent it, what they said, and what the next action should be. Flag hot leads."
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

    val ALL = listOf(FIND_PROSPECTS, DRAFT_EMAIL, CHECK_INBOX, BUILD_DEMO, AUDIT_SITE, RUN_RUNBOOK, CUSTOM)
}
