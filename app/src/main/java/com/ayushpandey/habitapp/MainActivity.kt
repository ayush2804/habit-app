package com.ayushpandey.habitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ayushpandey.habitapp.ui.HabitNav
import com.ayushpandey.habitapp.ui.theme.HabitAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as HabitApplication
        setContent {
            HabitAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HabitNav(app = app)
                }
            }
        }
    }
}
