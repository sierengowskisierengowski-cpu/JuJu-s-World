package com.jujusworld.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.jujusworld.utils.Prefs
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay

private val confetti = listOf("🎊","🎉","🌟","⭐","🦋","🌈","✨","💖","🎀","🌸")

/**
 * Full-screen win celebration overlay.
 * Show it by setting visible=true. It auto-dismisses after 3 seconds.
 * Also awards a star and plays the you_did_it sound.
 */
@Composable
fun WinCelebration(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val context = LocalContext.current
    val inf = rememberInfiniteTransition(label = "conf")

    // Big title entrance
    var titleScale by remember { mutableFloatStateOf(0.3f) }
    val animatedTitleScale by animateFloatAsState(
        titleScale, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium), label = "ts"
    )

    // Pulsing glow on title
    val glow by inf.animateFloat(0.85f, 1.15f,
        infiniteRepeatable(tween(500, easing = EaseInOutSine), RepeatMode.Reverse), label = "glow")

    // Star burst
    var starScale by remember { mutableFloatStateOf(0f) }
    val animatedStarScale by animateFloatAsState(
        starScale, spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessHigh), label = "ss"
    )

    // 20 confetti pieces with independent float positions
    val floats = (0 until 20).map { i ->
        inf.animateFloat(
            0f, 1f,
            infiniteRepeatable(
                tween(1800 + i * 120, easing = LinearEasing),
                RepeatMode.Restart,
                initialStartOffset = StartOffset(i * 90)
            ), label = "f$i"
        ).value
    }

    LaunchedEffect(visible) {
        if (!visible) return@LaunchedEffect
        SoundManager.playSuccess()
        SoundManager.speak("You did it! Great job JuJu!")
        Prefs.addStars(1)
        titleScale = 1f
        delay(300)
        starScale = 1f
        delay(3000)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
    ) {
        // Confetti rain
        floats.forEachIndexed { i, progress ->
            val x = ((i * 137.5f) % 100f)
            val emoji = confetti[i % confetti.size]
            val size = (18 + i % 16).sp
            Text(
                emoji, fontSize = size,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .padding(
                        start = (x * 3.5f).dp,
                        top = (progress * 700f).dp
                    )
                    .alpha(1f - progress * 0.6f)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Star burst
            Text("⭐", fontSize = 80.sp,
                modifier = Modifier.scale(animatedStarScale * glow))

            Spacer(Modifier.height(8.dp))

            // Main message
            Text(
                "YOU DID IT!! 🎉",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFBBF24),
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(animatedTitleScale * glow)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Amazing job, JuJu! 💖",
                fontSize = 28.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(animatedTitleScale)
            )
            Spacer(Modifier.height(12.dp))
            Text("✨ +1 Star Earned! ✨", fontSize = 22.sp, color = Color(0xFFFCD34D))
        }
    }
}
