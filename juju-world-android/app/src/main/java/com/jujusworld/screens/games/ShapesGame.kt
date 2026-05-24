package com.jujusworld.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.Prefs
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay

data class ShapeOption(val name: String, val emoji: String)

@Composable
fun ShapesGame(navController: NavController) {
    val allShapes = listOf(
        ShapeOption("Circle",    "⭕"),
        ShapeOption("Square",    "🟦"),
        ShapeOption("Triangle",  "🔺"),
        ShapeOption("Star",      "⭐"),
        ShapeOption("Heart",     "❤️"),
        ShapeOption("Diamond",   "💎"),
        ShapeOption("Rectangle", "▬"),
        ShapeOption("Oval",      "🥚"),
    )

    var target by remember { mutableStateOf(allShapes.random()) }
    var choices by remember { mutableStateOf(generateShapeChoices(target, allShapes)) }
    var result by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(0) }

    val celebScale by animateFloatAsState(
        if (result == "correct") 1.2f else 1f,
        spring(dampingRatio = 0.4f), label = "cs"
    )

    fun nextRound() {
        target = allShapes.random()
        choices = generateShapeChoices(target, allShapes)
        result = ""
    }

    LaunchedEffect(result) {
        if (result == "correct") { delay(1500); nextRound() }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(Color(0xFF312E81), Color(0xFF1E1B4B))
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("⭐  Shapes Game", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.weight(1f))
                Text("⭐ $score", fontSize = 20.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
            }

            Text("What shape is this?", fontSize = 20.sp, color = Color(0xFFE9D5FF))
            Spacer(Modifier.height(16.dp))

            // Shape canvas
            Box(
                modifier = Modifier.size(160.dp).scale(celebScale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2D1B69))
                    .border(3.dp, Color(0xFF8B5CF6), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                ShapeDrawing(shapeName = target.name, color = Color(0xFFEC4899))
            }

            Spacer(Modifier.height(12.dp))

            when (result) {
                "correct" -> Text("🎉 ${target.name}! You're brilliant!", fontSize = 22.sp,
                    fontWeight = FontWeight.Bold, color = Color(0xFF34D399), textAlign = TextAlign.Center)
                "wrong"   -> Text("Not quite — look carefully! 👀", fontSize = 20.sp,
                    color = Color(0xFFFBBF24), textAlign = TextAlign.Center)
                else      -> Text("Tap the shape name! 👇", fontSize = 18.sp, color = Color(0xFFA78BFA))
            }

            Spacer(Modifier.height(20.dp))

            // Choice grid 2x2
            val rows = choices.chunked(2)
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { choice ->
                            val isTarget = choice.name == target.name
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (result == "correct" && isTarget) Color(0xFF059669)
                                        else Color(0xFF4F46E5)
                                    )
                                    .border(
                                        if (isTarget && result.isNotEmpty()) 3.dp else 1.dp,
                                        if (isTarget && result.isNotEmpty()) Color.White else Color.White.copy(0.2f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable(enabled = result.isEmpty()) {
                                        if (choice.name == target.name) {
                                            result = "correct"; score++; Prefs.addStars()
                                            SoundManager.speak("${target.name}! That's right!")
                                        } else {
                                            result = "wrong"
                                            SoundManager.speak("Try again!")
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(choice.emoji, fontSize = 26.sp)
                                    Text(choice.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            if (result == "wrong") {
                Spacer(Modifier.height(16.dp))
                Button(onClick = { result = "" },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))) {
                    Text("Try Again 💪", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ShapeDrawing(shapeName: String, color: Color) {
    Canvas(modifier = Modifier.size(120.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2; val cy = h / 2
        val strokeW = 8f
        val style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)

        when (shapeName) {
            "Circle" -> drawCircle(color, radius = w * 0.38f, style = style)
            "Square" -> {
                val s = w * 0.65f
                drawRect(color, topLeft = Offset(cx - s/2, cy - s/2), size = Size(s, s), style = style)
            }
            "Triangle" -> {
                val path = Path().apply {
                    moveTo(cx, cy - h * 0.38f)
                    lineTo(cx + w * 0.38f, cy + h * 0.32f)
                    lineTo(cx - w * 0.38f, cy + h * 0.32f)
                    close()
                }
                drawPath(path, color, style = style)
            }
            "Star" -> {
                val pts = 5; val outer = w * 0.4f; val inner = w * 0.18f
                val path = Path()
                for (i in 0 until pts * 2) {
                    val r = if (i % 2 == 0) outer else inner
                    val angle = (Math.PI * i / pts - Math.PI / 2).toFloat()
                    val x = cx + r * kotlin.math.cos(angle)
                    val y = cy + r * kotlin.math.sin(angle)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, color, style = style)
            }
            "Heart" -> {
                val path = Path().apply {
                    moveTo(cx, cy + h * 0.3f)
                    cubicTo(cx - w * 0.5f, cy, cx - w * 0.5f, cy - h * 0.35f, cx, cy - h * 0.1f)
                    cubicTo(cx + w * 0.5f, cy - h * 0.35f, cx + w * 0.5f, cy, cx, cy + h * 0.3f)
                }
                drawPath(path, color, style = style)
            }
            "Diamond" -> {
                val path = Path().apply {
                    moveTo(cx, cy - h * 0.4f)
                    lineTo(cx + w * 0.35f, cy)
                    lineTo(cx, cy + h * 0.4f)
                    lineTo(cx - w * 0.35f, cy)
                    close()
                }
                drawPath(path, color, style = style)
            }
            "Rectangle" -> {
                drawRect(color, topLeft = Offset(cx - w * 0.42f, cy - h * 0.22f),
                    size = Size(w * 0.84f, h * 0.44f), style = style)
            }
            "Oval" -> drawOval(color, topLeft = Offset(cx - w * 0.42f, cy - h * 0.28f),
                size = Size(w * 0.84f, h * 0.56f), style = style)
        }
    }
}

fun generateShapeChoices(target: ShapeOption, all: List<ShapeOption>): List<ShapeOption> {
    val wrong = all.filter { it.name != target.name }.shuffled().take(3)
    return (wrong + target).shuffled()
}
