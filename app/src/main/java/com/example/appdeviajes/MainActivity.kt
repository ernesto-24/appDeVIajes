package com.example.appdeviajes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appdeviajes.ui.navegacion.AppNavegacion
import com.example.appdeviajes.ui.theme.AppDeViajesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppDeViajesTheme {
                AppNavegacion()
            }
        }
    }
}
