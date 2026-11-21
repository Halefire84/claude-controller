package com.cooperrivertrading.claudecontroller

import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptDateTest {
    private val utc = TimeZone.getTimeZone("UTC")

    @Test fun fixedClockFormats() {
        assertEquals("Nov 21, 2026", PromptDate.today(1795269900000L, utc))
        assertEquals("Jan 5, 2027", PromptDate.today(1799150400000L, utc))
    }

    @Test fun zoneChangesTheDay() {
        // 2026-11-21 02:00 UTC is still Nov 20 in New York
        assertEquals("Nov 20, 2026", PromptDate.today(1795226400000L, TimeZone.getTimeZone("America/New_York")))
    }

    @Test fun checkInboxPromptUsesGivenDateNotHardcoded() {
        val prompt = Jobs.CHECK_INBOX.buildPrompt(null)
        assertTrue(prompt.contains(PromptDate.today()))
        assertFalse(prompt.contains("Nov 16, 2026"))
    }
}
