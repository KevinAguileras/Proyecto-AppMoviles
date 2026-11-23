# 🌱 AgroNova

**Plataforma de conexión entre productores agrícolas y aplicadores.**

AgroNova es una aplicación móvil Android desarrollada como proyecto académico para Duoc UC. Su objetivo es facilitar la comunicación entre productores agrícolas que necesitan realizar aplicaciones en sus terrenos y personas que ofrecen servicios de aplicación.

La aplicación permite gestionar solicitudes agrícolas, consultar trabajos disponibles y realizar cotizaciones según las necesidades de cada productor.

---

## 1. Información general del proyecto

| Característica | Descripción |
|---|---|
| Nombre | AgroNova |
| Tipo de aplicación | Aplicación móvil Android |
| Plataforma | Android |
| Lenguaje principal | Kotlin |
| Interfaz | Jetpack Compose |
| Arquitectura | MVVM |
| Backend | Xano |
| Comunicación | API REST |
| Formato de datos | JSON |
| Entorno de desarrollo | Android Studio |

## 2. Objetivo del proyecto

Desarrollar una aplicación móvil que permita conectar a productores agrícolas con aplicadores, facilitando la publicación, consulta y gestión de solicitudes de aplicación agrícola.

La aplicación contempla dos tipos de usuarios:

- **Productor:** puede crear solicitudes de aplicación, indicar las características de su terreno y consultar el estado de sus solicitudes.
- **Aplicador:** puede explorar solicitudes disponibles, revisar sus características y realizar cotizaciones.

## 3. Tecnologías utilizadas

### Kotlin
Lenguaje utilizado para desarrollar la lógica de la aplicación Android.

### Jetpack Compose
Framework utilizado para construir las interfaces mediante componentes declarativos.

### Android Studio
Entorno de desarrollo utilizado para programar, ejecutar y generar la aplicación.

### Xano
Plataforma backend utilizada para almacenar información y proporcionar endpoints mediante API REST.

### JSON
Formato utilizado para enviar y recibir información entre la aplicación y Xano.

### Coroutines
Utilizadas para realizar operaciones asíncronas y evitar ejecutar las solicitudes de red en el hilo principal.

## 4. Arquitectura del proyecto

El proyecto utiliza el patrón arquitectónico **MVVM (Model - View - ViewModel)**, separando la interfaz, los datos y la lógica de presentación.

La estructura principal está organizada de la siguiente manera:

```text
app/
└── src/
    └── main/
        └── java/
            └── com/Duoc/TecnoApp/Duocuc/TecnoApp/
                │
                ├── model/
                │   └── Modelos.kt
                │
                ├── repository/
                │   ├── ConfiguracionXano.kt
                │   └── RepositorioSolicitudes.kt
                │
                ├── viewmodel/
                │   └── TecMundoViewModel.kt
                │
                └── ui/
                    ├── Navegacion.kt
                    ├── PantallaLogin.kt
                    ├── PantallaRegistro.kt
                    ├── PantallaInicioProductor.kt
                    ├── PantallaNuevaSolicitud.kt
                    ├── PantallaDetalleProductor.kt
                    ├── PantallaExploracion.kt
                    ├── PantallaCotizacion.kt
                    └── PantallaPerfil.kt
```

**Nota:** la estructura representa la organización prevista del proyecto. Los nombres y ubicaciones deben coincidir con los archivos definitivos del repositorio.

### Model

Contiene las clases y estructuras utilizadas para representar la información de la aplicación.

Entre los datos gestionados se encuentran:

- Solicitudes agrícolas.
- Tipos de productos.
- Horarios de aplicación.
- Estados de las solicitudes.
- Información relacionada con los usuarios.

### Repository

Se encarga de gestionar la comunicación con el backend y las operaciones relacionadas con los datos.

Entre sus responsabilidades están:

