package com.jujusworld.screens

import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

data class ShowItem(val emoji: String, val title: String, val url: String, val external: Boolean = false)

@Composable
fun ShowsScreen(navController: NavController) {
    val context = LocalContext.current
    var currentUrl by remember { mutableStateOf("") }
    var showTitle by remember { mutableStateOf("") }

    val shows = listOf(
        ShowItem("👩‍🏫", "Ms. Rachel", "https://www.youtube.com/@SongsforLittles", external = true),
        ShowItem("🐷", "Peppa Pig", "https://www.youtube.com/results?search_query=peppa+pig+full+episodes", external = true),
        ShowItem("🐶", "Bluey", "https://www.bluey.tv/watch/", external = false),
        ShowItem("🦁", "Lion Guard", "https://www.youtube.com/results?search_query=lion+guard+full+episodes", external = true),
        ShowItem("🧚", "Fairy Tales", "https://www.youtube.com/results?search_query=fairy+tales+for+kids", external = true),
        ShowItem("🎪", "PBS Kids", "https://pbskids.org", external = false),
        ShowItem("🦋", "Butterfly World", "https://www.youtube.com/results?search_query=butterfly+magic+for+kids", external = true),
        ShowItem("🌈", "Rainbow Magic", "https://www.youtube.com/results?search_query=rainbow+magic+kids+show", external = true),
        ShowItem("🎵", "Cocomelon", "https://www.youtube.com/results?search_query=cocomelon+nursery+rhymes", external = true),
    )

    if (currentUrl.isNotEmpty()) {
        Column(Modifier.fillMaxSize().background(Color(0xFF1E1B4B))) {
            Row(
                Modifier.fillMaxWidth().background(Color(0xFF2D1B69)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentUrl = "" }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(showTitle, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f))
            }
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient()
                        loadUrl(currentUrl)
                    }
                },
                update = { it.loadUrl(currentUrl) },
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF4A1D96), Color(0xFF1E1B4B)))
        )
    ) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            // Header
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("📺  Shows", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            // Ms. Rachel featured
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFF6B9D), Color(0xFF9B59B6))))
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(shows[0].url))
                        context.startActivity(intent)
                    }
                    .padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("👩‍🏫", fontSize = 52.sp)
                    Column {
                        Text("Ms. Rachel ⭐ FEATURED", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Songs for Littles — JuJu's favorite!", fontSize = 14.sp, color = Color(0xFFE9D5FF))
                        Text("Opens in YouTube App", fontSize = 12.sp, color = Color(0xFFFCD34D))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            // Show grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(shows.drop(1)) { show ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2D1B69))
                            .clickable {
                                if (show.external) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(show.url))
                                    context.startActivity(intent)
                                } else {
                                    currentUrl = show.url
                                    showTitle = show.title
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                            Text(show.emoji, fontSize = 36.sp)
                            Text(show.title, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                color = Color.White, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
