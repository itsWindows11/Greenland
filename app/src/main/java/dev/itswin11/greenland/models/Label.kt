package dev.itswin11.greenland.models

import kotlinx.serialization.Serializable
import com.atproto.label.Label as AtProtoLabel

@Serializable
data class Label(
    val value: String,
)

fun AtProtoLabel.toLabel(): Label {
    return Label(
        value = `val`,
    )
}