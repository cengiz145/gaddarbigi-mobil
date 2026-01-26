package com.example.gaddarquiz.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.theme.GaddarGold
import com.example.gaddarquiz.ui.theme.NeonRed
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onInitializationComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Bilgi Yükleniyor...") }
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing)
    )
    
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0.8f,
        animationSpec = tween(2000, easing = LinearOutSlowInEasing)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(800)
        statusText = "Kule Bekçileri Uyandırılıyor..."
        delay(800)
        statusText = "Hakikat Kristali Parlatılıyor..."
        delay(900)
        onInitializationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lottie Animation
            val composition by com.airbnb.lottie.compose.rememberLottieComposition(
                com.airbnb.lottie.compose.LottieCompositionSpec.RawRes(com.example.gaddarquiz.R.raw.anim_splash)
            )
            val progress by com.airbnb.lottie.compose.animateLottieCompositionAsState(
                composition = composition,
                iterations = 1 // Play once
            )
            
            com.airbnb.lottie.compose.LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(250.dp)
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
            )

            // Animated Title
            Text(
                text = "GADDAR",
                color = NeonRed,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .alpha(alphaAnim)
            )
            Text(
                text = "BİLGİ",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .alpha(alphaAnim)
                    .padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Loading Status
            Text(
                text = statusText,
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.alpha(alphaAnim * 0.7f)
            )
        }
        
        // Progress Bar (Subtle)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .fillMaxWidth(0.5f)
                .height(2.dp)
                .background(Color.DarkGray)
        ) {
            val progressWidth by animateFloatAsState(
                targetValue = if (startAnimation) 1f else 0f,
                animationSpec = tween(2500, easing = LinearEasing)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressWidth)
                    .fillMaxHeight()
                    .background(GaddarGold)
            )
        }
    }
}
