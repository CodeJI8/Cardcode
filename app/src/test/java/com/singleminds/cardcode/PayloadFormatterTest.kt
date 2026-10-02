package com.singleminds.cardcode

import org.junit.Assert.assertEquals
import org.junit.Test

class PayloadFormatterTest {
    @Test
    fun testWifi() {
        assertEquals("WIFI:T:WPA;S:MyNetwork;P:MyPassword;H:false;;", PayloadFormatter.formatWifi("MyNetwork", "MyPassword", "WPA"))
        assertEquals("WIFI:T:nopass;S:FreeWifi;;", PayloadFormatter.formatWifi("FreeWifi", "", "nopass"))
        assertEquals("WIFI:T:WPA;S:Net\\;work;P:Pass\\,word;H:false;;", PayloadFormatter.formatWifi("Net;work", "Pass,word", "WPA"))
    }

    @Test
    fun testUrl() {
        assertEquals("https://example.com", PayloadFormatter.formatUrl("example.com"))
        assertEquals("https://example.com", PayloadFormatter.formatUrl("https://example.com"))
        assertEquals("http://example.com", PayloadFormatter.formatUrl("http://example.com"))
    }

    @Test
    fun testContact() {
        assertEquals("MECARD:N:John Doe;TEL:123456789;EMAIL:john@example.com;;", PayloadFormatter.formatContact("John Doe", "123456789", "john@example.com"))
        assertEquals("MECARD:N:Jane;;", PayloadFormatter.formatContact("Jane", "", ""))
    }
}
