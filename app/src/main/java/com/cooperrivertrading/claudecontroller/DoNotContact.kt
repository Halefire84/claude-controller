package com.cooperrivertrading.claudecontroller

import android.content.Context

/** Pure matcher: case-insensitive, trimmed. */
object DoNotContact {
    /** Jobs that draft a cold email and should be checked first. */
    val CHECKED_JOBS = setOf("draft_email", "receptionist_pitch")

    /** Business name = text before an em dash or " - " (jobs take "Name — site"). */
    fun businessName(param: String): String =
        param.split("—", " - ", limit = 2)[0].trim()

    /** Returns the listed entry that matches the business, or null. */
    fun match(param: String?, list: Collection<String>): String? {
        val name = businessName(param ?: "")
        if (name.isEmpty()) return null
        return list.firstOrNull { it.trim().equals(name, ignoreCase = true) }
    }

    fun add(list: Collection<String>, name: String): List<String> {
        val n = name.trim()
        if (n.isEmpty() || list.any { it.trim().equals(n, ignoreCase = true) }) return list.toList()
        return list.toList() + n
    }

    fun remove(list: Collection<String>, name: String): List<String> =
        list.filterNot { it.trim().equals(name.trim(), ignoreCase = true) }
}

/** On-device list in plain SharedPreferences (business names only, no secrets). */
object DncPrefs {
    private const val FILE = "dnc"
    private const val KEY = "names"
    fun load(c: Context): List<String> =
        c.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY, "")!!
            .split("\n").filter { it.isNotBlank() }
    fun save(c: Context, list: List<String>) {
        c.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY, list.joinToString("\n")).apply()
    }
}
