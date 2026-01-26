package com.example.gaddarquiz.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val id: String
)
