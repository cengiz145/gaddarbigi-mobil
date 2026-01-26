package com.example.gaddarquiz.feature.tower

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.*
import androidx.compose.ui.draw.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.theme.GaddarGold
import kotlinx.coroutines.delay

@Composable
fun TowerTransitionAnimation(
    targetFloor: Int,
    onAnimationComplete: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(targetFloor) {
        visible = true
        delay(2000) // Duration of the cinematic transition
        visible = false
        delay(500)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // High speed background during climb
        TowerClimbBackground(Modifier.fillMaxSize(), speedMult = 5f)
        VignetteEffect(intensity = 0.6f)
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(1000)) + expandIn(expandFrom = Alignment.Center),
            exit = fadeOut(tween(500)) + shrinkOut(shrinkTowards = Alignment.Center)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "KULEYE TIRMANILIYOR",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Light
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                val floorTranslation by animateFloatAsState(
                    targetValue = if (visible) 0f else 100f,
                    animationSpec = tween(1500, easing = FastOutSlowInEasing),
                    label = "floorRise"
                )

                Text(
                    text = "$targetFloor. KAT",
                    color = GaddarGold,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    modifier = Modifier.graphicsLayer { translationY = floorTranslation }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Simple loading-like pulse or fog could go here
                Box(
                    modifier = Modifier
                        .size(100.dp, 2.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, GaddarGold, Color.Transparent)
                            )
                        )
                )
            }
        }
    }
}
