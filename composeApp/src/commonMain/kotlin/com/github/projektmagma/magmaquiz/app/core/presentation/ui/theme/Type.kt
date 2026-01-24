package com.github.projektmagma.magmaquiz.app.core.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    labelMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.W600
    ),
    titleMedium = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.W500,
        fontFamily = FontFamily.Default
    )
)
