package com.jujusworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.jujusworld.navigation.JujuNavGraph
import com.jujusworld.ui.theme.JujusWorldTheme
import com.jujusworld.utils.Prefs
import com.jujusworld.utils.SoundManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Prefs.init(this)
        SoundManager.init(this)
        enableEdgeToEdge()
        setContent {
            JujusWorldTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    JujuNavGraph()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundManager.release()
    }
}
