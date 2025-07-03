package com.tomiappdevelopment.milk_flow.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp

private fun clampFontSize(
    size: TextUnit,
    min: TextUnit,
    max: TextUnit
): TextUnit {
    // Defensive check for unspecified units
    if (size.isUnspecified) return min
    return when {
        size < min -> min
        size > max -> max
        else -> size
    }
}

@Composable
fun clampedTypography(): Typography {
    val base = Typography()

    return Typography(
        displayLarge = base.displayLarge.copy(
            fontSize = clampFontSize(base.displayLarge.fontSize, 19.4.sp, 26.sp) // was 29.7
        ),
        displayMedium = base.displayMedium.copy(
            fontSize = clampFontSize(base.displayMedium.fontSize, 16.sp, 22.sp) // was 25
        ),
        displaySmall = base.displaySmall.copy(
            fontSize = clampFontSize(base.displaySmall.fontSize, 13.7.sp, 18.sp) // was 21
        ),
        headlineLarge = base.headlineLarge.copy(
            fontSize = clampFontSize(base.headlineLarge.fontSize, 12.6.sp, 16.5.sp) // was 19.8
        ),
        headlineMedium = base.headlineMedium.copy(
            fontSize = clampFontSize(base.headlineMedium.fontSize, 11.4.sp, 15.sp) // was 18.5
        ),
        headlineSmall = base.headlineSmall.copy(
            fontSize = clampFontSize(base.headlineSmall.fontSize, 10.3.sp, 13.sp) // was 15.8
        ),
        titleLarge = base.titleLarge.copy(
            fontSize = clampFontSize(base.titleLarge.fontSize, 10.3.sp, 12.5.sp) // was 14.5
        ),
        titleMedium = base.titleMedium.copy(
            fontSize = clampFontSize(base.titleMedium.fontSize, 9.1.sp, 11.5.sp) // was 13.2
        ),
        titleSmall = base.titleSmall.copy(
            fontSize = clampFontSize(base.titleSmall.fontSize, 8.sp, 10.sp) // was 12
        ),
        bodyLarge = base.bodyLarge.copy(
            fontSize = clampFontSize(base.bodyLarge.fontSize, 8.sp, 10.sp) // was 12
        ),
        bodyMedium = base.bodyMedium.copy(
            fontSize = clampFontSize(base.bodyMedium.fontSize, 7.4.sp, 9.sp) // your updated
        ),
        bodySmall = base.bodySmall.copy(
            fontSize = clampFontSize(base.bodySmall.fontSize, 6.9.sp, 8.sp) // was 9.2
        ),
        labelLarge = base.labelLarge.copy(
            fontSize = clampFontSize(base.labelLarge.fontSize, 6.9.sp, 8.sp)
        ),
        labelMedium = base.labelMedium.copy(
            fontSize = clampFontSize(base.labelMedium.fontSize, 6.3.sp, 7.5.sp) // was 8.6
        ),
        labelSmall = base.labelSmall.copy(
            fontSize = clampFontSize(base.labelSmall.fontSize, 5.7.sp, 6.8.sp) // was 7.9
        )
    )
}

