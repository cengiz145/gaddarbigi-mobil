package com.example.gaddarquiz.feature.tower

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.ui.components.GaddarButton
import androidx.compose.ui.semantics.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun TowerScreen(
    onNavigateBack: () -> Unit,
    onStartQuiz: (String, Boolean, String) -> Unit // categoryId, isBoss, difficulty
) {
    val state = TowerGameManager.playerState
    val event = TowerGameManager.currentEvent
    val shakeController = rememberShakeController()
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Load state if first time or process death
    LaunchedEffect(Unit) {
        if (state.currentFloor == 1 && state.score == 0 && event is TowerEvent.Intro) {
             TowerGameManager.loadState(context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .shake(shakeController)
    ) {
        // Atmospheric Background
        TowerClimbBackground(Modifier.fillMaxSize(), speedMult = if (event is TowerEvent.Climbing) 4f else 0.5f)
        AnimatedNebulaBackground(Modifier.fillMaxSize().alpha(0.3f))
        
        // Tension Vignette (Red if low health, Black otherwise)
        val vignetteIntensity = if (state.currentHearts <= 1) 0.6f else 0.3f
        val vignetteColor = if (state.currentHearts <= 1) NeonRed else Color.Black
        VignetteEffect(intensity = vignetteIntensity, color = vignetteColor)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .rumble(isActive = state.currentFloor >= 5 || state.currentHearts <= 1),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HUD
            // val context = androidx.compose.ui.platform.LocalContext.current // Already defined above
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Bilgi: ${state.currentHearts} canın kaldı. ${state.currentFloor}. kattasın, ${TowerStory.getFloorTitle(state.currentFloor)}"
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Hearts
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clearAndSetSemantics {  } // Managed by parent
                ) {
                    repeat(state.maxHearts) { index ->
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (index < state.currentHearts) NeonRed else Color.Gray,
                            modifier = Modifier.size(24.dp).padding(end = 4.dp)
                        )
                    }
                }
                
                // Items (HUD)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    state.inventory.distinct().forEach { item ->
                        val count = state.inventory.count { it == item }
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(if(item == TowerItem.HEALTH_POTION) "❤️" else "⏭️", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("x$count", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (state.inventory.isEmpty()) {
                        Text("Çanta Boş", color = Color.Gray, fontSize = 10.sp)
                    }
                }
                
                // Floor Info
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.clearAndSetSemantics {  } // Managed by parent
                ) {
                    Text(
                        "KAT ${state.currentFloor}",
                        color = GaddarGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        TowerStory.getFloorTitle(state.currentFloor),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Content Area
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (val e = event) {
                    is TowerEvent.Intro -> {
                        TowerIntroAnimation(
                            onComplete = { TowerGameManager.startClimb() }
                        )
                    }
                    is TowerEvent.RoomSelection -> {
                        Column(
                             horizontalAlignment = Alignment.CenterHorizontally,
                             verticalArrangement = Arrangement.Center
                        ) {
                             // NPC Quote
                            if (e.npcQuote.isNotEmpty()) {
                                TowerNpcPanel(quote = e.npcQuote)
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            Text(
                                text = "KAPI SEÇ",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp).semantics { heading() }
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                e.doors.forEach { door ->
                                    DoorCard(door = door) {
                                        scope.launch {
                                            shakeController.shake(15f, 300)
                                            delay(300)
                                            TowerGameManager.handleDoorSelection(door)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is TowerEvent.QuestionEncounter -> {
                        val bossText = if(e.isBoss) TowerStory.getBossQuote(state.currentFloor) else null
                        
                        TowerStoryPanel(
                            title = if(e.isBoss) "PATRON SAVAŞI" else "MEYDAN OKUMA",
                            text = bossText ?: "Kategori: ${e.category.name}\nÖdül: ${e.reward?.description ?: "Puan"}\n\nHazır mısın?",
                            buttonText = "SAVAŞ",
                            onClick = { onStartQuiz(e.category.name.lowercase(), e.isBoss, e.difficulty) },
                            isWarning = e.isBoss
                        )
                    }
                    is TowerEvent.JournalFound -> {
                        TowerJournalCinematic(
                            entry = e.entry,
                            onClose = { TowerGameManager.proceedFromStory(context) }
                        )
                    }
                    is TowerEvent.TreasureFound -> {
                        TowerStoryPanel(
                            title = "HAZİNE ODASI",
                            text = "Karanlık bir köşede parlayan bir şey buldun!\n\n${e.item.displayName} kazandın.",
                            buttonText = "TOPLA VE İLERLE",
                            onClick = { TowerGameManager.collectTreasure(e.item, context) },
                            isWarning = false
                        )
                    }
                    is TowerEvent.Climbing -> {
                        LaunchedEffect(e.targetFloor) {
                            com.example.gaddarquiz.utils.SoundManager.playBimbom(context)
                        }
                        TowerTransitionAnimation(
                            targetFloor = e.targetFloor,
                            onAnimationComplete = { TowerGameManager.finishClimbing(context) }
                        )
                    }
                    is TowerEvent.GameOver -> {
                        TowerStoryPanel(
                            title = "ÖLÜM",
                            text = "Ruhun cehalet kulesinde kayboldu.\n\nUlaşılan Kat: ${state.currentFloor}",
                            buttonText = "TEKRAR DOĞ",
                            onClick = { TowerGameManager.startGame(context) },
                            isWarning = true
                        )
                    }
                    is TowerEvent.Victory -> {
                        TowerVictoryCinematic(
                            finalScore = e.finalScore,
                            onReturn = { onNavigateBack() }
                        )
                    }
                    is TowerEvent.StoryMoment -> {
                         TowerStoryPanel(
                            title = if(!state.isAlive) "SON SÖZ" else "ZAFER",
                            text = e.text,
                            buttonText = if(!state.isAlive) "SON" else "YÜKSEL",
                            onClick = { TowerGameManager.proceedFromStory(context) },
                            isWarning = !state.isAlive
                        )
                    }
                }
            }
        }
        
        // Progress Indicator (Side height bar)
        TowerProgressIndicator(
            currentFloor = state.currentFloor,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun TowerProgressIndicator(currentFloor: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(10.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(5.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        val totalFloors = 10f
        val progress = (currentFloor / totalFloors).coerceIn(0f, 1f)
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(progress)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(com.example.gaddarquiz.ui.theme.CyberCyan, com.example.gaddarquiz.ui.theme.GaddarGold)
                    )
                )
        )
    }
}

@Composable
fun DoorCard(door: TowerDoor, onClick: () -> Unit) {
    val isRisky = door.type == TowerDoorType.RISKY
    val color = if (isRisky) NeonRed else CyberCyan
    val imageRes = if (isRisky) com.example.gaddarquiz.R.drawable.risky_door else com.example.gaddarquiz.R.drawable.safe_door
    
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp)
            .clickable(
                onClick = onClick,
                onClickLabel = "${door.category.name} kapısını seç"
            )
            .semantics(mergeDescendants = true) {
                val description = if (isRisky) "Riskli Kapı" else "Güvenli Kapı"
                contentDescription = "$description. Kategori: ${door.category.name}. Zorluk: ${door.difficulty}. Ödül: ${door.reward.description}"
            },
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = androidx.compose.foundation.BorderStroke(2.dp, color)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Scrim for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
            
            Column(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Badge (Category)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(1.dp, color, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        door.category.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                // Bottom Info
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(door.difficulty, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(door.reward.description, color = GaddarGold, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun TowerStoryPanel(
    title: String,
    text: String,
    buttonText: String,
    onClick: () -> Unit,
    isWarning: Boolean = false,
    useTypewriter: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF111111).copy(alpha = 0.9f))
            .border(1.dp, if(isWarning) NeonRed else CyberCyan, RoundedCornerShape(24.dp))
            .semantics(mergeDescendants = true) {
                liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite
            }
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            color = if(isWarning) NeonRed else CyberCyan,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (useTypewriter) {
            TypewriterText(
                text = text,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                text,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        GaddarButton(
            text = buttonText,
            onClick = onClick,
            containerColor = if(isWarning) NeonRed else CyberCyan,
            contentColor = Color.Black
        )
    }
}

@Composable
fun TowerNpcPanel(quote: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 24.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
            .semantics(mergeDescendants = true) {
                liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite
                contentDescription = "Gezgin Bilge diyor ki: $quote"
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "NPC",
                tint = CyberCyan,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Gezgin Bilge", color = CyberCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(quote, color = Color.White, fontSize = 14.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        }
    }
}