- Realizar solicitudes HTTP.
- Consultar solicitudes almacenadas en Xano.
- Publicar nuevas solicitudes.
- Procesar respuestas JSON.
- Convertir los datos recibidos en objetos Kotlin.

### ViewModel

Administra el estado de la aplicación y conecta la interfaz con los repositorios.

Entre sus responsabilidades se encuentran:

- Gestionar el inicio de sesión.
- Gestionar el registro de usuarios.
- Controlar la navegación.
- Administrar el rol activo.
- Crear y consultar solicitudes.
- Gestionar mensajes de error.
- Mantener la información seleccionada por el usuario.

### UI

Contiene las pantallas y componentes visuales desarrollados con Jetpack Compose.

La navegación centralizada se administra mediante `Navegacion.kt`.

## 5. Funcionalidades principales

### Inicio de sesión

Permite que los usuarios accedan a la aplicación utilizando sus credenciales.

El proceso se conecta con el endpoint de autenticación de Xano y permite gestionar la respuesta del servidor.

### Registro de usuarios

Permite crear una cuenta y seleccionar el rol correspondiente.

Después de completar el registro, el usuario puede volver a la pantalla de inicio de sesión para ingresar con sus credenciales.

### Gestión de roles

La aplicación contempla dos perfiles:

- Productor.
- Aplicador.

La navegación y las funcionalidades disponibles dependen del rol activo.

### Solicitudes agrícolas

Los productores pueden publicar solicitudes indicando información como:

- Tipo de producto.
- Superficie en hectáreas.
- Disponibilidad de agua.
- Horario de aplicación.
- Obstáculos presentes en el terreno.
- Comentarios adicionales.

### Exploración de solicitudes

Los aplicadores pueden consultar las solicitudes disponibles y revisar sus características.

### Cotizaciones

Se contempla una pantalla para que los aplicadores puedan realizar ofertas relacionadas con las solicitudes disponibles.

### Perfil de usuario

Permite consultar la información del usuario, cambiar de rol y cerrar sesión.

## 6. Conexión con Xano

AgroNova utiliza Xano como backend para gestionar la información y comunicarse mediante endpoints REST.

La comunicación se realiza mediante solicitudes HTTP y respuestas en formato JSON.

La configuración de conexión se centraliza en `ConfiguracionXano.kt`.

### Configuración

La aplicación utiliza una URL base para construir las direcciones de los endpoints.

```kotlin
object ConfiguracionXano {
    const val URL_BASE = "https://x8ki-letl-twmt.n7.xano.io/api:i1fPbI_E"
    const val URL_LOGIN = "https://x8ki-letl-twmt.n7.xano.io/api:IS4D1C5l/auth/login"
}
```

Los valores definitivos deben coincidir con la configuración actual del proyecto en Xano.

### Métodos HTTP utilizados

**GET**

Se utiliza para consultar información almacenada en el backend.

Ejemplo implementado en el repositorio:

```kotlin
solicitarXano("GET", "solicitud")
```

**POST**

Se utiliza para enviar información al backend.

Ejemplo implementado:

```kotlin
solicitarXano("POST", "solicitud", datos)
```

### Procesamiento de respuestas

Las respuestas del backend se procesan mediante las clases `JSONObject`, `JSONArray` y `JSONTokener`.

El repositorio contempla:

- Lectura de respuestas JSON.
- Validación de códigos HTTP.
- Manejo de errores de conexión.
- Conversión de solicitudes recibidas desde Xano.
- Actualización de la lista de solicitudes.

### Endpoints

Los endpoints definitivos deben documentarse según la configuración real de Xano.

| Método | Recurso | Función |
|---|---|---|
| POST | `/auth/login` | Autenticación de usuarios |
| GET | `/solicitud` | Consultar solicitudes |
| POST | `/solicitud` | Publicar una solicitud |

Los endpoints de registro y ofertas deben agregarse con sus rutas y métodos reales.

## 7. Navegación de la aplicación

