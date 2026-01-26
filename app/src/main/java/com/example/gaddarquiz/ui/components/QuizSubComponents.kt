package com.example.gaddarquiz.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.theme.*

@Composable
fun QuizHeader(
    score: Int,
    onNavigateBack: () -> Unit,
    showMaratonLabel: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GaddarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Geri Dön",
                onClick = onNavigateBack,
                tint = CyberCyan
            )
            Spacer(modifier = Modifier.weight(1f))
            if (showMaratonLabel) {
                Text(
                    text = "MARATON",
                    color = GaddarGold.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
           modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
           horizontalArrangement = Arrangement.Start,
           verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SKOR", 
                    color = TextWhite.copy(alpha = 0.6f), 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 12.sp
                )
                Text(
                    text = score.toString().padStart(4, '0'),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        shadow = Shadow(color = CyberCyan.copy(alpha = 0.5f), blurRadius = 15f)
                    ),
                    color = CyberCyan
                )
            }
        }
    }
}

@Composable
fun QuizActionButtons(
    halfJokerUsed: Boolean,
    freezeJokerUsed: Boolean,
    passJokerUsed: Boolean,
    onHalfJoker: () -> Unit,
    onFreezeJoker: () -> Unit,
    onPassJoker: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        JokerButton(
             text = "%50", subText = "Yarı", icon = Icons.Default.Percent, 
             isUsed = halfJokerUsed, onClick = onHalfJoker
        )
        JokerButton(
             text = "", subText = "Dondur", icon = Icons.Filled.AcUnit, 
             isUsed = freezeJokerUsed, onClick = onFreezeJoker
        )
        JokerButton(
             text = "", subText = "Pas", icon = Icons.Default.FastForward, 
             isUsed = passJokerUsed, onClick = onPassJoker
        )
    }
}

@Composable
fun QuizGameOverContent(
    score: Int,
    onNextRound: (() -> Unit)?,
    onReturnHome: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if(score > 0) "SONUÇ" else "KAYBETTİN",
            style = MaterialTheme.typography.displayMedium,
            color = TextWhite,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "PUAN: $score",
            style = MaterialTheme.typography.headlineMedium,
            color = GaddarGold
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        if (onNextRound != null) {
            GaddarButton(
                text = "SIRADAKİ TUR", // Next Round
                onClick = onNextRound,
                containerColor = GaddarRed
            )
        } else {
            GaddarButton(
                text = "ANA MENÜYE DÖN",
                onClick = onReturnHome,
                containerColor = GaddarBlue
            )
        }
    }
}
