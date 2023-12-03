package dev.itswin11.greenland.util

import androidx.compose.ui.Modifier

fun Modifier.conditional(condition: Boolean, modifier: Modifier) : Modifier
    = if (condition) then(modifier) else this