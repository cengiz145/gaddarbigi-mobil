package com.example.gaddarquiz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.utils.SettingsManager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun SettingsDrawerContent() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val volume = SettingsManager.musicVolume
    val sfxVolume = SettingsManager.sfxVolume
    val currentSong = SettingsManager.selectedMusicId
    val selectedColor = com.example.gaddarquiz.ui.theme.CyberCyan

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "AYARLAR",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 4.sp,
            modifier = Modifier.padding(bottom = 40.dp, top = 24.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
            // Accessibility Mode
            com.example.gaddarquiz.ui.components.SettingsSection(title = "ERİŞİLEBİLİRLİK", icon = Icons.Default.Face) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Görme Engelli Modu", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Ekran okuyucu için optimize eder", color = Color.Gray, fontSize = 12.sp)
                    }
                    Switch(
                        checked = SettingsManager.isAccessibilityMode,
                        onCheckedChange = { SettingsManager.setAccessibilityMode(context, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = selectedColor,
                            checkedTrackColor = selectedColor.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            // Appearance
            // Theme & Appearance
            com.example.gaddarquiz.ui.components.ThemeSelectionSection(
                currentBackgroundId = SettingsManager.selectedBackgroundId,
                currentPaletteId = SettingsManager.selectedPaletteId,
                onBackgroundSelected = { id: Int -> SettingsManager.setSelectedBackground(context, id) },
                onPaletteSelected = { id: Int -> SettingsManager.setSelectedPalette(context, id) }
            )

            // Font Size
            com.example.gaddarquiz.ui.components.SettingsSection(title = "YAZI BOYUTU", icon = Icons.Default.TextFields) {
                Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Metin Büyüklüğü",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "%.1fx".format(SettingsManager.fontScale),
                            color = selectedColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Aa", fontSize = 12.sp, color = Color.Gray)
                        Slider(
                            value = SettingsManager.fontScale,
                            onValueChange = { SettingsManager.setFontScale(context, it) },
                            valueRange = 0.85f..1.30f,
                            steps = 2, // Steps: 0.85, 1.0, 1.15, 1.30
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = selectedColor,
                                activeTrackColor = selectedColor,
                                inactiveTrackColor = Color.DarkGray.copy(alpha = 0.2f)
                            )
                        )
                        Text("Aa", fontSize = 20.sp, color = Color.White)
                    }
                }
            }
            
            // Music Selection
            val songs = listOf(
                "QUIZ_VIBE" to com.example.gaddarquiz.R.raw.music_quiz,
                "COĞRAFYA_BEAT" to com.example.gaddarquiz.R.raw.music_cografya,
                "ÖMERİM_REMIX" to com.example.gaddarquiz.R.raw.music_omerim,
                "PEŞİNDEYİM" to com.example.gaddarquiz.R.raw.music_pesindeyim,
                "YERLİ_MİLLİ" to com.example.gaddarquiz.R.raw.music_yerli_milli,
                "BİZLER" to com.example.gaddarquiz.R.raw.music_sizlerle
            )

            com.example.gaddarquiz.ui.components.SettingsSection(title = "MÜZİK", icon = Icons.Default.MusicNote) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Shuffle Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Müzikleri Karışık Çal", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Şarkı bitince rastgele yenisine geçer", color = Color.Gray, fontSize = 11.sp)
                        }
                        Switch(
                            checked = SettingsManager.isShuffleMusic,
                            onCheckedChange = { SettingsManager.setShuffleMusic(context, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = selectedColor,
                                checkedTrackColor = selectedColor.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        songs.forEach { (name, resId) ->
                            val isSelected = (currentSong == resId) || (currentSong == 0 && resId == com.example.gaddarquiz.R.raw.music_quiz)
                            com.example.gaddarquiz.ui.components.MusicSelectionItem(
                                name = name,
                                isSelected = isSelected,
                                selectedColor = selectedColor,
                                onClick = {
                                    if (currentSong != resId) {
                                        SettingsManager.setSelectedMusic(context, resId)
                                        com.example.gaddarquiz.utils.MusicManager.playMusic(context, resId)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Sound Volume
            com.example.gaddarquiz.ui.components.SettingsSection(title = "SES", icon = Icons.AutoMirrored.Default.VolumeUp) {
                Column(verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.padding(horizontal = 8.dp)) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Müzik", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            Text("${(volume * 100).toInt()}%", color = selectedColor, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                             value = volume,
                             onValueChange = { SettingsManager.setMusicVolume(context, it) },
                             colors = SliderDefaults.colors(
                                 thumbColor = selectedColor,
                                 activeTrackColor = selectedColor,
                                 inactiveTrackColor = Color.DarkGray.copy(alpha = 0.2f)
                             )
                        )
                    }

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Efektler", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            Text("${(sfxVolume * 100).toInt()}%", color = selectedColor, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = sfxVolume,
                            onValueChange = { SettingsManager.setSfxVolume(context, it) },
                            colors = SliderDefaults.colors(
                                thumbColor = selectedColor,
                                activeTrackColor = selectedColor,
                                inactiveTrackColor = Color.DarkGray.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }

            // Data Management
            com.example.gaddarquiz.ui.components.SettingsSection(title = "VERİ", icon = Icons.Default.Palette) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Soru Geçmişini Sıfırla",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Görülen soruların kaydını siler ve listeyi tazeler.",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    com.example.gaddarquiz.ui.components.GaddarButton(
                        text = "GEÇMİŞİ TEMİZLE",
                        onClick = { SettingsManager.clearSeenQuestions(context) },
                        containerColor = GaddarRed.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "v1.0",
            color = selectedColor.copy(alpha = 0.4f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
    }
}
