package com.example.gaddarquiz.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object VisualManager {
    // Centralized Colors
    val ColorCorrect = Color(0xFF22C55E) // Green
    val ColorWrong = Color(0xFFEF4444)   // Red
    val ColorPrimary = Color(0xFFFFD700) // Gold
    val ColorBackground = Color(0xFF0F172A) // Dark Blue
    
    // Difficulty Colors
    val ColorEasy = Color(0xFF4ADE80)   // Light Green
    val ColorMedium = Color(0xFFFACC15) // Yellow
    val ColorHard = Color(0xFFF87171)   // Light Red

    // Dimensions
    val PaddingStandard = 16.dp
    val PaddingLarge = 24.dp
    val CornerRadiusStandard = 16.dp
    val CornerRadiusLarge = 24.dp
    
    // Text Sizes (Scales logic can go here if needed)
}
