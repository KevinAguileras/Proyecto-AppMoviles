package com.Duoc.TecnoApp.example.TecnoApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.Duoc.TecnoApp.example.TecnoApp.ui.screens.auth.LoginScreen
import com.Duoc.TecnoApp.example.TecnoApp.ui.theme.TecnoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TecnoAppTheme {
                LoginScreen(
                    onLoginExitoso = {
                        // Aquí conectaremos luego con el Home del Productor/Aplicador
                    },
                    onIrARegistro = {
                        // Aquí manejaremos la transición al Registro
                    }
                )
            }
        }
    }
}