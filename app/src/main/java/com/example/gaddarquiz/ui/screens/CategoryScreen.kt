package com.example.gaddarquiz.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.ui.components.GaddarIconButton
import com.example.gaddarquiz.ui.components.CategoryCard
import com.example.gaddarquiz.ui.components.ChanceWheelBanner
import com.example.gaddarquiz.utils.SettingsManager
import kotlinx.coroutines.launch
import com.example.gaddarquiz.ui.components.CategoryCard_Elite
import com.example.gaddarquiz.ui.components.ChanceWheelBanner_Elite

@Composable
fun CategoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGameMode: (String) -> Unit,
    onNavigateToWheelSelection: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onReportError: () -> Unit = {}
) {
    val categories = com.example.gaddarquiz.data.CategoryData.defaultCategories

    val currentBackground = com.example.gaddarquiz.ui.theme.ThemeManager.getBackground(SettingsManager.selectedBackgroundId)
    val currentPalette = com.example.gaddarquiz.ui.theme.ThemeManager.getPalette(SettingsManager.selectedPaletteId)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentBackground.brush)
    ) {
        // Decorative Circles for "Lively" feel
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawCircle(
                color = Color(0xFF6A1B9A).copy(alpha = 0.2f),
                center = Offset(x = canvasWidth * 0.9f, y = canvasHeight * 0.1f),
                radius = 150.dp.toPx()
            )
            drawCircle(
                color = Color(0xFF1565C0).copy(alpha = 0.2f),
                center = Offset(x = canvasWidth * 0.1f, y = canvasHeight * 0.5f),
                radius = 200.dp.toPx()
            )
            drawCircle(
                color = Color(0xFFAD1457).copy(alpha = 0.2f),
                center = Offset(x = canvasWidth * 0.8f, y = canvasHeight * 0.9f),
                radius = 180.dp.toPx()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp) // Slightly tighter padding for mobile
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri Dön",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Added Settings and Report Error buttons to fix unused parameter warnings
                IconButton(onClick = onReportError) {
                    Icon(Icons.Default.Email, contentDescription = "Hata Bildir", tint = Color.Gray)
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Ayarlar", tint = Color.Gray)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "KATEGORİ SEÇ",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            shadow = Shadow(color = NeonRed.copy(alpha = 0.5f), blurRadius = 15f)
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "SELECT_MISSION",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonRed,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            // Chance Wheel Banner
            ChanceWheelBanner_Elite(
                onClick = onNavigateToWheelSelection,
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            )

            // Premium Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp), // Extra padding for scrolling
                modifier = Modifier.weight(1f)
            ) {
                items(categories) { category ->
                    CategoryCard_Elite(category, onNavigateToGameMode)
                }
            }
        }
    }
}
