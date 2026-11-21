package com.cooperrivertrading.claudecontroller

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Pure date text for prompts. The clock is injectable so tests use a fixed time. */
object PromptDate {
    fun today(nowMillis: Long = System.currentTimeMillis(), zone: TimeZone = TimeZone.getDefault()): String =
        SimpleDateFormat("MMM d, yyyy", Locale.US).apply { timeZone = zone }.format(Date(nowMillis))
}
