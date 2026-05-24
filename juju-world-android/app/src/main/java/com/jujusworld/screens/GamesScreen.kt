package com.jujusworld.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.navigation.Routes
import com.jujusworld.utils.SoundManager

data class GameTile(
    val emoji: String, val title: String, val route: String,
    val difficulty: Int, val g1: Color, val g2: Color
)

@Composable
fun GamesScreen(navController: NavController) {
    val inf = rememberInfiniteTransition(label = "games")
    val glow1 by inf.animateFloat(0.3f, 1f, infiniteRepeatable(tween(800, easing = EaseInOutSine), RepeatMode.Reverse), label = "g1")
    val glow2 by inf.animateFloat(0.6f, 1f, infiniteRepeatable(tween(600, easing = EaseInOutSine), RepeatMode.Reverse, StartOffset(300)), label = "g2")
    val confY1 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart), label = "cy1")
    val confY2 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(4200, easing = LinearEasing), RepeatMode.Restart, StartOffset(1200)), label = "cy2")

    val games = listOf(
        GameTile("🔤","Letters",      Routes.LETTERS,      1, Color(0xFF8B5CF6), Color(0xFF6366F1)),
        GameTile("🔢","Counting",     Routes.COUNTING,     1, Color(0xFFEC4899), Color(0xFFDB2777)),
        GameTile("🎨","Colors",       Routes.COLORS,       1, Color(0xFFF59E0B), Color(0xFFF97316)),
        GameTile("⬡","Shapes",       Routes.SHAPES,       1, Color(0xFF10B981), Color(0xFF059669)),
        GameTile("🫧","Bubble Pop",   Routes.BUBBLE_POP,   1, Color(0xFF06B6D4), Color(0xFF0284C7)),
        GameTile("🐮","Animal Sounds",Routes.ANIMAL_SOUNDS,1, Color(0xFF84CC16), Color(0xFF4D7C0F)),
        GameTile("🧩","Puzzle",       Routes.PUZZLE,       2, Color(0xFFF43F5E), Color(0xFFBE123C)),
        GameTile("🔨","Whack-a-Mole", Routes.WHACK_MOLE,  2, Color(0xFF8B5CF6), Color(0xFF5B21B6)),
        GameTile("🎈","Balloon Pop",  Routes.BALLOON_POP,  2, Color(0xFFFB923C), Color(0xFFEA580C)),
        GameTile("🎹","Piano",        Routes.PIANO,        2, Color(0xFF6366F1), Color(0xFF4338CA)),
        GameTile("💃","Dance Party",  Routes.DANCE_PARTY,  2, Color(0xFFEC4899), Color(0xFFA21CAF)),
        GameTile("👗","Dress Up",     Routes.DRESS_UP,     3, Color(0xFFF59E0B), Color(0xFFD97706)),
        GameTile("🐟","Feed Animal",  Routes.FEED_ANIMAL,  3, Color(0xFF14B8A6), Color(0xFF0D9488)),
        GameTile("🎤","Nursery Rhyme",Routes.NURSERY_RHYME,1, Color(0xFF818CF8), Color(0xFF6366F1)),
    )

    val tileFloats = games.mapIndexed { i, _ ->
        inf.animateFloat(0f, -5f,
            infiniteRepeatable(tween(1900 + i * 130, easing = EaseInOutSine), RepeatMode.Reverse,
                StartOffset(i * 190)), label = "tf$i").value
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF030712), Color(0xFF0F0524), Color(0xFF1A0A2E)))
        ))
        // Neon glow dots
        listOf(
            Triple(10f, 20f, Color(0xFFEC4899)), Triple(30f, 40f, Color(0xFF06B6D4)),
            Triple(60f, 15f, Color(0xFF8B5CF6)), Triple(80f, 35f, Color(0xFFF59E0B)),
            Triple(50f, 55f, Color(0xFF10B981)), Triple(20f, 70f, Color(0xFFF43F5E)),
            Triple(70f, 65f, Color(0xFF6366F1)), Triple(90f, 25f, Color(0xFF34D399)),
        ).forEachIndexed { i, (x, y, col) ->
            val a = if (i % 2 == 0) glow1 else glow2
            Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                .padding(start = (x * 3.6f).dp, top = (y * 6f).dp)
                .size(8.dp).clip(RoundedCornerShape(4.dp))
                .background(col.copy(alpha = a * 0.7f)))
        }
        // Confetti
        listOf("🎊","🎉","🌟","⭐").forEachIndexed { i, e ->
            Text(e, fontSize = 18.sp,
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                    .padding(start = (i * 90f + 30).dp, top = (confY1 * 700).dp)
                    .alpha(1f - confY1 * 0.5f))
            Text(e, fontSize = 14.sp,
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                    .padding(start = (i * 80f + 60).dp, top = (confY2 * 700).dp)
                    .alpha(1f - confY2 * 0.5f))
        }

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Column {
                    Text("🎮 Games", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Pick your adventure!", fontSize = 14.sp, color = Color(0xFF818CF8))
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp),
                contentPadding        = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(games) { i, game ->
                    GameCard(game, tileFloats.getOrElse(i) { 0f }) {
                        SoundManager.playTap()
                        navController.navigate(game.route)
                    }
                }
            }
        }
    }
}

@Composable
fun GameCard(game: GameTile, floatDp: Float, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.88f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "gsc"
    )
    Box(
        modifier = Modifier
            .aspectRatio(0.88f).offset(y = floatDp.dp).scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(game.g1, game.g2),
                start = Offset(0f, 0f), end = Offset(200f, 200f)))
            .border(1.dp, game.g1.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(game.emoji, fontSize = 42.sp, modifier = Modifier.shadow(4.dp))
            Text(game.title, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color.White, textAlign = TextAlign.Center, maxLines = 2, lineHeight = 13.sp)
            Text("⭐".repeat(game.difficulty), fontSize = 9.sp)
        }
    }
}
