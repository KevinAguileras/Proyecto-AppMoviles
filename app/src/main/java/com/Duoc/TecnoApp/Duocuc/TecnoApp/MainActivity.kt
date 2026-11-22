package com.Duoc.TecnoApp.Duocuc.TecnoApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.Duoc.TecnoApp.Duocuc.TecnoApp.ui.NavegacionAplicacion
import com.Duoc.TecnoApp.Duocuc.TecnoApp.ui.theme.TecnoAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TecnoAppTheme {
                NavegacionAplicacion()
            }
        }
    }
}
