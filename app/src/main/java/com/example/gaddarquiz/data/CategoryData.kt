package com.example.gaddarquiz.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.ui.graphics.Color
import com.example.gaddarquiz.model.Category

object CategoryData {
    val defaultCategories = listOf(
        Category(
            title = "COĞRAFYA", 
            subtitle = "Dünya avuçlarının içinde.", 
            icon = Icons.Default.Public, 
            color = Color(0xFF00B0FF), // More vibrant Blue
            id = "cografya"
        ),
        Category(
            title = "TARİH", 
            subtitle = "Geçmişe yolculuk başlasın.", 
            icon = Icons.Default.HistoryEdu, 
            color = Color(0xFFFFD600), // Vibrant Yellow/Gold
            id = "tarih"
        ),
        Category(
            title = "GENEL KÜLTÜR", 
            subtitle = "Bilgini sına!", 
            icon = Icons.Default.Lightbulb, 
            color = Color(0xFFF43F5E), 
            id = "genel_kultur"
        ),
        Category(
            title = "PSİKOLOJİ", 
            subtitle = "Bilgini sına!", 
            icon = Icons.Default.Psychology, 
            color = Color(0xFF3B82F6), 
            id = "psikoloji"
        ),
        Category(
            title = "EDEBİYAT", 
            subtitle = "Bilgini sına!", 
            icon = Icons.AutoMirrored.Default.MenuBook, 
            color = Color(0xFFF97316), 
            id = "edebiyat"
        ),
        Category(
            title = "SPOR", 
            subtitle = "Sahaların hakimi sen misin?", 
            icon = Icons.Default.EmojiEvents, 
            color = Color(0xFFEF4444), 
            id = "spor"
        ),
        Category(
            title = "SİNEMA", 
            subtitle = "Bilgini sına!", 
            icon = Icons.Default.MovieFilter, 
            color = Color(0xFFD500F9), // Vibrant Purple
            id = "sinema"
        ),
        Category(
            title = "TEKNOLOJİ", 
            subtitle = "Geleceği kodlayan sen misin?", 
            icon = Icons.Default.Computer, 
            color = Color(0xFF00E676), // Vibrant Green
            id = "teknoloji"
        )
    )
}
