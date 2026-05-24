package com.jujusworld.screens

import android.content.Intent
import android.net.Uri
import android.webkit.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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

data class Bookmark(val emoji: String, val title: String, val url: String, val external: Boolean = false)

@Composable
fun BrowserScreen(navController: NavController) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf("") }
    var pageTitle by remember { mutableStateOf("") }
    var loadProgress by remember { mutableIntStateOf(100) }

    val bookmarks = listOf(
        Bookmark("🌿", "National Geographic Kids", "https://kids.nationalgeographic.com", false),
        Bookmark("🧪", "NASA Kids' Club",           "https://www.nasa.gov/kidsclub/index.html", false),
        Bookmark("📖", "Storynory",                  "https://www.storynory.com", false),
        Bookmark("🎨", "Art for Kids Hub",           "https://www.artforkidshub.com", false),
        Bookmark("🔬", "Science Kids",               "https://www.sciencekids.co.nz", false),
        Bookmark("🎵", "Classics for Kids",          "https://www.classicsforkids.com", false),
        Bookmark("🦁", "San Diego Zoo Kids",         "https://kids.sandiegozoo.org", false),
        Bookmark("📚", "International Children's Library", "https://en.childrenslibrary.org", false),
        Bookmark("▶", "YouTube (via browser)",      "https://www.youtube.com", true),
    )

    if (currentUrl.isNotEmpty()) {
        Column(Modifier.fillMaxSize().background(Color(0xFF1E1B4B))) {
            // Navigation bar
            Row(
                Modifier.fillMaxWidth().background(Color(0xFF2D1B69)).padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (webViewRef?.canGoBack() == true) webViewRef?.goBack()
                    else currentUrl = ""
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                IconButton(onClick = {
                    if (webViewRef?.canGoForward() == true) webViewRef?.goForward()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Forward",
                        tint = if (webViewRef?.canGoForward() == true) Color.White else Color(0xFF6D28D9))
                }
                IconButton(onClick = { webViewRef?.reload() }) {
                    Icon(Icons.Filled.Refresh, "Reload", tint = Color.White)
                }
                Text(pageTitle.ifEmpty { currentUrl }, fontSize = 12.sp, color = Color.White,
                    modifier = Modifier.weight(1f), maxLines = 1)
                IconButton(onClick = { currentUrl = "" }) {
                    Icon(Icons.Filled.Home, "Home", tint = Color(0xFFEC4899))
                }
            }
            if (loadProgress < 100) {
                LinearProgressIndicator(
                    progress = { loadProgress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFEC4899)
                )
            }
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).also { wv ->
                        webViewRef = wv
                        wv.settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            setSupportZoom(true)
                        }
                        wv.webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                                loadProgress = 0
                            }
                            override fun onPageFinished(view: WebView, url: String) {
                                loadProgress = 100
                            }
                            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                                val url = request.url.toString()
                                if (bookmarks.any { it.external && url.contains(it.url.substringAfter("//").substringBefore("/")) }) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    ctx.startActivity(intent)
                                    return true
                                }
                                return false
                            }
                        }
                        wv.webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView, newProgress: Int) {
                                loadProgress = newProgress
                            }
                            override fun onReceivedTitle(view: WebView, title: String) {
                                pageTitle = title
                            }
                        }
                        wv.loadUrl(currentUrl)
                    }
                },
                update = { },
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    // Bookmark grid
    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF0C4A6E), Color(0xFF1E1B4B)))
    )) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🌐  Browser", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text("Safe websites just for JuJu!", fontSize = 16.sp, color = Color(0xFFBAE6FD),
                modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(bookmarks) { bm ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F2749))
                            .clickable {
                                if (bm.external) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bm.url))
                                    context.startActivity(intent)
                                } else {
                                    currentUrl = bm.url
                                    pageTitle = bm.title
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                            Text(bm.emoji, fontSize = 32.sp)
                            Text(bm.title, fontSize = 10.sp, color = Color.White,
                                textAlign = TextAlign.Center, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}
