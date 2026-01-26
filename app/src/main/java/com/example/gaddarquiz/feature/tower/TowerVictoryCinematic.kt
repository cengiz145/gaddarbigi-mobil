package com.example.gaddarquiz.feature.tower

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.draw.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.components.GaddarButton
import com.example.gaddarquiz.ui.theme.CyberCyan
import com.example.gaddarquiz.ui.theme.GaddarGold
import com.example.gaddarquiz.ui.theme.NeonRed
import kotlinx.coroutines.delay

@Composable
fun TowerVictoryCinematic(
    finalScore: Int,
    onReturn: () -> Unit
) {
    var phase by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        delay(1000)
        phase = 1 // Darkness fades
        delay(2000)
        phase = 2 // Crystal Appears
        delay(3000)
        phase = 3 // Final Stats
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Background Effects
        TowerClimbBackground(Modifier.fillMaxSize(), speedMult = 0.3f)
        AnimatedNebulaBackground(Modifier.fillMaxSize().alpha(0.5f))
        
        // Dynamic Vignette (Dark at start, Gold at victory)
        val vColor = if (phase >= 3) GaddarGold else Color.Black
        val vIntensity = if (phase >= 3) 0.4f else 0.7f
        VignetteEffect(intensity = vIntensity, color = vColor)

        // Darkness to Light Transition
        val infiniteTransition = rememberInfiniteTransition(label = "glow")
        val glowScale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "scale"
        )

        // PHASE 1-2: THE CRYSTAL
        AnimatedVisibility(
            visible = phase >= 2,
            enter = fadeIn(tween(2000)) + scaleIn(tween(2000), initialScale = 0.5f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Outer Glow
                Canvas(modifier = Modifier.size(200.dp * glowScale).graphicsLayer { alpha = 0.4f }) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(CyberCyan, Color.Transparent)
                        )
                    )
                }
                
                // The Crystal Core
                Canvas(modifier = Modifier.size(120.dp)) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width / 2, 0f)
                        lineTo(size.width, size.height / 3)
                        lineTo(size.width / 2, size.height)
                        lineTo(0f, size.height / 3)
                        close()
                    }
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(GaddarGold, CyberCyan)
                        )
                    )
                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = 0.5f),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
            }
        }

        // PHASE 1 Text: Narrative
        AnimatedVisibility(
            visible = phase == 1,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                "Karanlık dağıldı...\nBilgi yeniden doğdu.",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }

        // PHASE 3: FINAL SCORE & BUTTON
        AnimatedVisibility(
            visible = phase >= 3,
            enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(24.dp)
                    .semantics(mergeDescendants = true) {
                        liveRegion = LiveRegionMode.Assertive
                        contentDescription = "Tebrikler Efsane! Hakikat Kristalini kazandın. Toplam puanın: $finalScore"
                    }
            ) {
                Text(
                    "TEBRİKLER EFSANE!",
                    color = GaddarGold,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { heading() }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Hakikat Kristali Artık Senin Elinde.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, GaddarGold, RoundedCornerShape(16.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        "$finalScore PUAN",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))

                GaddarButton(
                    text = "BİR EFSANE OLARAK DÖN",
                    onClick = onReturn,
                    containerColor = CyberCyan,
                    contentColor = Color.Black
                )
            }
        }
    }
}
