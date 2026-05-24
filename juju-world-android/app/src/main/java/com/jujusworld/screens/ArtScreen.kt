package com.jujusworld.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class DrawPath(val points: List<Offset>, val color: Color, val width: Float)

@Composable
fun ArtScreen(navController: NavController) {
    var paths by remember { mutableStateOf(listOf<DrawPath>()) }
    var currentPoints by remember { mutableStateOf(listOf<Offset>()) }
    var selectedColor by remember { mutableStateOf(Color(0xFFEC4899)) }
    var brushSize by remember { mutableStateOf(10f) }
    var isEraser by remember { mutableStateOf(false) }

    val palette = listOf(
        Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF3B82F6), Color(0xFF10B981),
        Color(0xFFFBBF24), Color(0xFFF97316), Color(0xFFEF4444), Color(0xFF06B6D4),
        Color(0xFFFFFFFF), Color(0xFF1E1B4B), Color(0xFF6B7280), Color.Black
    )

    val stamps = listOf("🦋", "⭐", "🌸", "🦄", "🌈", "💖", "✨", "🌟")
    var stampPositions by remember { mutableStateOf(listOf<Pair<Offset, String>>()) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {
        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF1E293B)).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Text("🎨 Art", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.weight(1f))
            // Brush size slider
            Text("Size", fontSize = 12.sp, color = Color.White)
            Slider(
                value = brushSize,
                onValueChange = { brushSize = it },
                valueRange = 5f..40f,
                modifier = Modifier.width(100.dp),
                colors = SliderDefaults.colors(thumbColor = Color(0xFFEC4899), activeTrackColor = Color(0xFFEC4899))
            )
            // Eraser
            IconButton(onClick = { isEraser = !isEraser }) {
                Text(if (isEraser) "✏️" else "🧹", fontSize = 20.sp)
            }
            // Undo
            IconButton(onClick = { if (paths.isNotEmpty()) paths = paths.dropLast(1) }) {
                Icon(Icons.Filled.Undo, "Undo", tint = Color.White)
            }
            // Clear
            IconButton(onClick = { paths = listOf(); stampPositions = listOf() }) {
                Icon(Icons.Filled.Delete, "Clear", tint = Color(0xFFEF4444))
            }
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Left sidebar: colors
            Column(
                modifier = Modifier.width(56.dp).fillMaxHeight().background(Color(0xFF1E293B)).padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                palette.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (color == selectedColor && !isEraser)
                                    Modifier.border(3.dp, Color.White, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = color; isEraser = false }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                // Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset -> currentPoints = listOf(offset) },
                                onDrag = { change, _ ->
                                    currentPoints = currentPoints + change.position
                                },
                                onDragEnd = {
                                    val drawColor = if (isEraser) Color.White else selectedColor
                                    val drawWidth = if (isEraser) brushSize * 3 else brushSize
                                    paths = paths + DrawPath(currentPoints, drawColor, drawWidth)
                                    currentPoints = listOf()
                                }
                            )
                        }
                ) {
                    // Draw completed paths
                    paths.forEach { path ->
                        if (path.points.size > 1) {
                            val androidPath = Path()
                            androidPath.moveTo(path.points.first().x, path.points.first().y)
                            path.points.drop(1).forEach { pt -> androidPath.lineTo(pt.x, pt.y) }
                            drawPath(androidPath, path.color, style = Stroke(width = path.width, cap = StrokeCap.Round, join = StrokeJoin.Round))
                        }
                    }
                    // Current path being drawn
                    if (currentPoints.size > 1) {
                        val androidPath = Path()
                        androidPath.moveTo(currentPoints.first().x, currentPoints.first().y)
                        currentPoints.drop(1).forEach { pt -> androidPath.lineTo(pt.x, pt.y) }
                        val drawColor = if (isEraser) Color.White else selectedColor
                        val drawWidth = if (isEraser) brushSize * 3 else brushSize
                        drawPath(androidPath, drawColor, style = Stroke(width = drawWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }
                }

                // Stamp overlay
                stampPositions.forEach { (pos, stamp) ->
                    Text(stamp, fontSize = 32.sp, modifier = Modifier.offset(pos.x.dp, pos.y.dp))
                }
            }

            // Right sidebar: stamps
            Column(
                modifier = Modifier.width(56.dp).fillMaxHeight().background(Color(0xFF1E293B)).padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Stamp", fontSize = 9.sp, color = Color(0xFF94A3B8), modifier = Modifier.fillMaxWidth())
                stamps.forEach { stamp ->
                    Text(stamp, fontSize = 26.sp,
                        modifier = Modifier.fillMaxWidth().clickable {
                            stampPositions = stampPositions + Pair(Offset(200f, 300f), stamp)
                        })
                }
            }
        }
    }
}
