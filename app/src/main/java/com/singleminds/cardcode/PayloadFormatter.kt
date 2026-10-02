package com.singleminds.cardcode

object PayloadFormatter {
    fun formatWifi(ssid: String, password: String, security: String): String {
        val escapedSsid = escape(ssid)
        val escapedPassword = escape(password)
        return if (security == "nopass") {
            "WIFI:T:nopass;S:$escapedSsid;;"
        } else {
            "WIFI:T:$security;S:$escapedSsid;P:$escapedPassword;H:false;;"
        }
    }

    fun formatUrl(url: String): String {
        val trimmed = url.trim()
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }

    fun formatContact(name: String, phone: String, email: String): String {
        val parts = mutableListOf<String>()
        parts.add("MECARD:")
        if (name.isNotBlank()) parts.add("N:$name;")
        if (phone.isNotBlank()) parts.add("TEL:$phone;")
        if (email.isNotBlank()) parts.add("EMAIL:$email;")
        parts.add(";")
        return parts.joinToString("")
    }

    private fun escape(text: String): String {
        return text.replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace(":", "\\:")
            .replace("\"", "\\\"")
    }
}
