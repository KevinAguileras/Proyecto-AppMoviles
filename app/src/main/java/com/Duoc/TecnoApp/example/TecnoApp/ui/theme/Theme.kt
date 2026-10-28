package com.Duoc.TecnoApp.example.TecnoApp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColoresTecMundo = lightColorScheme(
    primary = VerdePrincipal,
    onPrimary = Color.White,
    secondary = VerdeAcento,
    onSecondary = Color.White,
    background = FondoAplicacion,
    onBackground = TextoOscuro,
    surface = Color.White,
    onSurface = TextoOscuro,
    outline = BordeSuave
)

@Composable
fun TecnoAppTheme(contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColoresTecMundo, typography = Typography, content = contenido)
}
