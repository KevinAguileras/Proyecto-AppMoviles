# AgroNova

Aplicación móvil Android (demostrativa) que conecta a **productores agrícolas** con **aplicadores**. El productor publica solicitudes de aplicación de productos, el aplicador envía cotizaciones y el productor acepta la que prefiera.

Proyecto desarrollado para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles** (Duoc UC).

## Funcionalidades

**General**
- Inicio de sesión con validación de campos y aceptación de términos.
- Registro de usuario con selección de rol (Productor o Aplicador).
- Perfil de usuario con cambio de vista entre roles y cierre de sesión.
- Barra de navegación inferior (Inicio, Info, Perfil) y botón atrás integrado.

**Vista Productor**
- Listado de "Mis solicitudes" con estado (Pendiente, Con ofertas, Finalizada).
- Creación de solicitudes: tipo de producto, superficie, agua disponible, horario, obstáculos, comentarios y hasta 4 fotos de referencia.
- Detalle de la solicitud con ofertas recibidas, aceptación de oferta y contacto simulado.

**Vista Aplicador**
- Exploración de solicitudes disponibles cerca de ti.
- Cotización por hectárea con cálculo del total en tiempo real (+ IVA).

## Tecnologías

| Componente | Versión |
|---|---|
| Lenguaje | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3 |
| Arquitectura | MVVM (ViewModel + estado con `mutableStateOf`) |
| Android Gradle Plugin | 9.2.1 |
| minSdk / targetSdk | 24 / 36 |

## Estructura del proyecto

Paquete base: `com.Duoc.TecnoApp.example.TecnoApp`

```
app/src/main/java/com/Duoc/TecnoApp/example/TecnoApp/
├── MainActivity.kt
├── model/
│   └── Modelos.kt                  # Enums, data classes y formato de pesos
├── repository/
│   └── RepositorioSolicitudes.kt   # Datos en memoria
├── viewmodel/
│   └── TecMundoViewModel.kt        # Estado y lógica de la app
└── ui/
    ├── Componentes.kt              # Componentes reutilizables
    ├── Navegacion.kt               # Controlador de pantallas
    ├── Login.kt
    ├── Registro.kt
    ├── HomeProductor.kt
    ├── NuevaSolicitud.kt
    ├── DetalleMatching.kt
    ├── Exploracion.kt
    ├── Cotizacion.kt
    ├── Perfil.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Cómo ejecutar

1. Clona el repositorio:
   ```bash
   git clone https://github.com/USUARIO/REPOSITORIO.git
   ```
2. Abre la carpeta del proyecto en **Android Studio**.
3. Espera a que Gradle sincronice (**Sync Now**).
4. Selecciona un emulador o dispositivo físico y presiona **Run ▶**.

## Uso de la demo

- **Iniciar sesión:** el correo viene precargado (`productor@tecmundo.test`). Escribe cualquier contraseña y marca *Acepto los términos y condiciones*.
- **Probar ambos roles:** entra a **Perfil** y usa el botón *Cambiar a vista Aplicador / Productor*.
- **Flujo completo sugerido:**
  1. Como productor, crea una solicitud nueva.
  2. Cambia a vista aplicador y envía una oferta.
  3. Vuelve a vista productor, abre la solicitud y acepta la oferta.
