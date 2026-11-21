package com.cooperrivertrading.claudecontroller

import org.junit.Assert.*
import org.junit.Test

class HistoryStoreTest {
    private class Mem : HistoryStorage {
        val rows = mutableListOf<HistoryEntry>()
        private var next = 1L
        override fun insert(timestampMillis: Long, jobType: String, param: String, output: String): Long {
            rows += HistoryEntry(next, timestampMillis, jobType, param, output); return next++
        }
        override fun all() = rows.toList()
        override fun get(id: Long) = rows.find { it.id == id }
        override fun delete(id: Long) { rows.removeAll { it.id == id } }
    }

    @Test fun recordsAndReturnsFullOutput() {
        val s = HistoryStore(Mem())
        val long = "x".repeat(5000)
        val id = s.record(1000, "draft_email", "  Koozer  ", long)!!
        val e = s.get(id)!!
        assertEquals(long, e.output)
        assertEquals("Koozer", e.param)
        assertEquals("draft_email", e.jobType)
    }

    @Test fun listIsNewestFirst() {
        val s = HistoryStore(Mem())
        s.record(100, "a", null, "one")
        s.record(300, "b", null, "three")
        s.record(200, "c", null, "two")
        assertEquals(listOf("b", "c", "a"), s.list().map { it.jobType })
    }

    @Test fun equalTimestampsNewestIdFirst() {
        val s = HistoryStore(Mem())
        s.record(100, "first", null, "o"); s.record(100, "second", null, "o")
        assertEquals("second", s.list().first().jobType)
    }

    @Test fun blankOutputNotStored() {
        val s = HistoryStore(Mem())
        assertNull(s.record(1, "a", null, "  \n "))
        assertTrue(s.list().isEmpty())
    }

    @Test fun deleteRemoves() {
        val s = HistoryStore(Mem())
        val id = s.record(1, "a", null, "o")!!
        s.delete(id)
        assertNull(s.get(id))
    }

    @Test fun previewShortUnchangedLongCutAt200() {
        assertEquals("hello world", HistoryStore.preview("hello\n\n world "))
        val p = HistoryStore.preview("y".repeat(500))
        assertEquals(201, p.length)
        assertTrue(p.endsWith("…"))
    }
}