La navegación se centraliza en `Navegacion.kt`.

Se utiliza el estado administrado por el ViewModel para determinar qué pantalla debe mostrarse.

Entre las pantallas contempladas están:

1. Acceso.
2. Registro.
3. Inicio del productor.
4. Nueva solicitud.
5. Detalle del productor.
6. Exploración del aplicador.
7. Cotización.
8. Perfil.

La barra de navegación inferior permite acceder a las secciones principales cuando el usuario ya ingresó a la aplicación.

## 8. Permisos de Android

La aplicación necesita permiso de acceso a Internet para comunicarse con el backend.

En `AndroidManifest.xml` se declara:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Los demás permisos y recursos nativos deben documentarse según las funcionalidades que estén implementadas y probadas en la versión final.

## 9. Instalación y ejecución

Para abrir el proyecto:

1. Descargar o clonar el repositorio.
2. Abrir Android Studio.
3. Seleccionar la carpeta del proyecto.
4. Esperar la sincronización de Gradle.
5. Verificar la configuración de Xano.
6. Ejecutar la aplicación en un emulador o dispositivo Android.

### Clonar el repositorio

```bash
git clone URL_DEL_REPOSITORIO
```

Después de descargar el proyecto, abrirlo desde Android Studio.

## 10. Compilación de la aplicación

Para comprobar la compilación desde Android Studio:

1. Abrir el menú Build.
2. Seleccionar Make Project.
3. Esperar a que finalice la compilación.
4. Revisar los mensajes de errores en Build.

También se puede ejecutar desde la terminal:

```bash
gradlew assembleDebug
```

## 11. Generación de APK firmado

Para generar una versión de distribución:

1. Abrir Android Studio.
2. Seleccionar Build.
3. Elegir Generate Signed Bundle / APK.
4. Seleccionar APK.
5. Crear o seleccionar el archivo de firma `.jks`.
6. Seleccionar la variante `release`.
7. Completar el proceso de generación.

El archivo APK generado debe probarse en un dispositivo Android antes de su entrega.

**Importante:** el archivo de firma y sus contraseñas deben mantenerse privados y no subirse al repositorio público.

## 12. Pruebas

El proyecto debe verificarse mediante pruebas de funcionamiento y pruebas unitarias.

Las verificaciones funcionales deben considerar:

- Inicio de sesión con credenciales válidas.
- Manejo de credenciales incorrectas.
- Registro de nuevos usuarios.
- Consulta de solicitudes.
- Publicación de solicitudes.
- Navegación entre pantallas.
- Cambio de rol.
- Cierre de sesión.
- Comunicación con Xano.

Las pruebas unitarias definitivas deben documentarse indicando su nombre, objetivo y resultado.

## 13. Control de versiones

El código fuente se administra mediante Git y GitHub.

Se recomienda mantener commits descriptivos que permitan identificar los cambios realizados durante el desarrollo.

Ejemplos:

```bash
git add .
git commit -m "Implementa conexión con Xano"
git commit -m "Agrega registro de usuarios"
git commit -m "Corrige navegación entre pantallas"
git commit -m "Actualiza interfaz de AgroNova"
git push origin main
```

## 14. Seguridad y consideraciones

- No publicar contraseñas ni credenciales privadas.
- No subir archivos `.jks` al repositorio.
- Mantener la configuración del backend correctamente actualizada.
- Validar las respuestas recibidas desde Xano.
- Manejar los errores de conexión.
- No almacenar información sensible innecesaria en el dispositivo.

## 15. Autores

**Proyecto académico desarrollado para Duoc UC.**

**Nombre del proyecto:** AgroNova  
**Área:** Desarrollo de aplicaciones móviles  
**Tecnología principal:** Kotlin y Jetpack Compose  
**Backend:** Xano

---

*AgroNova busca facilitar la conexión entre productores agrícolas y aplicadores mediante una plataforma móvil sencilla, organizada y funcional.*
