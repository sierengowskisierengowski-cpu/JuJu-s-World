package com.jujusworld.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.jujusworld.navigation.Routes
import com.jujusworld.utils.Prefs
import com.jujusworld.utils.SoundManager
import java.text.SimpleDateFormat
import java.util.*

data class NavTile(
    val emoji: String,
    val title: String,
    val route: String,
    val gradStart: Color,
    val gradEnd: Color
)

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val hour = remember { calendar.get(Calendar.HOUR_OF_DAY) }

    // Time-aware sky
    val (bgStart, bgMid, bgEnd, greeting) = when {
        hour in 5..11  -> Quad(Color(0xFFFFF9C3), Color(0xFFFDE68A), Color(0xFFFCD34D), "Good Morning, JuJu! ☀️")
        hour in 12..16 -> Quad(Color(0xFFBAE6FD), Color(0xFF7DD3FC), Color(0xFF38BDF8), "Good Afternoon, JuJu! 🌤")
        hour in 17..20 -> Quad(Color(0xFFE879F9), Color(0xFFA855F7), Color(0xFF6D28D9), "Good Evening, JuJu! 🌅")
        else           -> Quad(Color(0xFF1E1B4B), Color(0xFF0F0C29), Color(0xFF030712), "Goodnight, JuJu! 🌙")
    }
    val isDark = hour !in 5..20

    // Time display
    var timeStr by remember { mutableStateOf(SimpleDateFormat("h:mm a", Locale.US).format(Date())) }
    LaunchedEffect(Unit) {
        while (true) {
            timeStr = SimpleDateFormat("h:mm a", Locale.US).format(Date())
            kotlinx.coroutines.delay(30_000)
        }
    }

    // All 10 navigation tiles (9 sections + parent)
    val tiles = listOf(
        NavTile("📺", "JuJu's Shows",   Routes.SHOWS,    Color(0xFFFF6B9D), Color(0xFF9B59B6)),
        NavTile("🎮", "Games",          Routes.GAMES,    Color(0xFF4ECDC4), Color(0xFF2ECC71)),
        NavTile("📚", "Books",          Routes.BOOKS,    Color(0xFF45B7D1), Color(0xFF2980B9)),
        NavTile("🎵", "Music",          Routes.MUSIC,    Color(0xFFFF9A56), Color(0xFFF97316)),
        NavTile("🎨", "Art Studio",     Routes.ART,      Color(0xFFA8E6CF), Color(0xFF10B981)),
        NavTile("📷", "Camera",         Routes.CAMERA,   Color(0xFFB3A0FF), Color(0xFF7C3AED)),
        NavTile("🌐", "Browser",        Routes.BROWSER,  Color(0xFF74B9FF), Color(0xFF3B82F6)),
        NavTile("🏪", "App Store",      Routes.APPSTORE, Color(0xFFFFD700), Color(0xFFF59E0B)),
        NavTile("🌙", "Sleep",          Routes.SLEEP,    Color(0xFF4F46E5), Color(0xFF1E1B4B)),
        NavTile("🔒", "Parent",         Routes.PARENT,   Color(0xFF6B7280), Color(0xFF374151)),
    )

    val stars = Prefs.stars

    // Continuous floating animation for decorations
    val inf = rememberInfiniteTransition(label = "float")
    val floatY by inf.animateFloat(0f, -14f,
        infiniteRepeatable(tween(2400, easing = EaseInOutSine), RepeatMode.Reverse), label = "fy")
    val floatY2 by inf.animateFloat(0f, -10f,
        infiniteRepeatable(tween(3100, easing = EaseInOutSine), RepeatMode.Reverse), label = "fy2")

    // Wing flap for butterflies
    val wingX by inf.animateFloat(0.8f, 1f,
        infiniteRepeatable(tween(230, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "wx")

    // Sparkle twinkle
    val tw by inf.animateFloat(0.3f, 1f,
        infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse), label = "tw")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgStart, bgMid, bgEnd)))
    ) {
        // ── Decorative background elements ─────────────────────────────────
        Text("🦋", fontSize = 34.sp,
            modifier = Modifier.padding(start = 18.dp, top = (55 + floatY).dp)
                .scale(scaleX = wingX, scaleY = 1f))
        Text("✨", fontSize = 20.sp,
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(end = 28.dp, top = (70 + floatY2).dp)
                .scale(tw))
        Text("🌸", fontSize = 26.sp,
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(end = 80.dp, top = (110 + floatY).dp))
        Text("⭐", fontSize = 18.sp,
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = (40 + floatY2).dp)
                .scale(tw * 0.8f + 0.2f))
        Text("💫", fontSize = 22.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = (90 + floatY).dp))
        Text("🦋", fontSize = 24.sp,
            modifier = Modifier.align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = (80 + floatY2).dp)
                .scale(scaleX = 1f - (1f - wingX), scaleY = 1f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header bar ──────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "JuJu's World 🌈",
                        fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF1E1B4B)
                    )
                    Text(
                        greeting,
                        fontSize = 14.sp,
                        color = if (isDark) Color(0xFFE9D5FF) else Color(0xFF4C1D95)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        timeStr,
                        fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF4C1D95)
                    )
                    Text(
                        "⭐ $stars stars",
                        fontSize = 14.sp, color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── Floating character ─────────────────────────────────────────
            Text(
                "🦄",
                fontSize = 72.sp,
                modifier = Modifier.offset(y = floatY.dp).padding(vertical = 4.dp)
            )

            // ── Nav grid ───────────────────────────────────────────────────
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement   = Arrangement.spacedBy(10.dp)
            ) {
                items(tiles) { tile ->
                    JujuNavTile(tile) {
                        SoundManager.playTap()
                        SoundManager.speak(tile.title)
                        navController.navigate(tile.route)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun JujuNavTile(tile: NavTile, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.90f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "sc"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(tile.gradStart, tile.gradEnd)))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(6.dp)
        ) {
            Text(tile.emoji, fontSize = 32.sp)
            Spacer(Modifier.height(3.dp))
            Text(
                text = tile.title,
                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color.White, textAlign = TextAlign.Center,
                maxLines = 2, lineHeight = 14.sp
            )
        }
    }
}

/** Helper to destructure 4-tuples without a data class. */
private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
private operator fun <A, B, C, D> Quad<A, B, C, D>.component1() = first
private operator fun <A, B, C, D> Quad<A, B, C, D>.component2() = second
private operator fun <A, B, C, D> Quad<A, B, C, D>.component3() = third
private operator fun <A, B, C, D> Quad<A, B, C, D>.component4() = fourth
