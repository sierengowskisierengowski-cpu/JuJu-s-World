package com.jujusworld.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.Prefs
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay

data class ColorOption(val name: String, val color: Color, val emoji: String)

@Composable
fun ColorsGame(navController: NavController) {
    val context = LocalContext.current

    val allColors = listOf(
        ColorOption("Red",    Color(0xFFEF4444), "🔴"),
        ColorOption("Orange", Color(0xFFF97316), "🟠"),
        ColorOption("Yellow", Color(0xFFFBBF24), "🟡"),
        ColorOption("Green",  Color(0xFF22C55E), "🟢"),
        ColorOption("Blue",   Color(0xFF3B82F6), "🔵"),
        ColorOption("Purple", Color(0xFF8B5CF6), "🟣"),
        ColorOption("Pink",   Color(0xFFEC4899), "🌸"),
        ColorOption("Brown",  Color(0xFF92400E), "🟤"),
        ColorOption("White",  Color(0xFFFFFFFF), "⬜"),
        ColorOption("Black",  Color(0xFF1F2937), "⬛"),
    )

    var target by remember { mutableStateOf(allColors.random()) }
    var choices by remember { mutableStateOf(generateColorChoices(target, allColors)) }
    var result by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(0) }

    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseSc by pulse.animateFloat(0.95f, 1.05f,
        infiniteRepeatable(tween(800, easing = EaseInOutSine), RepeatMode.Reverse), label = "ps")

    fun nextRound() {
        target = allColors.random()
        choices = generateColorChoices(target, allColors)
        result = ""
    }

    LaunchedEffect(result) {
        if (result == "correct") { delay(1500); nextRound() }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF7C2D12), Color(0xFF1E1B4B)))
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
                Text("🌈  Colors Game", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.weight(1f))
                Text("⭐ $score", fontSize = 20.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
            }

            Text("What color is this?", fontSize = 20.sp, color = Color(0xFFFED7AA))
            Spacer(Modifier.height(20.dp))

            // Color swatch
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(if (result == "correct") pulseSc else 1f)
                    .clip(CircleShape)
                    .background(target.color)
                    .border(6.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (result == "correct") Text("✓", fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            when (result) {
                "correct" -> Text("🎉 ${target.name}! You're right!", fontSize = 24.sp,
                    fontWeight = FontWeight.Bold, color = Color(0xFF34D399), textAlign = TextAlign.Center)
                "wrong"   -> Text("Hmm, try again! 🌈", fontSize = 20.sp,
                    color = Color(0xFFFBBF24), textAlign = TextAlign.Center)
                else      -> Text("Tap the right color name! 👇", fontSize = 18.sp, color = Color(0xFFFED7AA))
            }

            Spacer(Modifier.height(20.dp))

            // Choice buttons (2x2)
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
                                        else choice.color.copy(alpha = 0.85f)
                                    )
                                    .border(
                                        if (isTarget && result.isNotEmpty()) 4.dp else 2.dp,
                                        if (isTarget && result.isNotEmpty()) Color.White else Color.White.copy(0.3f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable(enabled = result.isEmpty()) {
                                        if (choice.name == target.name) {
                                            result = "correct"; score++; Prefs.addStars()
                                            SoundManager.speak("${target.name}! Correct!")
                                        } else {
                                            result = "wrong"
                                            SoundManager.speak("Try again!")
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(choice.emoji, fontSize = 24.sp)
                                    Text(choice.name, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                        color = if (choice.name == "White") Color.Black else Color.White)
                                }
                            }
                        }
                    }
                }
            }

            if (result == "wrong") {
                Spacer(Modifier.height(16.dp))
                Button(onClick = { result = "" },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))) {
                    Text("Try Again 💪", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

fun generateColorChoices(target: ColorOption, all: List<ColorOption>): List<ColorOption> {
    val wrong = all.filter { it.name != target.name }.shuffled().take(3)
    return (wrong + target).shuffled()
}
