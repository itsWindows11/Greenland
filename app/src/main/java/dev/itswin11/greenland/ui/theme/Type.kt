package dev.itswin11.greenland.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.itswin11.greenland.R

private val customFont = FontFamily(
    Font(R.font.inter_thin, FontWeight.Thin),
    Font(R.font.inter_extralight, FontWeight.ExtraLight),
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
    Font(R.font.inter_black, FontWeight.Black),
)

private val defaultTypography = Typography()

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = customFont),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = customFont),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = customFont),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = customFont),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = customFont),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = customFont),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = customFont),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = customFont),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = customFont),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = customFont),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = customFont),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = customFont),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = customFont),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = customFont),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = customFont)
)