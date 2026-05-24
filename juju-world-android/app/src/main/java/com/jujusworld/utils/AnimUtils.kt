package com.jujusworld.utils

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

/** Shared animation specs used across all JuJu screens. */

/** Infinite gentle floating up-and-down. Returns offset in dp. */
@Composable
fun rememberFloatAnim(periodMs: Int = 2400, amplitude: Float = 10f): Float {
    val inf = rememberInfiniteTransition(label = "float")
    return inf.animateFloat(
        0f, amplitude,
        infiniteRepeatable(tween(periodMs, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "fv"
    ).value
}

/** Infinite pulse scale (breathe in/out). */
@Composable
fun rememberPulseAnim(periodMs: Int = 900, lo: Float = 0.94f, hi: Float = 1.06f): Float {
    val inf = rememberInfiniteTransition(label = "pulse")
    return inf.animateFloat(
        lo, hi,
        infiniteRepeatable(tween(periodMs, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pv"
    ).value
}

/** Infinite spin (full 360°). Returns degrees. */
@Composable
fun rememberSpinAnim(periodMs: Int = 4000): Float {
    val inf = rememberInfiniteTransition(label = "spin")
    return inf.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(periodMs, easing = LinearEasing)),
        label = "sv"
    ).value
}

/** Butterfly wing-flap: alternates between 0.8 and 1.0 X scale quickly. */
@Composable
fun rememberWingFlapAnim(): Float {
    val inf = rememberInfiniteTransition(label = "wing")
    return inf.animateFloat(
        0.8f, 1.0f,
        infiniteRepeatable(tween(220, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "wv"
    ).value
}

/** Sparkle twinkle: opacity oscillation. */
@Composable
fun rememberTwinkleAnim(offsetMs: Int = 0): Float {
    val inf = rememberInfiniteTransition(label = "twinkle_$offsetMs")
    return inf.animateFloat(
        0.2f, 1f,
        infiniteRepeatable(
            tween(800 + offsetMs % 600, easing = EaseInOutSine),
            RepeatMode.Reverse,
            initialStartOffset = StartOffset(offsetMs)
        ),
        label = "tv"
    ).value
}
