package com.jujusworld.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
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
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// 15-second staged splash reveal
//
//  Stage 0 → 1  (0.3 s)  background fades in
//  Stage 1 → 2  (1.0 s)  unicorn pops in with spring
//  Stage 2 → 3  (0.8 s)  "JuJu's World" title appears
//  Stage 3 → 4  (0.7 s)  emoji ribbon appears
//  Stage 4 → 5  (0.7 s)  greeting from TTS / MP3 plays
//  Stage 5 → 6  (0.7 s)  "Welcome, JuJu!" big text
//  Stage 6 → 7  (0.7 s)  butterflies drift in
//  Stage 7 → 8  (0.7 s)  sparkle row
//  Stage 8 → 9  (0.7 s)  tagline
//  Stage 9 → 10 (7.1 s)  idle (letting greeting audio finish)
//  → navigate to HOME
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    var stage by remember { mutableIntStateOf(0) }

    // Animated values derived from stage
    val bgAlpha        by animateFloatAsState(if (stage >= 1) 1f else 0f, tween(800), label = "bg")
    val logoAlpha      by animateFloatAsState(if (stage >= 2) 1f else 0f, tween(900), label = "la")
    val logoScale      by animateFloatAsState(if (stage >= 2) 1f else 0.5f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium), label = "ls")
    val titleAlpha     by animateFloatAsState(if (stage >= 3) 1f else 0f, tween(700), label = "ta")
    val titleSlide     by animateFloatAsState(if (stage >= 3) 0f else 40f, spring(dampingRatio = 0.6f), label = "ts")
    val ribbonAlpha    by animateFloatAsState(if (stage >= 4) 1f else 0f, tween(600), label = "ra")
    val welcomeAlpha   by animateFloatAsState(if (stage >= 6) 1f else 0f, tween(700), label = "wa")
    val welcomeScale   by animateFloatAsState(if (stage >= 6) 1f else 1.4f, spring(dampingRatio = 0.55f), label = "ws")
    val butterAlpha    by animateFloatAsState(if (stage >= 7) 1f else 0f, tween(700), label = "ba")
    val sparkleAlpha   by animateFloatAsState(if (stage >= 8) 1f else 0f, tween(600), label = "spa")
    val tagAlpha       by animateFloatAsState(if (stage >= 9) 1f else 0f, tween(600), label = "tga")

    // Continuous pulse on the unicorn
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseSc by pulse.animateFloat(0.96f, 1.04f,
        infiniteRepeatable(tween(1100, easing = EaseInOutSine), RepeatMode.Reverse), label = "pv")

    // Sparkle twinkle
    val twinkle = rememberInfiniteTransition(label = "tw")
    val tw by twinkle.animateFloat(0.3f, 1f,
        infiniteRepeatable(tween(700, easing = EaseInOutSine), RepeatMode.Reverse), label = "tv")

    // Butterfly wing flap scale
    val wing = rememberInfiniteTransition(label = "wing")
    val wingX by wing.animateFloat(0.85f, 1f,
        infiniteRepeatable(tween(230, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "wv")

    // Stage sequencer — total ≈ 15 s
    LaunchedEffect(Unit) {
        delay(300);  stage = 1
        delay(1000); stage = 2
        delay(900);  stage = 3
        delay(800);  stage = 4
        delay(700);  SoundManager.speakGreeting(context); stage = 5
        delay(700);  stage = 6
        delay(700);  stage = 7
        delay(700);  stage = 8
        delay(700);  stage = 9
        delay(7100)  // let greeting audio finish
        navController.navigate(Routes.HOME) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2D1B69), Color(0xFF0F0C29), Color(0xFF1A0533))
                )
            )
            .alpha(bgAlpha),
        contentAlignment = Alignment.Center
    ) {
        // ── Background stars scattered around ──────────────────────────────
        repeat(28) { i ->
            val sx = ((i * 137.5f) % 100f)
            val sy = ((i * 83.7f)  % 100f)
            val sz = (6 + i % 10).sp
            Text("✦", fontSize = sz, color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .padding(start = (sx * 3.6f).dp, top = (sy * 6.2f).dp)
                    .alpha(tw * sparkleAlpha * (0.3f + (i % 4) * 0.15f)))
        }

        // ── Floating butterflies (background layer) ────────────────────────
        Text("🦋", fontSize = 28.sp,
            modifier = Modifier.align(Alignment.TopStart)
                .padding(start = 48.dp, top = 90.dp)
                .scale(scaleX = wingX, scaleY = 1f)
                .alpha(butterAlpha))
        Text("🦋", fontSize = 22.sp,
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(end = 60.dp, top = 130.dp)
                .scale(scaleX = 1f - (1f - wingX), scaleY = 1f)
                .alpha(butterAlpha * 0.8f))
        Text("🦋", fontSize = 20.sp,
            modifier = Modifier.align(Alignment.BottomStart)
                .padding(start = 80.dp, bottom = 100.dp)
                .scale(scaleX = wingX, scaleY = 1f)
                .alpha(butterAlpha * 0.7f))

        // ── Main content column ────────────────────────────────────────────
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Unicorn hero
            Text("🦄",
                fontSize = 96.sp,
                modifier = Modifier
                    .alpha(logoAlpha)
                    .scale(logoScale * pulseSc))

            Spacer(Modifier.height(8.dp))

            // "Welcome, JuJu!" — big entrance
            Text(
                text = "Welcome, JuJu! 💖",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFBBF24),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(welcomeAlpha)
                    .scale(welcomeScale)
            )

            Spacer(Modifier.height(4.dp))

            // Title
            Text(
                text = "JuJu's World",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .offset(y = titleSlide.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Emoji ribbon
            Text(
                text = "🦋  ✨  🌈  🌸  🦄  💫  ⭐",
                fontSize = 26.sp,
                modifier = Modifier.alpha(ribbonAlpha)
            )

            Spacer(Modifier.height(14.dp))

            // Sparkle row
            Text(
                text = "✨  ✨  ✨  ✨  ✨",
                fontSize = 20.sp,
                modifier = Modifier.alpha(sparkleAlpha * tw)
            )

            Spacer(Modifier.height(16.dp))

            // Tagline
            Text(
                text = "A magical world made just for you!",
                fontSize = 18.sp,
                color = Color(0xFFE9D5FF),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(tagAlpha)
            )

            Spacer(Modifier.height(28.dp))

            // Loading dots
            Text(
                text = "✦  ✦  ✦",
                fontSize = 14.sp,
                color = Color(0xFF6D28D9),
                modifier = Modifier.alpha(tagAlpha * tw)
            )
        }
    }
}
