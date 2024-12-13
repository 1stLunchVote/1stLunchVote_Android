package com.jwd.lunchvote.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jwd.lunchvote.presentation.R

val nanumSquareFontFamily = FontFamily(
  Font(R.font.nanumsquareneo_regular),
  Font(R.font.nanumsquareneo_bold, weight = FontWeight.Bold),
  Font(R.font.nanumsquareneo_extrabold, weight = FontWeight.ExtraBold),
  Font(R.font.nanumsquareneo_light, weight = FontWeight.Light),
  Font(R.font.nanumsquareneo_heavy, weight = FontWeight.W900),
)

@Composable
fun lunchVoteTypography() = MaterialTheme.typography.copy(
  displayLarge = MaterialTheme.typography.displayLarge.copy(
    fontWeight = FontWeight.Bold,
    fontFamily = nanumSquareFontFamily
  ),
  displayMedium = MaterialTheme.typography.displayMedium.copy(
    fontWeight = FontWeight.Bold,
    fontFamily = nanumSquareFontFamily
  ),
  displaySmall = MaterialTheme.typography.displaySmall.copy(
    fontWeight = FontWeight.Bold,
    fontFamily = nanumSquareFontFamily
  ),
  headlineLarge = MaterialTheme.typography.headlineLarge.copy(
    fontWeight = FontWeight.Bold,
    fontFamily = nanumSquareFontFamily
  ),
  headlineMedium = MaterialTheme.typography.headlineMedium.copy(
    fontWeight = FontWeight.Bold,
    fontFamily = nanumSquareFontFamily
  ),
  headlineSmall = MaterialTheme.typography.headlineSmall.copy(
    fontWeight = FontWeight.Bold,
    fontFamily = nanumSquareFontFamily
  ),
  titleLarge = MaterialTheme.typography.titleLarge.copy(
    fontWeight = FontWeight.ExtraBold,
    fontFamily = nanumSquareFontFamily
  ),
  titleMedium = MaterialTheme.typography.titleMedium.copy(
    fontWeight = FontWeight.ExtraBold,
    fontFamily = nanumSquareFontFamily
  ),
  titleSmall = MaterialTheme.typography.titleSmall.copy(
    fontWeight = FontWeight.ExtraBold,
    fontFamily = nanumSquareFontFamily
  ),
  bodyLarge = MaterialTheme.typography.bodyLarge.copy(
    fontWeight = FontWeight.Bold,
    fontFamily = nanumSquareFontFamily
  ),
  bodyMedium = MaterialTheme.typography.bodyMedium.copy(
    fontWeight = FontWeight.Bold,
    fontFamily = nanumSquareFontFamily
  ),
  bodySmall = MaterialTheme.typography.bodySmall.copy(
    fontWeight = FontWeight.Bold,
    fontFamily = nanumSquareFontFamily
  ),
  labelLarge = MaterialTheme.typography.labelLarge.copy(
    fontWeight = FontWeight.ExtraBold,
    fontFamily = nanumSquareFontFamily
  ),
  labelMedium = MaterialTheme.typography.labelMedium.copy(
    fontWeight = FontWeight.ExtraBold,
    fontFamily = nanumSquareFontFamily
  ),
  labelSmall = MaterialTheme.typography.labelSmall.copy(
    fontWeight = FontWeight.ExtraBold,
    fontFamily = nanumSquareFontFamily
  )
)