package com.example.gaddarquiz.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.ui.graphics.vector.ImageVector

enum class YaramazMode(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val difficultyMultiplier: Float = 1.0f // Potential score multiplier
) {
    TIME_BOMB(
        "Saatli Bomba",
        "Süren sadece 5 saniye! Hızlı ol!",
        Icons.Default.Timer,
        1.5f
    ),
    ALL_OR_NOTHING(
        "Hep ya da Hiç",
        "2 Kat Puan ya da SIFIR. Risk büyük!",
        Icons.Default.Diamond,
        2.0f
    ),
    LIAR_JOKER(
        "Yalancı Joker",
        "%50 Jokeri doğru şıkkı silebilir!",
        Icons.Default.SentimentVeryDissatisfied,
        1.2f
    ),
    MIRROR_MODE(
        "Ters Dünya",
        "Ekran baş aşağı döndü!",
        Icons.Default.Flip,
        1.3f
    ),
    GHOST_OPTIONS(
        "Hayalet Şıklar",
        "Şıklar görünüp kayboluyor!",
        Icons.Default.VisibilityOff,
        1.4f
    ),
    REVERSE_TEXT(
        "Ters Metin",
        "Yazılar tersten yazılıyor.",
        Icons.Default.SwapHoriz,
        1.3f
    ),
    NO_VOWELS(
        "Sessiz Harfler",
        "Sesli harfler çalındı!",
        Icons.AutoMirrored.Filled.VolumeOff,
        1.3f
    ),
    SCRIBBLED_TEXT(
        "Sansür",
        "Bazı harfler karalanmış.",
        Icons.Default.Brush,
        1.2f
    ),
    TINY_TEXT(
        "Kör Nokta",
        "Yazılar karınca kadar küçük.",
        Icons.Default.ZoomIn,
        1.2f
    ),
    DRUNK_MODE(
        "Sarhoş Modu",
        "Ekran sallanıyor, başın dönebilir.",
        Icons.Default.Waves,
        1.4f
    ),
    WORD_SALAD(
        "Kelime Salatası",
        "Kelimelerin yeri karıştı.",
        Icons.Default.Shuffle,
        1.3f
    ),
    BLURRY_VISION(
        "Bulanık Görüş",
        "Her şey çok bulanık.",
        Icons.Default.BlurOn,
        1.3f
    ),
    GRAYSCALE(
        "Renk Körlüğü",
        "Dünya siyah beyaz oldu.",
        Icons.Default.InvertColors,
        1.2f
    ),
    SPINNING_OPTIONS(
        "Dönen Şıklar",
        "Butonlar yerinde durmuyor.",
        Icons.Default.Refresh,
        1.3f
    ),
    RAHAT_NEFES(
        "Rahat Nefes",
        "Şanslısın, bu sefer ceza yok.",
        Icons.Default.CheckCircle,
        1.0f
    )
}
