package com.pyco.app.models

enum class Tags(val displayName: String) {
    AUTUMN("Autumn"),
    OUTDOORS("Outdoors"),
    CUTE("Cute"),
    FORMAL("Formal"),
    CASUAL("Casual"),
    WINTER("Winter"),
    SUMMER("Summer");

    companion object {
        fun fromDisplayName(displayName: String): Tags? {
            return entries.find { it.displayName == displayName }
        }
    }
}