package com.jwd.lunchvote.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jwd.lunchvote.core.ui.R

val nanumSquareFontFamily = FontFamily(
    Font(R.font.nanumsquareneo_regular),
    Font(R.font.nanumsquareneo_bold, weight = FontWeight.Bold),
    Font(R.font.nanumsquareneo_extrabold, weight = FontWeight.ExtraBold),
    Font(R.font.nanumsquareneo_light, weight = FontWeight.Light),
    Font(R.font.nanumsquareneo_heavy, weight = FontWeight.W900),
)

val nanumSquareTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 57.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 64.sp
    ),

    displayMedium = TextStyle(
        fontSize = 45.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 52.sp
    ),

    displaySmall = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 44.sp
    ),

    headlineLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 40.sp
    ),

    headlineMedium = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 36.sp
    ),

    headlineSmall = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 32.sp
    ),

    titleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 28.sp
    ),

    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 24.sp
    ),

    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 20.sp
    ),

    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 24.sp
    ),

    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 20.sp
    ),

    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 16.sp
    ),

    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 20.sp
    ),

    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 16.sp
    ),

    labelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = nanumSquareFontFamily,
        lineHeight = 16.sp
    ),
)