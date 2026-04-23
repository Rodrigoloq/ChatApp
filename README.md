
# ChatApp

ChatApp es una aplicación de mensajería en tiempo real para Android, construida utilizando prácticas modernas de desarrollo con Kotlin y Jetpack Compose. Utiliza Firebase como backend, ofreciendo funcionalidades como autenticación de usuarios, base de datos en tiempo real, almacenamiento en la nube y notificaciones push.

## Características

- **Autenticación**: Inicio de sesión y registro seguros usando Email/Contraseña y Google Sign-In. Incluye opción de recuperación de contraseña.
- **Chat en tiempo real**: Envía y recibe mensajes de texto e imágenes instantáneamente con otros usuarios.
- **Perfiles de usuario**: Visualiza y administra tu perfil, incluyendo la actualización de tu nombre y foto.
- **Estado de usuario**: Visualiza si otros usuarios están en línea o desconectados.
- **Descubrimiento de usuarios**: Pantalla dedicada para ver y buscar todos los usuarios registrados en la aplicación.
- **Lista de conversaciones**: Accede rápidamente a todos tus chats activos desde una lista centralizada.
- **Compartir imágenes**: Envía imágenes desde la galería de tu dispositivo dentro de un chat. Las imágenes pueden visualizarse en pantalla completa con zoom.
- **Notificaciones push**: Recibe notificaciones de nuevos mensajes mediante Firebase Cloud Messaging (FCM), incluso cuando la app está en segundo plano.

## Tecnologías utilizadas

- **Lenguaje**: [Kotlin](https://kotlinlang.org/)
- **Interfaz de usuario**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Arquitectura**: Model-View-ViewModel (MVVM)
- **Backend**: [Firebase](https://firebase.google.com/)
    *   **Authentication**: Gestión de usuarios e inicio de sesión seguro.
    *   **Realtime Database**: Almacenamiento y sincronización en tiempo real de datos y mensajes.
    *   **Cloud Storage**: Almacenamiento de imágenes y fotos de perfil.
    *   **Cloud Messaging (FCM)**: Notificaciones push.
- **Navegación**: [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation)
- **Carga de imágenes**: [Coil](https://coil-kt.github.io/coil/)
- **Cliente HTTP**: [Volley](https://developer.android.com/training/volley) (usado para enviar notificaciones FCM)

## Estructura del proyecto

El proyecto sigue una estructura de paquetes basada en funcionalidades para mejorar la organización y la escalabilidad.

```
com.rodrigoloq.chatapp
│
├── auth/          # Pantallas de autenticación (Login, Registro, Recuperar contraseña)
├── chat/          # Pantalla de chat individual, mensajes y lógica relacionada
├── chats/         # Pantalla con la lista de conversaciones activas
├── entities/      # Clases del modelo de datos (User, Chat, etc.)
├── navigation/    # Configuración de navegación con Jetpack Navigation
├── notifications/ # Servicio de Firebase Cloud Messaging
├── profile/       # Perfil de usuario, edición de información y cambio de contraseña
├── users/         # Pantalla para listar y buscar usuarios
└── utils/         # Funciones utilitarias y constantes
```
