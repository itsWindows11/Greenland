package dev.itswin11.greenland.main

import java.time.Instant
import java.time.ZonedDateTime


sealed class DateHelpers {
    companion object {
        private val supportedFormats: Array<Regex> = arrayOf(
            Regex("\\d+-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z"),
            Regex("\\d+-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z"),
            Regex("\\d+-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z"),
            Regex("\\d+-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+([+-]\\d{2}:\\d{2})"),
            Regex("\\d+-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+([+-]\\d{2}:\\d{2})")
        )

        fun parseAtProtoIsoDate(date: String): Long {
            for ((index, format) in supportedFormats.withIndex()) {
                if (format.matches(date)) {
                    return when (index) {
                        0, 1, 2 -> Instant.parse(date).toEpochMilli()
                        3, 4 -> ZonedDateTime.parse(date).toEpochSecond()
                        else -> 0
                    }
                }
            }

            return 0
        }
    }
}