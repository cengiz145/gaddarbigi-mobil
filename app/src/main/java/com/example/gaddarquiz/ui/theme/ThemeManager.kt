package com.example.gaddarquiz.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class GaddarPalette(
    val id: Int,
    val displayName: String,
    val primary: Color,
    val secondary: Color,
    val accent: Color
) {
    NEON_STRIKE(0, "Zehirli Neon", Color(0xFFFF1744), Color(0xFFD500F9), Color(0xFF00E5FF)),
    ROYAL_GOLD(1, "Kraliyet Altını", Color(0xFFFFD700), Color(0xFFFFA000), Color(0xFFFFFFFF)),
    OCEAN_BREEZE(2, "Okyanus Esintisi", Color(0xFF00E5FF), Color(0xFF2979FF), Color(0xFF18FFFF)),
    SUNSET_FIRE(3, "Gün Batımı", Color(0xFFFF9100), Color(0xFFFF3D00), Color(0xFFFFEA00)),
    CYBER_PINK(4, "Siber Pembe", Color(0xFFFF006E), Color(0xFFE500A4), Color(0xFFFFB3DA)),
    TOXIC_GREEN(5, "Zehirli Yeşil", Color(0xFF00FF41), Color(0xFF00B82D), Color(0xFFB8FF00)),
    ICE_BLUE(6, "Buz Mavisi", Color(0xFF00D4FF), Color(0xFF0077B6), Color(0xFFADE8F4)),
    VOLCANIC_RED(7, "Volkanik Kırmızı", Color(0xFFDC2F02), Color(0xFF6A040F), Color(0xFFFAA307))
}

enum class GaddarBackground(
    val id: Int,
    val displayName: String,
    val brush: Brush
) {
    DEEP_SPACE(0, "Derin Uzay", Brush.verticalGradient(listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)))),
    LIVELY_MINT(1, "Canlı Nane", Brush.verticalGradient(listOf(Color(0xFF11998e), Color(0xFF38ef7d)))),
    ROYAL_PURPLE(2, "Asil Mor", Brush.verticalGradient(listOf(Color(0xFF2b1055), Color(0xFF7597de)))),
    MIDNIGHT_CITY(3, "Gece Şehri", Brush.verticalGradient(listOf(Color(0xFF232526), Color(0xFF414345)))),
    CRIMSON_BLAZE(4, "Kızıl Alev", Brush.verticalGradient(listOf(Color(0xFF870000), Color(0xFF190A05)))),
    ELECTRIC_VIOLET(5, "Elektrik Moru", Brush.verticalGradient(listOf(Color(0xFF4A00E0), Color(0xFF8E2DE2)))),
    SUNSET_GLOW(6, "Gün Batımı", Brush.verticalGradient(listOf(Color(0xFFfc4a1a), Color(0xFFf7b733)))),
    AURORA_NIGHTS(7, "Kutup Işıkları", Brush.verticalGradient(listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))))
}

object ThemeManager {
    fun getPalette(id: Int): GaddarPalette = GaddarPalette.values().find { it.id == id } ?: GaddarPalette.NEON_STRIKE
    fun getBackground(id: Int): GaddarBackground = GaddarBackground.values().find { it.id == id } ?: GaddarBackground.DEEP_SPACE
}
