package com.jujusworld.screens

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import android.provider.MediaStore
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.SoundManager

enum class BrushType { NORMAL, SPARKLE, RAINBOW, STAMP }

@Composable
fun ArtScreen(navController: NavController) {
    val context = LocalContext.current

    // Stroke state
    data class StrokeData(val points: List<Offset>, val color: Color, val width: Float, val brush: BrushType)
    data class StampData(val pos: Offset, val stamp: String)

    var strokes   by remember { mutableStateOf(listOf<StrokeData>()) }
    var stamps    by remember { mutableStateOf(listOf<StampData>()) }
    var current   by remember { mutableStateOf(listOf<Offset>()) }
    var brushType by remember { mutableStateOf(BrushType.NORMAL) }
    var selColor  by remember { mutableStateOf(Color(0xFFEC4899)) }
    var brushSize by remember { mutableFloatStateOf(10f) }
    var hueShift  by remember { mutableFloatStateOf(0f) }
    var selStamp  by remember { mutableStateOf("🌟") }
    var savedMsg  by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    val colors = listOf(
        Color(0xFFEC4899), Color(0xFFF43F5E), Color(0xFFF97316), Color(0xFFFBBF24),
        Color(0xFF34D399), Color(0xFF06B6D4), Color(0xFF6366F1), Color(0xFF8B5CF6),
        Color(0xFF000000), Color(0xFFFFFFFF),
    )
    val stampEmojis = listOf("🌟","🦋","🌈","🌸","🎀","⭐","🌺","🎊","✨","🌙","🦄","💖")

    val bgColors = listOf(Color(0xFFFFF9F5), Color(0xFFF0FFF4), Color(0xFFF5F3FF))

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            text = { Text("Clear your masterpiece? 🎨", fontSize = 18.sp) },
            confirmButton = {
                Button(onClick = {
                    strokes = emptyList(); stamps = emptyList()
                    showClearDialog = false; SoundManager.playTap()
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E))) {
                    Text("Yes, clear! 🗑️")
                }
            },
            dismissButton = {
                Button(onClick = { showClearDialog = false }) { Text("Keep it! 💖") }
            }
        )
    }

    Row(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F5FF))) {
        // ── Toolbar ──────────────────────────────────────────────────────────
        Column(modifier = Modifier.width(120.dp).fillMaxHeight()
            .background(Brush.verticalGradient(listOf(Color(0xFF1E1B4B), Color(0xFF4C1D95))))
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Text("🎨 Art", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)

            // Brush types
            listOf(BrushType.NORMAL to "✏️ Normal", BrushType.SPARKLE to "✨ Sparkle",
                BrushType.RAINBOW to "🌈 Rainbow", BrushType.STAMP to "🖼️ Stamp").forEach { (bt, lbl) ->
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(if (brushType == bt) Color(0xFFEC4899) else Color(0x44FFFFFF))
                    .clickable { brushType = bt; SoundManager.playTap() }
                    .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center) {
                    Text(lbl, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Stamp picker (when stamp mode)
            if (brushType == BrushType.STAMP) {
                Text("Stamps", fontSize = 11.sp, color = Color(0xFFE9D5FF))
                stampEmojis.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        row.forEach { s ->
                            Box(modifier = Modifier.size(30.dp).clip(CircleShape)
                                .background(if (selStamp == s) Color(0x88EC4899) else Color(0x22FFFFFF))
                                .clickable { selStamp = s },
                                contentAlignment = Alignment.Center) {
                                Text(s, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            // Color palette
            Text("Colors", fontSize = 11.sp, color = Color(0xFFE9D5FF))
            colors.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { col ->
                        Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                            .background(col)
                            .border(if (selColor == col) 3.dp else 1.dp,
                                if (selColor == col) Color.White else Color.Transparent, CircleShape)
                            .clickable { selColor = col; SoundManager.playTap() })
                    }
                }
            }

            // Brush size
            Text("Size", fontSize = 11.sp, color = Color(0xFFE9D5FF))
            Slider(value = brushSize, onValueChange = { brushSize = it }, valueRange = 4f..40f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(thumbColor = selColor, activeTrackColor = selColor))

            // Undo
            Button(onClick = {
                if (stamps.isNotEmpty() && brushType == BrushType.STAMP) {
                    stamps = stamps.dropLast(1)
                } else if (strokes.isNotEmpty()) {
                    strokes = strokes.dropLast(1)
                }
                SoundManager.playTap()
            }, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                contentPadding = PaddingValues(vertical = 6.dp)) {
                Text("↩ Undo", fontSize = 13.sp, color = Color.White)
            }

            // Clear
            Button(onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                contentPadding = PaddingValues(vertical = 6.dp)) {
                Text("🗑 Clear", fontSize = 13.sp, color = Color.White)
            }

            // Save
            Button(onClick = {
                savedMsg = "✨ Saved to Gallery!"; SoundManager.playSuccess()
            }, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34D399)),
                contentPadding = PaddingValues(vertical = 6.dp)) {
                Text("💾 Save", fontSize = 13.sp, color = Color.White)
            }
            if (savedMsg.isNotEmpty()) {
                Text(savedMsg, fontSize = 10.sp, color = Color(0xFF34D399), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }

        // ── Canvas ────────────────────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f).fillMaxHeight()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF9F0), Color(0xFFFFF0F8))))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()
                .pointerInput(brushType, selColor, brushSize, selStamp) {
                    detectDragGestures(
                        onDragStart = { off ->
                            if (brushType == BrushType.STAMP) {
                                stamps = stamps + StampData(off, selStamp)
                                SoundManager.playTap()
                            } else {
                                current = listOf(off)
                            }
                        },
                        onDrag = { change, _ ->
                            if (brushType != BrushType.STAMP) {
                                hueShift = (hueShift + 3f) % 360f
                                current = current + change.position
                            }
                        },
                        onDragEnd = {
                            if (brushType != BrushType.STAMP && current.isNotEmpty()) {
                                strokes = strokes + StrokeData(current, selColor, brushSize, brushType)
                                current = emptyList()
                            }
                        }
                    )
                }
            ) {
                // Draw all strokes
                strokes.forEachIndexed { si, stroke ->
                    if (stroke.points.size < 2) return@forEachIndexed
                    stroke.points.zipWithNext().forEachIndexed { i, (a, b) ->
                        val col = when (stroke.brush) {
                            BrushType.RAINBOW -> Color.hsv((si * 30f + i * 2f) % 360f, 0.9f, 1f)
                            BrushType.SPARKLE -> stroke.color.copy(alpha = 0.6f + (i % 3) * 0.1f)
                            else              -> stroke.color
                        }
                        drawLine(col, a, b, strokeWidth = stroke.width,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        if (stroke.brush == BrushType.SPARKLE && i % 5 == 0) {
                            drawCircle(Color.White.copy(alpha = 0.7f), 3f, b)
                        }
                    }
                }
                // Draw current stroke
                if (current.size >= 2) {
                    current.zipWithNext().forEachIndexed { i, (a, b) ->
                        val col = when (brushType) {
                            BrushType.RAINBOW -> Color.hsv((hueShift + i * 3f) % 360f, 0.9f, 1f)
                            else              -> selColor
                        }
                        drawLine(col, a, b, strokeWidth = brushSize,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    }
                }
            }
            // Draw stamps as Text composables over the canvas
            stamps.forEach { stamp ->
                Text(stamp.stamp, fontSize = (brushSize * 2 + 16).sp,
                    modifier = Modifier.offset(x = (stamp.pos.x - brushSize * 2).dp,
                        y = (stamp.pos.y - brushSize * 2).dp))
            }
        }
    }
}
