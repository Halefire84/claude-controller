package com.cooperrivertrading.claudecontroller

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Pure file-name builder for "Save to file". */
object ExportName {
    fun fileName(title: String, timestampMillis: Long, zone: TimeZone = TimeZone.getDefault()): String {
        val slug = title.lowercase(Locale.US).replace(Regex("[^a-z0-9]+"), "-").trim('-').ifEmpty { "output" }
        val fmt = SimpleDateFormat("yyyy-MM-dd-HHmm", Locale.US).apply { timeZone = zone }
        return "$slug-${fmt.format(Date(timestampMillis))}.txt"
    }
}
