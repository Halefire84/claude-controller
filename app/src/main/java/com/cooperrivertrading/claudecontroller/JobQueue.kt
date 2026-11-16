package com.cooperrivertrading.claudecontroller

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class QueueStep(
    val jobId: String,
    val paramOverride: String? = null   // null = prompt user or chain from previous output
)

data class JobQueue(
    val id: String,
    val icon: String,
    val title: String,
    val subtitle: String,
    val steps: List<QueueStep>
)

object Queues {

    val OUTREACH = JobQueue(
        id = "q_outreach",
        icon = "🚀",
        title = "Full Outreach Run",
        subtitle = "Find → Audit → Draft email → Check inbox",
        steps = listOf(
            QueueStep("find_prospects"),
            QueueStep("audit_site"),
            QueueStep("draft_email"),
            QueueStep("check_inbox")
        )
    )

    val RECEPTIONIST_PITCH = JobQueue(
        id = "q_receptionist",
        icon = "📞",
        title = "Receptionist Pitch Run",
        subtitle = "Find prospects → Build profile → Pitch email",
        steps = listOf(
            QueueStep("find_prospects", "HVAC, pool service, pest control"),
            QueueStep("receptionist_build_profile"),
            QueueStep("receptionist_pitch")
        )
    )

    val SITE_BUILD = JobQueue(
        id = "q_site_build",
        icon = "🏗️",
        title = "Build + Send Demo",
        subtitle = "Audit site → Build demo → Draft pitch email",
        steps = listOf(
            QueueStep("audit_site"),
            QueueStep("build_demo"),
            QueueStep("draft_email")
        )
    )

    val DAILY_OPS = JobQueue(
        id = "q_ops",
        icon = "⚡",
        title = "Daily Ops",
        subtitle = "Check inbox → Run runbook → Custom",
        steps = listOf(
            QueueStep("check_inbox"),
            QueueStep("run_runbook", "DAILY-OUTREACH-RHYTHM")
        )
    )

    val ALL = listOf(OUTREACH, RECEPTIONIST_PITCH, SITE_BUILD, DAILY_OPS)
}
