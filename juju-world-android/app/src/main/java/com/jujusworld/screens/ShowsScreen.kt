package com.jujusworld.screens

import android.annotation.SuppressLint
import android.webkit.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.jujusworld.utils.SoundManager

data class Channel(val emoji: String, val name: String, val url: String, val g1: Color, val g2: Color)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ShowsScreen(navController: NavController) {
    val channels = listOf(
        Channel("🎵","Cocomelon",  "https://www.youtube.com/c/Cocomelon",    Color(0xFFFF6B9D), Color(0xFF9B59B6)),
        Channel("👩‍🏫","Ms. Rachel","https://www.youtube.com/c/SongsforLittles",Color(0xFFF59E0B),Color(0xFFF97316)),
        Channel("🔵","Blippi",     "https://www.youtube.com/c/Blippi",       Color(0xFF0EA5E9), Color(0xFF0284C7)),
        Channel("🐕","Bluey",      "https://www.youtube.com/results?search_query=bluey+episodes",Color(0xFF34D399),Color(0xFF059669)),
        Channel("🐾","Paw Patrol", "https://www.youtube.com/results?search_query=paw+patrol",Color(0xFF3B82F6),Color(0xFF1D4ED8)),
        Channel("🐷","Peppa Pig",  "https://www.youtube.com/results?search_query=peppa+pig",Color(0xFFF43F5E),Color(0xFFBE123C)),
    )
    var currentUrl by remember { mutableStateOf("https://www.youtube.com/kids") }
    var isLoading   by remember { mutableStateOf(true) }
    var webViewRef  by remember { mutableStateOf<WebView?>(null) }

    val inf = rememberInfiniteTransition(label = "shows")
    // Saturday morning golden-hour background
    val popcornY1 by inf.animateFloat(1f, 0f, infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart), label = "p1")
    val popcornY2 by inf.animateFloat(1f, 0f, infiniteRepeatable(tween(5200, easing = LinearEasing), RepeatMode.Restart, StartOffset(1800)), label = "p2")
    val starTw by inf.animateFloat(0.3f, 1f, infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse), label = "st")
    val cloudX  by inf.animateFloat(-0.2f, 1.1f, infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart), label = "cx")

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Cozy golden-hour background ──────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF78350F), Color(0xFFB45309), Color(0xFFF59E0B), Color(0xFFFDE68A)))
        ))
        // Stars
        for (i in 0 until 20) {
            val sx = (i * 113f) % 100f; val sy = (i * 71f) % 40f
            Text("⭐", fontSize = (10 + i % 8).sp,
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                    .padding(start = (sx * 3.6f).dp, top = (sy * 5f).dp).alpha(starTw * 0.6f))
        }
        // Clouds
        Text("☁️", fontSize = 52.sp,
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                .padding(start = (cloudX * 400 - 60).dp, top = 30.dp).alpha(0.35f))
        // Popcorn floating up
        Text("🍿", fontSize = 22.sp,
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                .padding(start = 40.dp, top = (popcornY1 * 600 + 50).dp).alpha(1f - popcornY1 * 0.7f))
        Text("🍿", fontSize = 18.sp,
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                .padding(start = 300.dp, top = (popcornY2 * 600 + 100).dp).alpha(1f - popcornY2 * 0.7f))

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            // Header
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back",
                        tint = Color(0xFF78350F))
                }
                Text("📺 JuJu's Shows", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF78350F))
            }

            // ── Channel shortcut buttons ──────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                channels.forEach { ch ->
                    ChannelButton(ch) {
                        SoundManager.playTap()
                        currentUrl = ch.url
                        webViewRef?.loadUrl(ch.url)
                    }
                }
            }

            // Navigation row
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                // Back in web history
                Button(onClick = { if (webViewRef?.canGoBack() == true) webViewRef?.goBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x44000000)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("◀ Back", color = Color.White, fontSize = 13.sp)
                }
                // Loading indicator
                if (isLoading) {
                    Text("🌀 Loading...", fontSize = 13.sp, color = Color(0xFF78350F),
                        modifier = Modifier.weight(1f))
                } else {
                    Spacer(Modifier.weight(1f))
                }
                // Home (YouTube Kids)
                Button(onClick = {
                    currentUrl = "https://www.youtube.com/kids"
                    webViewRef?.loadUrl(currentUrl)
                    SoundManager.playTap()
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("🏠 Home", color = Color.White, fontSize = 13.sp)
                }
            }

            // ── WebView ───────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) {
                AndroidView(factory = { ctx ->
                    WebView(ctx).apply {
                        webViewRef = this
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled  = true
                            mediaPlaybackRequiresUserGesture = false
                            useWideViewPort = true
                            loadWithOverviewMode = true
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView, url: String) {
                                isLoading = false
                            }
                            override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                                isLoading = true
                            }
                        }
                        webChromeClient = WebChromeClient()
                        loadUrl(currentUrl)
                    }
                }, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun ChannelButton(ch: Channel, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "cs")
    Box(modifier = Modifier
        .scale(scale)
        .clip(RoundedCornerShape(20.dp))
        .background(Brush.linearGradient(listOf(ch.g1, ch.g2)))
        .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
        .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(ch.emoji, fontSize = 20.sp)
            Text(ch.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
