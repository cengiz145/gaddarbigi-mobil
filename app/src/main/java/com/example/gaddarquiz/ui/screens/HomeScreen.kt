package com.example.gaddarquiz.ui.screens

import android.widget.Toast
import com.example.gaddarquiz.ui.components.Elite3DButton
import com.example.gaddarquiz.ui.components.SmokeParticle
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.*
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.R
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.utils.SettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.gaddarquiz.ui.components.CategoryCard_Elite
import com.example.gaddarquiz.ui.components.ChanceWheelBanner_Elite
import kotlin.random.Random
import androidx.activity.compose.BackHandler

@Composable
fun HomeScreen(
    onNavigateToCategory: () -> Unit,
    onNavigateToWheel: () -> Unit,
    onNavigateToTower: () -> Unit,
    onReportError: () -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Çıkmak mı?", fontWeight = FontWeight.Bold) },
            text = { Text("Uygulamadan çıkmak istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(onClick = { 
                    (context as? android.app.Activity)?.finish()
                }) {
                    Text("Evet, Çık", color = NeonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("İptal", color = CyberCyan)
                }
            },
            containerColor = Color(0xFF1A1A1A),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }


    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    val glitchOffset by infiniteTransition.animateFloat(
        initialValue = -5f, targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glitchOffset"
    )

    val currentBackground = com.example.gaddarquiz.ui.theme.ThemeManager.getBackground(SettingsManager.selectedBackgroundId)
    val currentPalette = com.example.gaddarquiz.ui.theme.ThemeManager.getPalette(SettingsManager.selectedPaletteId)

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
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(brush = currentBackground.brush)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFF1744).copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width * 0.2f, size.height * 0.2f),
                        radius = 400f
                    ),
                    center = Offset(size.width * 0.2f, size.height * 0.2f),
                    radius = 400f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFD500F9).copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width * 0.8f, size.height * 0.8f),
                        radius = 500f
                    ),
                    center = Offset(size.width * 0.8f, size.height * 0.8f),
                    radius = 500f
                )
            }

            val smokeParticles = remember { List(20) { SmokeParticle() } }
            val smokeInfinite = rememberInfiniteTransition(label = "smoke")
            val smokeTime by smokeInfinite.animateFloat(
                initialValue = 0f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing))
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                smokeParticles.forEach { p ->
                    p.update(smokeTime)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.1f), Color.Transparent),
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
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    com.example.gaddarquiz.ui.components.GaddarIconButton(
                        icon = Icons.Default.Email,
                        contentDescription = "Hata Bildir",
                        onClick = onReportError,
                        tint = Color.White
                    )
                    com.example.gaddarquiz.ui.components.GaddarIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Ayarlar",
                        onClick = { scope.launch { drawerState.open() } },
                        tint = Color.White
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(0.50f).fillMaxWidth()
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    currentPalette.primary.copy(alpha = 0.6f),
                                    currentPalette.accent.copy(alpha = 0.3f),
                                    Color.Transparent
                                ),
                                radius = size.minDimension / 1.0f
                            )
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.gaddaryeni),
                        contentDescription = "GADDAR",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .graphicsLayer {
                                shadowElevation = 60f
                                scaleX = 1f + (glitchOffset / 100f)
                                translationX = glitchOffset
                            },
                        contentScale = ContentScale.Fit
                    )
                }

                Text(
                    text = "v1.0",
                    color = currentPalette.accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "SADECE EN GÜÇLÜ ZİHİNLER HAYATTA KALIR",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        shadow = Shadow(
                            color = currentPalette.primary.copy(alpha = 0.9f),
                            blurRadius = 16f
                        )
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                Spacer(modifier = Modifier.weight(0.08f))

                // RESTORED SIDE-BY-SIDE ELITE BUTTONS
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        com.example.gaddarquiz.ui.components.EliteSideButton(
                            text = "OYNA",
                            subText = "KATEGORİ_SEÇ",
                            icon = Icons.Default.PlayArrow,
                            mainColor = CyberCyan,
                            accentColor = GaddarBlue,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = onNavigateToCategory
                        )

                        com.example.gaddarquiz.ui.components.EliteSideButton(
                            text = "YARAMAZ ÇARK",
                            subText = "KAOS_ZAMANI",
                            icon = Icons.Default.Refresh,
                            mainColor = GaddarOrange,
                            accentColor = Color.Red,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = onNavigateToWheel
                        )
                    }

                    Elite3DButton(
                        text = "CEHALET KULESİ",
                        subText = "ZİRVEYE_YÜKSEL",
                        mainColor = GaddarGold,
                        accentColor = NeonRed,
                        modifier = Modifier.fillMaxWidth().height(90.dp),
                        onClick = onNavigateToTower
                    )
                }
            }
        }
    }
}
