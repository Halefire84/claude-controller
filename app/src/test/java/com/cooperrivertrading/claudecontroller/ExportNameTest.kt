package com.cooperrivertrading.claudecontroller

import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class ExportNameTest {
    private val utc = TimeZone.getTimeZone("UTC")
    // 2026-11-21 14:05:00 UTC
    private val ts = 1795269900000L

    @Test fun slugAndStamp() =
        assertEquals("draft-outreach-email-2026-11-21-1405.txt", ExportName.fileName("Draft Outreach Email", ts, utc))

    @Test fun symbolsCollapse() =
        assertEquals("a-b-2026-11-21-1405.txt", ExportName.fileName("  A / B!! ", ts, utc))

    @Test fun emptyTitleFallsBack() =
        assertEquals("output-2026-11-21-1405.txt", ExportName.fileName("???", ts, utc))
}
