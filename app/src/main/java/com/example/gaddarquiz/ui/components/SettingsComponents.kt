package com.example.gaddarquiz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.theme.TextGray
import com.example.gaddarquiz.ui.theme.TextWhite

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = com.example.gaddarquiz.ui.theme.CyberCyan, 
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = com.example.gaddarquiz.ui.theme.CyberCyan,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        content()
    }
}

@Composable
fun ColorOption(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp, 32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) TextWhite else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick), 
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(Icons.Default.Check, null, tint = TextWhite)
        }
    }
}

@Composable
fun MusicSelectionItem(
    name: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val themeColor = com.example.gaddarquiz.ui.theme.CyberCyan
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) themeColor.copy(alpha = 0.1f) else com.example.gaddarquiz.ui.theme.GlassWhite)
            .border(
                1.dp, 
                if (isSelected) themeColor else Color.Transparent, 
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote, 
            contentDescription = null, 
            tint = if (isSelected) themeColor else Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            color = if (isSelected) Color.White else Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.Check, null, tint = themeColor, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ThemeSelectionSection(
    currentBackgroundId: Int,
    currentPaletteId: Int,
    onBackgroundSelected: (Int) -> Unit,
    onPaletteSelected: (Int) -> Unit
) {
    SettingsSection(
        title = "TEMA & RENK", 
        icon = Icons.Default.Palette
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Background Selection - Show ALL backgrounds
            Text("Arka Plan", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(com.example.gaddarquiz.ui.theme.GaddarBackground.values().size) { index ->
                    val bg = com.example.gaddarquiz.ui.theme.GaddarBackground.values()[index]
                    BackgroundOption(bg, currentBackgroundId == bg.id) { onBackgroundSelected(bg.id) }
                }
            }

            // Palette Selection - Show ALL palettes  
            Text("Renk Paleti", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(com.example.gaddarquiz.ui.theme.GaddarPalette.values().size) { index ->
                    val pal = com.example.gaddarquiz.ui.theme.GaddarPalette.values()[index]
                    ColorOption(pal.primary, currentPaletteId == pal.id) { onPaletteSelected(pal.id) }
                }
            }
        }
    }
}

@Composable
fun BackgroundOption(
    background: com.example.gaddarquiz.ui.theme.GaddarBackground,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background.brush)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}
