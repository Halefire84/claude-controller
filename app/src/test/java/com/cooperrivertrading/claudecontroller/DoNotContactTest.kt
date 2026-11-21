package com.cooperrivertrading.claudecontroller

import org.junit.Assert.*
import org.junit.Test

class DoNotContactTest {
    private val list = listOf("Koozer Painting", "  Coastal HVAC ")

    @Test fun matchesCaseInsensitiveTrimmed() {
        assertEquals("Koozer Painting", DoNotContact.match("  koozer PAINTING  ", list))
        assertEquals("  Coastal HVAC ", DoNotContact.match("coastal hvac", list))
    }

    @Test fun matchesNamePartOfNameAndSite() {
        assertNotNull(DoNotContact.match("Koozer Painting — koozerpainting.com", list))
        assertNotNull(DoNotContact.match("koozer painting - koozerpainting.com", list))
    }

    @Test fun noMatchAndBlank() {
        assertNull(DoNotContact.match("Other Plumbing — x.com", list))
        assertNull(DoNotContact.match("", list))
        assertNull(DoNotContact.match(null, list))
        assertNull(DoNotContact.match("Koozer", list))
    }

    @Test fun addDedupesAndRemoveWorks() {
        val a = DoNotContact.add(list, " koozer painting ")
        assertEquals(2, a.size)
        assertEquals(3, DoNotContact.add(list, "New Co").size)
        assertEquals(listOf("  Coastal HVAC "), DoNotContact.remove(list, "KOOZER painting"))
    }
}
