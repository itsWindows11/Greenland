package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class Delta(
    val duration: Duration,
)