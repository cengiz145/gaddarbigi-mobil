package com.example.gaddarquiz.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.model.Category
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.utils.SettingsManager
import android.content.Context

@Composable
fun WheelControlPanel(
    spinCount: Int,
    isSpinning: Boolean,
    onNavigateBack: () -> Unit,
    onSpinClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top: Header / Back
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            GaddarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Geri",
                onClick = onNavigateBack,
                tint = CyberCyan
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center: Play Button (Primary Action)
        Box(contentAlignment = Alignment.Center) {
            // Glow effect
            Canvas(modifier = Modifier.size(120.dp)) {
                drawCircle(
                    color = NeonRed.copy(alpha = 0.2f),
                    radius = size.minDimension / 2,
                    center = center
                )
            }
            
            Elite3DButton(
                text = if (isSpinning) "BEKLE..." else "OYNA",
                subText = if (spinCount > 0) "KALAN: $spinCount" else "TAMAMLANDI",
                mainColor = NeonRed,
                accentColor = GaddarGold,
                onClick = onSpinClick
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom: Secondary Actions (Report & Settings)
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GaddarIconButton(
                icon = Icons.Default.Warning,
                contentDescription = "Hata Bildir",
                onClick = { /* Reporting logic handled in Home/Category */ },
                tint = Color.Yellow
            )
            Text("HATA BİLDİR", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            GaddarIconButton(
                icon = Icons.Default.Settings,
                contentDescription = "Ayarlar",
                onClick = onSettingsClick,
                tint = Color.White
            )
            Text("AYARLAR", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun StandardWheelCanvas(
    rotation: Float,
    sectors: Int,
    categories: List<Category>
) {
    val sectorAngle = 360f / sectors
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .rotate(rotation)
            .padding(8.dp)
    ) {
         val radius = size.minDimension / 2
         val center = Offset(size.width / 2, size.height / 2)
         val textPaint = android.graphics.Paint().apply {
             color = android.graphics.Color.WHITE
             textSize = 24f 
             textAlign = android.graphics.Paint.Align.CENTER
             isFakeBoldText = true
         }
         
          for (i in 0 until sectors) {
              val category = categories[i]
              drawArc(
                  color = category.color.copy(alpha = 0.8f),
                  startAngle = i * sectorAngle - 90f,
                  sweepAngle = sectorAngle,
                  useCenter = true,
                  topLeft = Offset(center.x - radius, center.y - radius),
                  size = Size(radius * 2, radius * 2),
                  style = Fill
              )
              
              // Labels
              val angleRad = Math.toRadians((i * sectorAngle - 90f + sectorAngle / 2).toDouble())
              val textRadius = radius * 0.65f 
              val x = center.x + (textRadius * Math.cos(angleRad)).toFloat()
              val y = center.y + (textRadius * Math.sin(angleRad)).toFloat()

              drawContext.canvas.nativeCanvas.save()
              drawContext.canvas.nativeCanvas.rotate(
                  i * sectorAngle + sectorAngle / 2,
                  x,
                  y + (textPaint.textSize / 3) 
              )
              drawContext.canvas.nativeCanvas.drawText(
                category.title.uppercase(),
                x,
                y,
                textPaint
              )
              drawContext.canvas.nativeCanvas.restore()
          }
         
         // Cyber Rim
         drawCircle(
             color = CyberCyan,
             radius = radius + 2f,
             center = center,
             style = Stroke(width = 2f)
         )
    }
}

@Composable
fun SettingsDrawerContent(
    context: Context,
    onClose: () -> Unit
) {
     Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp) // Drawer width
            .background(Color(0xFF111111).copy(alpha = 0.95f))
            .border(1.dp, CyberCyan.copy(alpha=0.3f))
            .clickable(enabled = false) {} // Catch clicks
    ) {
         Column(modifier = Modifier.padding(24.dp)) {
             Row(verticalAlignment = Alignment.CenterVertically) {
                 Text(
                     "AYARLAR", 
                     style = MaterialTheme.typography.titleLarge, 
                     color = Color.White,
                     fontWeight = FontWeight.Bold
                 )
                 Spacer(Modifier.weight(1f))
                 IconButton(onClick = onClose) {
                     Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kapat", tint = Color.Gray, modifier = Modifier.rotate(180f))
                 }
             }

             
            // Theme Selection
            com.example.gaddarquiz.ui.components.ThemeSelectionSection(
                currentBackgroundId = SettingsManager.selectedBackgroundId,
                currentPaletteId = SettingsManager.selectedPaletteId,
                onBackgroundSelected = { SettingsManager.setSelectedBackground(context, it) },
                onPaletteSelected = { SettingsManager.setSelectedPalette(context, it) }
            )
             
             Spacer(Modifier.height(16.dp))
             
             Text("Müzik", color = Color.White)
             Switch(checked = true, onCheckedChange = {})
         }
    }
}

@Composable
fun WheelResultDialog(
    category: Category,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Block */ },
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text(
                "KATEGORİ BELİRLENDİ",
                style = MaterialTheme.typography.labelSmall,
                color = category.color,
                letterSpacing = 2.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    category.title.uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Oturuma yeni bir soru eklendi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("DEVAM ET", color = category.color, fontWeight = FontWeight.Bold)
            }
        }
    )
}
