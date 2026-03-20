package com.example.gaddarquiz.ui.screens

import android.widget.Toast
import com.example.gaddarquiz.ui.components.Elite3DButton
import com.example.gaddarquiz.ui.components.SmokeParticle
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gaddarquiz.ui.components.GaddarIconButton
import com.example.gaddarquiz.ui.components.SoundType
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.ui.utils.GaddarMotion.animatedGradientBackground
import com.example.gaddarquiz.ui.utils.GaddarMotion.breathingPulse
import com.example.gaddarquiz.utils.SettingsManager
import com.example.gaddarquiz.utils.LocalMusicManager
import com.example.gaddarquiz.utils.LocalSettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.gaddarquiz.ui.components.CategoryCard_Elite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCategory: () -> Unit,
    onNavigateToWheel: () -> Unit,
    onNavigateToEkpss: () -> Unit,
    onNavigateToRadio: () -> Unit,
    onNavigateToQuiz: (String, Int, String, Int) -> Unit
) {
    val core = com.example.gaddarquiz.ui.utils.LocalSmartCore.current
    val settingsManager = LocalSettingsManager.current
    val musicManager = LocalMusicManager.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    
    // SettingsManager uses mutableStateOf, no need for collectAsState
    val isReducedMotion = settingsManager.isReducedMotion
    
    var showMusicPopup by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    
    // Cyber Glitch Animation State
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    val glitchOffset by if (isReducedMotion) {
        remember { mutableStateOf(0f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = -2f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(150, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glitchOffset"
        )
    }

    val currentPalette = remember(settingsManager.selectedPaletteId) {
        com.example.gaddarquiz.ui.theme.ThemeManager.getPalette(settingsManager.selectedPaletteId)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF111111),
                modifier = Modifier.width(300.dp)
            ) {
                SettingsDrawerContent()
            }
        },
        gesturesEnabled = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Arka Plan Tuvali
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .animatedGradientBackground(
                        colors = listOf(
                            Color(0xFF0F0F1A),
                            currentPalette.primary.copy(alpha = 0.3f),
                            Color(0xFF1A1A2E),
                            currentPalette.accent.copy(alpha = 0.2f),
                            Color(0xFF0F0F1A)
                        )
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(currentPalette.primary.copy(alpha = 0.25f), Color.Transparent),
                            center = Offset(size.width * 0.2f, size.height * 0.2f),
                            radius = 400f
                        ),
                        center = Offset(size.width * 0.2f, size.height * 0.2f),
                        radius = 400f
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(currentPalette.accent.copy(alpha = 0.25f), Color.Transparent),
                            center = Offset(size.width * 0.8f, size.height * 0.8f),
                            radius = 500f
                        ),
                        center = Offset(size.width * 0.8f, size.height * 0.8f),
                        radius = 500f
                    )
                }
            }

            val smokeParticles = remember { List(20) { SmokeParticle() } }
            val smokeInfinite = rememberInfiniteTransition(label = "smoke")
            val smokeTime by if (isReducedMotion) {
                remember { mutableStateOf(0f) }
            } else {
                smokeInfinite.animateFloat(
                    initialValue = 0f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
                    label = "smokeTime"
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                smokeParticles.forEach { p ->
                    p.update(smokeTime)
                    val particleColor = Color.White.copy(alpha = 0.1f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(particleColor, Color.Transparent),
                            center = Offset(p.x * size.width, p.y * size.height),
                            radius = p.size * size.width
                        ),
                        center = Offset(p.x * size.width, p.y * size.height),
                        radius = p.size * size.width
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // MODERN GLASS TOP BAR
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sol tarafta Oyunu Kapat butonu
                        com.example.gaddarquiz.ui.components.GaddarIconButton(
                            icon = Icons.Default.PowerSettingsNew,
                            contentDescription = "Oyunu Kapat",
                            onClick = { showExitDialog = true },
                            tint = NeonRed,
                            soundType = SoundType.CLOSE
                        )
                        
                        // Sağ tarafta Ayarlar + Müzik butonları yan yana
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            com.example.gaddarquiz.ui.components.GaddarIconButton(
                                icon = Icons.Default.Settings,
                                contentDescription = "Ayarlar",
                                onClick = { scope.launch { drawerState.open() } },
                                tint = Color.White,
                                soundType = SoundType.BUTTON
                            )
                            
                            val radioManager = com.example.gaddarquiz.utils.LocalRadioManager.current
                            com.example.gaddarquiz.ui.components.GaddarIconButton(
                                icon = if (radioManager.isPlaying) Icons.Default.CellTower else Icons.Default.Radio,
                                contentDescription = "Gaddar Radyo",
                                onClick = { onNavigateToRadio() },
                                tint = if (radioManager.isPlaying) CyberCyan else Color.White,
                                soundType = SoundType.BUTTON,
                                customIcon = if (radioManager.isBuffering) {
                                    {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = CyberCyan,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                } else null
                            )
                            com.example.gaddarquiz.ui.components.GaddarIconButton(
                                icon = Icons.Default.MusicNote,
                                contentDescription = "Müzik Ayarlar",
                                onClick = { showMusicPopup = true },
                                tint = if (settingsManager.isMusicEnabled) Color(0xFFFFD700) else Color.Gray,
                                soundType = SoundType.BUTTON
                            )
                        }
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(0.50f).fillMaxWidth()
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Multi-layered Aura
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    currentPalette.primary.copy(alpha = 0.5f),
                                    currentPalette.accent.copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                radius = size.minDimension / 1.1f
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                radius = size.minDimension / 2f
                            )
                        )
                    }
                    Image(
                        painter = painterResource(id = com.example.gaddarquiz.R.drawable.gaddaryeni),
                        contentDescription = "GADDAR",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(core.smartPadding(4f))
                            .breathingPulse(scaleTarget = 1.03f, duration = 3500)
                            .graphicsLayer {
                                shadowElevation = 60f
                                if (!isReducedMotion) {
                                    scaleX = 1f + (glitchOffset / 100f)
                                    translationX = glitchOffset
                                }
                            },
                        contentScale = ContentScale.Fit
                    )
                }

                Text(
                    text = "v2.7.0 Premium",
                    color = currentPalette.accent,
                    fontSize = core.smartSP(13f),
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = core.smartPadding(6f))
                )

                Text(
                    text = "SADECE EN GÜÇLÜ ZİHİNLER HAYATTA KALIR",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.5.sp,
                        shadow = Shadow(
                            color = currentPalette.primary.copy(alpha = 0.8f),
                            blurRadius = core.smartDP(20f).value,
                            offset = Offset(0f, 4f)
                        )
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = core.smartPadding(16f), vertical = core.smartPadding(12f))
                        .semantics { heading() }
                )

                Spacer(modifier = Modifier.weight(0.08f))

                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = core.smartPadding(32f)),
                    verticalArrangement = Arrangement.spacedBy(core.smartDP(16f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(core.smartDP(120f)),
                        horizontalArrangement = Arrangement.spacedBy(core.smartDP(16f))
                    ) {
                        com.example.gaddarquiz.ui.components.EliteSideButton(
                            text = "OYNA",
                            subText = "KATEGORİ SEÇ",
                            icon = Icons.Default.PlayArrow,
                            mainColor = CyberCyan,
                            accentColor = GaddarBlue,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = onNavigateToCategory
                        )

                        com.example.gaddarquiz.ui.components.EliteSideButton(
                            text = "YARAMAZ ÇARK",
                            subText = "KAOS ZAMANI",
                            icon = Icons.Default.Refresh,
                            mainColor = GaddarOrange,
                            accentColor = Color.Red,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = onNavigateToWheel
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().height(core.smartDP(100f)),
                        horizontalArrangement = Arrangement.spacedBy(core.smartDP(16f))
                    ) {
                        com.example.gaddarquiz.ui.components.EliteSideButton(
                            text = "EKPSS MERKEZİ",
                            subText = "MEMURLUK YOLUNDA",
                            icon = Icons.AutoMirrored.Filled.Article,
                            mainColor = Color(0xFF1E88E5),
                            accentColor = Color(0xFF64B5F6),
                            modifier = Modifier.weight(if (settingsManager.weeklyQuestionsSolved < settingsManager.weeklyLimit) 1f else 2f).fillMaxHeight(),
                            onClick = { onNavigateToEkpss() }
                        )

                        if (settingsManager.weeklyQuestionsSolved < settingsManager.weeklyLimit) {
                            com.example.gaddarquiz.ui.components.EliteSideButton(
                                text = "HAFTANIN SORULARI",
                                subText = "MART ÖZEL (50/50)",
                                icon = Icons.Default.Stars,
                                mainColor = Color(0xFFFFD700),
                                accentColor = Color(0xFFFFA500),
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                onClick = { 
                                    onNavigateToQuiz("hafta_ozel", 50, "rahat", 0)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Alt tarafta çıkış diyaloğu vb. de olabilir
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Oyundan Çık", color = Color.White) },
            text = { Text("Ayrılmak istediğine emin misin?", color = Color.LightGray) },
            confirmButton = {
                TextButton(onClick = { /* System exit veya activity finish her neyse */ }) {
                    Text("EVET", color = NeonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("HAYIR", color = Color.White)
                }
            },
            containerColor = Color(0xFF111111)
        )
    }

    if (showMusicPopup) {
        Dialog(onDismissRequest = { showMusicPopup = false }) {
            com.example.gaddarquiz.ui.components.SoundPanel_Quantum(
                settingsManager = settingsManager,
                musicManager = musicManager,
                accentColor = currentPalette.primary,
                onDismiss = { showMusicPopup = false }
            )
        }
    }
}
