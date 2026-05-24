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
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.navigation.Routes
import com.jujusworld.utils.Prefs
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

data class NavTile(
    val emoji: String, val title: String, val route: String,
    val g1: Color, val g2: Color, val g3: Color
)

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }

    // Time display
    var timeStr by remember { mutableStateOf(SimpleDateFormat("h:mm a", Locale.US).format(Date())) }
    LaunchedEffect(Unit) {
        while (true) {
            timeStr = SimpleDateFormat("h:mm a", Locale.US).format(Date())
            delay(30_000)
        }
    }

    // Hidden parent tap zone — 5 taps within 3 seconds
    var tapTimes by remember { mutableStateOf(listOf<Long>()) }
    fun recordHiddenTap() {
        val now = System.currentTimeMillis()
        val recent = tapTimes.filter { now - it < 3000 } + now
        tapTimes = recent
        if (recent.size >= 5) {
            tapTimes = emptyList()
            navController.navigate(Routes.PARENT)
        }
    }

    val inf = rememberInfiniteTransition(label = "home")

    // ── Background life ──────────────────────────────────────────────────────
    // Stars twinkling — 3 different rates
    val tw1 by inf.animateFloat(0.2f, 1f, infiniteRepeatable(tween(700, easing = EaseInOutSine), RepeatMode.Reverse), label = "tw1")
    val tw2 by inf.animateFloat(0.5f, 1f, infiniteRepeatable(tween(1100, easing = EaseInOutSine), RepeatMode.Reverse, StartOffset(350)), label = "tw2")
    val tw3 by inf.animateFloat(0.1f, 0.9f, infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse, StartOffset(200)), label = "tw3")
    // Fireflies drifting up
    val ff1 by inf.animateFloat(1f, 0f, infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart), label = "ff1")
    val ff2 by inf.animateFloat(1f, 0f, infiniteRepeatable(tween(4100, easing = LinearEasing), RepeatMode.Restart, StartOffset(1500)), label = "ff2")
    val ff3 by inf.animateFloat(1f, 0f, infiniteRepeatable(tween(3700, easing = LinearEasing), RepeatMode.Restart, StartOffset(800)), label = "ff3")
    // Cloud drift
    val cloud1 by inf.animateFloat(-0.2f, 1.1f, infiniteRepeatable(tween(18000, easing = LinearEasing), RepeatMode.Restart), label = "cl1")
    val cloud2 by inf.animateFloat(-0.2f, 1.1f, infiniteRepeatable(tween(24000, easing = LinearEasing), RepeatMode.Restart, StartOffset(9000)), label = "cl2")
    // Flower sway
    val sway by inf.animateFloat(-6f, 6f, infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse), label = "sway")

    // ── Owl mascot animations ────────────────────────────────────────────────
    val owlBob by inf.animateFloat(0f, -12f, infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse), label = "owlbob")
    val owlSway by inf.animateFloat(-4f, 4f, infiniteRepeatable(tween(3400, easing = EaseInOutSine), RepeatMode.Reverse), label = "owlsway")
    val wingFlap by inf.animateFloat(0.85f, 1f, infiniteRepeatable(tween(220, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "wf")
    // Butterfly orbit around owl
    val btflyAngle by inf.animateFloat(0f, 360f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "bta")
    // Sun pulse (morning/afternoon)
    val sunPulse by inf.animateFloat(0.92f, 1.08f, infiniteRepeatable(tween(1400, easing = EaseInOutSine), RepeatMode.Reverse), label = "sp")
    // ZZZ float (bedtime)
    val zzz1 by inf.animateFloat(1f, 0f, infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart), label = "z1")
    val zzz2 by inf.animateFloat(1f, 0f, infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart, StartOffset(700)), label = "z2")
    val zzz3 by inf.animateFloat(1f, 0f, infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart, StartOffset(1400)), label = "z3")
    // Yawn (occasional every ~35 sec) — just a scale pop on the owl
    var yawning by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (hour in 17..20 || hour !in 6..20) {
            while (true) {
                delay((30_000..45_000).random().toLong())
                yawning = true
                delay(1200)
                yawning = false
            }
        }
    }
    val yawnScale by animateFloatAsState(if (yawning) 1.18f else 1f, spring(dampingRatio = 0.4f), label = "yn")
    // Eye open animation (morning)
    val eyeAlpha by inf.animateFloat(0.3f, 1f, infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse), label = "ea")

    val owlMood = when (hour) {
        in 6..10  -> "morning"
        in 11..16 -> "afternoon"
        in 17..20 -> "evening"
        else      -> "bedtime"
    }
    val (owlGreeting, owlAccent) = when (owlMood) {
        "morning"   -> "Good Morning JuJu! ☀️" to Color(0xFFFCD34D)
        "afternoon" -> "Hi JuJu! Ready to play? 🌈" to Color(0xFF34D399)
        "evening"   -> "Good Evening JuJu! 🌅" to Color(0xFFFB923C)
        else        -> "Goodnight JuJu! 🌙" to Color(0xFF818CF8)
    }

    // ── Tiles ────────────────────────────────────────────────────────────────
    val tiles = listOf(
        NavTile("📺","Shows",    Routes.SHOWS,    Color(0xFFFF6B9D), Color(0xFFEC4899), Color(0xFF9B59B6)),
        NavTile("🎮","Games",    Routes.GAMES,    Color(0xFF34D399), Color(0xFF14B8A6), Color(0xFF0EA5E9)),
        NavTile("📚","Books",    Routes.BOOKS,    Color(0xFF60A5FA), Color(0xFF3B82F6), Color(0xFF4F46E5)),
        NavTile("🎵","Music",    Routes.MUSIC,    Color(0xFFFBBF24), Color(0xFFF97316), Color(0xFFEF4444)),
        NavTile("🎨","Art",      Routes.ART,      Color(0xFF6EE7B7), Color(0xFF34D399), Color(0xFF10B981)),
        NavTile("📷","Camera",   Routes.CAMERA,   Color(0xFFC4B5FD), Color(0xFFA78BFA), Color(0xFF7C3AED)),
        NavTile("🌐","Browser",  Routes.BROWSER,  Color(0xFF7DD3FC), Color(0xFF38BDF8), Color(0xFF0284C7)),
        NavTile("🌙","Sleep",    Routes.SLEEP,    Color(0xFF818CF8), Color(0xFF6366F1), Color(0xFF1E1B4B)),
        NavTile("🛍️","App Store",Routes.APPSTORE, Color(0xFFFDE68A), Color(0xFFFBBF24), Color(0xFFF59E0B)),
    )
    // Each tile gets its own float phase
    val tileFloats = tiles.mapIndexed { i, _ ->
        inf.animateFloat(0f, -5f,
            infiniteRepeatable(tween(1800 + i * 180, easing = EaseInOutSine), RepeatMode.Reverse,
                StartOffset(i * 220)), label = "tf$i").value
    }

    val stars = Prefs.stars

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Enchanted meadow background ──────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Sky gradient
            drawRect(Brush.verticalGradient(
                listOf(Color(0xFF0D0628), Color(0xFF1A0A3D), Color(0xFF2D1260), Color(0xFF4A1C6E), Color(0xFF7B3FA0), Color(0xFFC06090)),
                startY = 0f, endY = size.height * 0.82f
            ))
            // Horizon pink glow
            drawRect(Brush.verticalGradient(
                listOf(Color.Transparent, Color(0x55FF9DC2), Color(0xAAFFB3CC)),
                startY = size.height * 0.65f, endY = size.height
            ))
            // Ground hint
            drawRect(Color(0xFF0A1F0A), topLeft = Offset(0f, size.height * 0.88f),
                size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.12f))
        }

        // Stars layer — 60 stars with 3 twinkle groups
        for (i in 0 until 60) {
            val sx = (i * 97.3f + 13f) % 100f
            val sy = (i * 53.7f + 7f)  % 75f
            val sz = (5 + i % 8).sp
            val alpha = when (i % 3) { 0 -> tw1; 1 -> tw2; else -> tw3 }
            Text("✦", fontSize = sz, color = Color.White,
                modifier = Modifier
                    .fillMaxSize().wrapContentSize(Alignment.TopStart)
                    .padding(start = (sx * 3.6f).dp, top = (sy * 5.8f).dp)
                    .alpha(alpha * 0.7f))
        }

        // Fireflies drifting up
        listOf(ff1 to Pair(15f, 60f), ff2 to Pair(45f, 70f), ff3 to Pair(75f, 65f))
            .forEach { (progress, pos) ->
                Text("✨", fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxSize().wrapContentSize(Alignment.TopStart)
                        .padding(start = (pos.first * 3.6f).dp, top = (progress * 500 + 100f).dp)
                        .alpha((1f - progress) * 0.9f))
            }

        // Clouds
        Text("☁️", fontSize = 48.sp,
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                .padding(start = (cloud1 * 400 - 60).dp, top = 60.dp).alpha(0.25f))
        Text("☁️", fontSize = 36.sp,
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                .padding(start = (cloud2 * 400 - 60).dp, top = 100.dp).alpha(0.2f))

        // Ground flowers swaying
        Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("🌸","🌼","🌺","🌸","🌼","🌺","🌸","🌼","🌺","🌸","🌼","🌺").forEach {
                Text(it, fontSize = 20.sp, modifier = Modifier.rotate(sway * (if (it == "🌼") 1f else -1f)))
            }
        }

        // ── Main layout ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Top bar ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clock — hidden 5-tap parent zone behind it
                Box(modifier = Modifier.size(width = 100.dp, height = 60.dp)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        recordHiddenTap()
                    }) {
                    Column {
                        Text(timeStr, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE68A))
                        Text("JuJu's World 🌈", fontSize = 11.sp, color = Color(0xFFE9D5FF))
                    }
                }
                // Star counter
                Box(modifier = Modifier
                    .background(Color(0x44000000), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("⭐ × $stars", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24))
                }
            }

            // ── Owl mascot area (top ~35%) ────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth().weight(0.38f),
                contentAlignment = Alignment.Center
            ) {
                // Background glow / accent behind owl
                when (owlMood) {
                    "morning", "afternoon" -> {
                        Text("☀️", fontSize = (72 * sunPulse).sp,
                            modifier = Modifier.align(Alignment.Center).offset(x = 0.dp, y = 16.dp).alpha(0.5f))
                    }
                    "evening" -> {
                        Text("🌅", fontSize = 80.sp,
                            modifier = Modifier.align(Alignment.Center).offset(y = 20.dp).alpha(0.4f))
                    }
                    "bedtime" -> {
                        Text("🌙", fontSize = 64.sp,
                            modifier = Modifier.align(Alignment.Center).offset(x = 30.dp, y = (-20).dp).alpha(0.6f))
                    }
                }

                // ZZZ float up (bedtime)
                if (owlMood == "bedtime") {
                    Text("z", fontSize = 18.sp, color = Color(0xFFE9D5FF),
                        modifier = Modifier.align(Alignment.Center).offset(x = 50.dp, y = (zzz1 * -80 - 20).dp).alpha(1f - zzz1))
                    Text("z", fontSize = 14.sp, color = Color(0xFFC4B5FD),
                        modifier = Modifier.align(Alignment.Center).offset(x = 62.dp, y = (zzz2 * -70 - 10).dp).alpha(1f - zzz2))
                    Text("z", fontSize = 11.sp, color = Color(0xFFA78BFA),
                        modifier = Modifier.align(Alignment.Center).offset(x = 72.dp, y = (zzz3 * -60).dp).alpha(1f - zzz3))
                }

                // Orbiting butterfly (afternoon) / firefly (evening)
                if (owlMood == "afternoon" || owlMood == "evening") {
                    val rad = Math.toRadians(btflyAngle.toDouble())
                    val ox = (Math.cos(rad) * 70).toFloat()
                    val oy = (Math.sin(rad) * 30).toFloat()
                    Text(if (owlMood == "afternoon") "🦋" else "✨", fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Center).offset(x = ox.dp, y = oy.dp)
                            .scale(scaleX = wingFlap, scaleY = 1f))
                }

                // Owl body — big + animated
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = owlBob.dp).rotate(owlSway)) {
                    Text("🦉",
                        fontSize = (if (yawning) 116 else 108).sp,
                        modifier = Modifier.scale(yawnScale))

                    Spacer(Modifier.height(4.dp))

                    // Greeting text
                    Text(owlGreeting,
                        fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = owlAccent, textAlign = TextAlign.Center,
                        modifier = Modifier.shadow(4.dp))
                }
            }

            // ── Nav tile grid ─────────────────────────────────────────────
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.62f)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp),
                contentPadding        = PaddingValues(bottom = 12.dp)
            ) {
                itemsIndexed(tiles) { i, tile ->
                    JujuNavTile(tile, tileFloats.getOrElse(i) { 0f }) {
                        SoundManager.playTap()
                        SoundManager.speak(tile.title)
                        navController.navigate(tile.route)
                    }
                }
            }
        }
    }
}

@Composable
fun JujuNavTile(tile: NavTile, floatOffsetDp: Float, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.88f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "tsc"
    )

    Box(
        modifier = Modifier
            .aspectRatio(0.9f)
            .offset(y = floatOffsetDp.dp)
            .scale(scale)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(tile.g1, tile.g2, tile.g3),
                    start = Offset(0f, 0f), end = Offset(300f, 300f)
                )
            )
            .border(1.dp,
                Brush.linearGradient(listOf(tile.g1.copy(alpha = 0.9f), Color.White.copy(alpha = 0.3f))),
                RoundedCornerShape(28.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            Text(tile.emoji, fontSize = 52.sp,
                modifier = Modifier.shadow(6.dp))
            Spacer(Modifier.height(4.dp))
            Text(tile.title, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                color = Color.White, textAlign = TextAlign.Center, maxLines = 2,
                modifier = Modifier.shadow(2.dp))
        }
    }
}
