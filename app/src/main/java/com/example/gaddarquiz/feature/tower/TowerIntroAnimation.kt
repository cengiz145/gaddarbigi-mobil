package com.example.gaddarquiz.feature.tower

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.draw.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.components.GaddarButton
import com.example.gaddarquiz.ui.theme.CyberCyan
import com.example.gaddarquiz.ui.theme.NeonRed
import androidx.compose.ui.semantics.*
import kotlinx.coroutines.delay

@Composable
fun TowerIntroAnimation(
    onComplete: () -> Unit
) {
    var phase by remember { mutableIntStateOf(0) }
    val shakeController = rememberShakeController()
    
    // Animation Sequence
    LaunchedEffect(Unit) {
        delay(500)
        phase = 1 // "Dünya sustu..."
        delay(2500)
        phase = 2 // "Gerçeği unuttular..."
        delay(2500)
        phase = 3 // "Sadece KULE kaldı..."
        delay(2500)
        phase = 4 // TITLE REVEAL
        shakeController.shake(20f, 500)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .shake(shakeController),
        contentAlignment = Alignment.Center
    ) {
        // Background Effect
        TowerClimbBackground(Modifier.fillMaxSize(), speedMult = 0.5f)
        AnimatedNebulaBackground(Modifier.fillMaxSize().alpha(0.3f))
        VignetteEffect(intensity = 0.5f)
        GlitchForeground(isActive = phase == 3)

        // Phase 1 Text
        AnimatedVisibility(
            visible = phase == 1,
            enter = fadeIn(tween(1000)),
            exit = fadeOut(tween(500))
        ) {
            Text(
                "Dünya sustu...",
                color = Color.LightGray,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite }
            )
        }

        // Phase 2 Text
        AnimatedVisibility(
            visible = phase == 2,
            enter = fadeIn(tween(1000)),
            exit = fadeOut(tween(500))
        ) {
            Text(
                "Gerçeği unuttular...",
                color = Color.LightGray,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite }
            )
        }

        // Phase 3 Text
        AnimatedVisibility(
            visible = phase == 3,
            enter = fadeIn(tween(200)) + expandVertically(),
            exit = fadeOut(tween(500))
        ) {
            // Shake Effect for this text
            val infiniteTransition = rememberInfiniteTransition()
            val offset by infiniteTransition.animateFloat(
                initialValue = -5f,
                targetValue = 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(50, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = "shake"
            )

            Text(
                "Sadece\nCEHALET\nkaldı.",
                color = NeonRed,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { translationX = offset }
                    .semantics { liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite }
            )
        }

        // Phase 4 - Title Reveal (Final)
        AnimatedVisibility(
            visible = phase >= 4,
            enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn(tween(1500))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
                    .semantics(mergeDescendants = true) { liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite }
            ) {
                Text(
                    "v1.0",
                    color = NeonRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    "CEHALET\nKULESİ",
                    color = CyberCyan,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 50.sp,
                    modifier = Modifier.semantics { heading() }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "Aptallık Çağı Başladı.\nKurtarmaya cesaretin var mı?",
                    color = Color.White.copy(alpha=0.7f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                GaddarButton(
                    text = "GİRMEYE CÜRET ET",
                    onClick = onComplete,
                    containerColor = NeonRed,
                    contentColor = Color.Black
                )
            }
        }
        
        // Skip Button (Always visible but subtle)
        if (phase < 4) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "Bu kısmı geç >>",
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color.Gray,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.clickable { phase = 4 }
                )
            }
        }
    }
}
