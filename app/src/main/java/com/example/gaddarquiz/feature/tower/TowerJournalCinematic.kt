package com.example.gaddarquiz.feature.tower

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.components.GaddarButton
import com.example.gaddarquiz.ui.theme.CyberCyan
import com.example.gaddarquiz.ui.theme.GaddarGold
import androidx.compose.ui.semantics.*

@Composable
fun TowerJournalCinematic(
    entry: TowerStory.JournalEntry,
    onClose: () -> Unit
) {
    var showSignature by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Atmospheric Background
        TowerClimbBackground(Modifier.fillMaxSize(), speedMult = 0.2f)
        VignetteEffect(intensity = 0.7f)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.8f))
                .border(2.dp, GaddarGold, RoundedCornerShape(16.dp))
                .semantics(mergeDescendants = true) {
                    liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite
                }
                .padding(24.dp)
        ) {
            Text(
                text = entry.title.uppercase(),
                color = GaddarGold,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().semantics { heading() }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(modifier = Modifier.weight(1f, fill = false)) {
                TypewriterText(
                    text = entry.text,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp,
                        fontStyle = FontStyle.Italic
                    ),
                    onComplete = { showSignature = true }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AnimatedVisibility(
                visible = showSignature,
                enter = fadeIn(tween(1000)) + slideInHorizontally(initialOffsetX = { 50 })
            ) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text(
                        text = "— ${entry.author}",
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    GaddarButton(
                        text = "GÜNLÜĞÜ KAPAT VE İLERLE",
                        onClick = onClose,
                        containerColor = GaddarGold,
                        contentColor = Color.Black
                    )
                }
            }
        }
    }
}
