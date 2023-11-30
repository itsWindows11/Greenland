package dev.itswin11.greenland.models

import androidx.compose.runtime.Immutable

@Immutable
data class AspectRatio(
    val width: Long,
    val height: Long
) {
    val ratio get() = width / height
}