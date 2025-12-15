# Zyndra v1.0 - Agente Personal con Sistema Gacha

![Android](https://img.shields.io/badge/Android-24%2B-green)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-blue)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.02-orange)
![Room](https://img.shields.io/badge/Room-2.6.1-purple)

## Descripción

Zyndra es una aplicación móvil de agente personal para Android que combina funcionalidades de chat inteligente con un sistema gacha adictivo para desbloquear cosméticos y personalizar la experiencia del usuario. Desarrollada con las tecnologías más modernas de Android, incluyendo Jetpack Compose, Room Database y arquitectura MVVM.

# **NOTA**: ROOM Fue modificado en la ultima version por la API del proyecto de FULLSTACK Para cumplir con los estandares de la evaluación
## Características Principales

### 🎰 Sistema Gacha
- **20 ítems coleccionables** divididos en 4 categorías:
  - 10 estilos de burbujas de chat
  - 8 iconos de perfil
  - 4 fondos de pantalla
  - 2 animaciones especiales
- **4 rarezas** con probabilidades balanceadas (Común 60%, Raro 30%, Super Raro 9%, Legendario 1%)
- **Sistema de monedas** con recompensas por actividad
- **Inventario persistente** con opciones de equipar/desequipar

### 💬 Chat Inteligente
- Conversaciones ilimitadas con historial completo
- Soporte para adjuntos multimedia (imágenes, archivos)
- Integración con cámara para captura directa
- Burbujas personalizables con cosméticos del gacha
- Persistencia local con Room Database
- Integración con API externa (n8n)

### 👤 Perfil de Usuario
- Foto de perfil personalizable
- Estadísticas en tiempo real (monedas, nivel, ítems)
- Sistema de niveles y experiencia
- Gestión de sesión con auto-login
- Cierre de sesión seguro

### 📷 Funcionalidades de Hardware
- **Cámara:** Captura de fotos con preview en tiempo real
- **Galería:** Selección de imágenes y archivos
- **Bluetooth:** Visualización de dispositivos emparejados

### 🎨 Personalización
- 10 estilos de burbujas desbloqueables
- Tema claro/oscuro
- Cosméticos aplicables en tiempo real
- Interfaz Material Design 3

## Tecnologías Utilizadas

### Core
- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI moderna y declarativa
- **Material Design 3** - Sistema de diseño

### Arquitectura
- **MVVM** - Separación de responsabilidades
- **Repository Pattern** - Abstracción de datos
- **Coroutines & Flow** - Programación asíncrona y reactiva

### Persistencia
- **Room Database** - Base de datos local
- **DataStore** - Preferencias y sesión
- **SharedPreferences** - Configuración legacy

### Multimedia
- **CameraX** - Captura de fotos
- **Coil** - Carga de imágenes
- **Activity Result API** - Selección de archivos

### Networking
- **Retrofit** - Cliente HTTP
- **Gson** - Serialización JSON

### Utilities
- **Accompanist Permissions** - Gestión de permisos
- **Lottie** - Animaciones

## Requisitos del Sistema

- **Android:** 7.0 (API 24) o superior
- **Espacio:** ~50 MB
- **Permisos:**
  - Cámara (opcional)
  - Almacenamiento (opcional)
  - Bluetooth (opcional)
  - Internet (requerido)

## EndPoints Usados
- Zyndra utiliza tecnologia de AWS Aurora RDS para api *INTERNA* donde se almacenan los Usuarios creados
- Zyndra utiliza para la api *EXTERNA* N8N para las respuestas del agente 

## Instalación

### Para Desarrollo

1. **Clonar o descomprimir el proyecto**
   ```bash
   unzip zyndrav0-fixed.zip
   cd zyndrav0-main
   ```

2. **Abrir en Android Studio**
   - File > Open
   - Seleccionar carpeta del proyecto
   - Esperar sincronización de Gradle

3. **Ejecutar**
   - Conectar dispositivo Android o iniciar emulador
   - Run > Run 'app' (Shift+F10)

### Para Usuarios

1. Compilar APK desde Android Studio
2. Instalar en dispositivo Android
3. Conceder permisos solicitados
4. ¡Disfrutar!

## APK
- El APK esta en el drive de los integrantes y el archivo jks tambien

## Guía de Uso Rápido

### Primer Inicio
1. Ingresa email y contraseña (se crea usuario automáticamente)
2. Explora las 4 pestañas principales: Chats, Gacha, Perfil, Ajustes

### Obtener Monedas
- Envía mensajes en el chat (+5 monedas por mensaje)
- Monedas iniciales: 500 (suficiente para 5 tiradas)

### Usar el Gacha
1. Ve a la pestaña "Gacha"
2. Toca "Tirar Gacha" (costo: 100 monedas)
3. Observa la animación del resultado
4. Equipa ítems desde tu inventario

### Personalizar Chat
1. Obtén burbujas del gacha
2. Equipa tu favorita desde el inventario
3. Tus mensajes usarán ese estilo automáticamente

## Estructura del Proyecto

```
app/src/main/java/com/example/zyndrav0/
├── data/               # Capa de datos
│   ├── database/       # Room Database
│   ├── repository/     # Repositorios
│   └── datastore/      # DataStore
├── model/              # Modelos de datos
├── network/            # API y networking
├── ui/                 # Interfaz de usuario
│   ├── screen/         # Pantallas Compose
│   └── theme/          # Tema Material3
├── viewmodel/          # ViewModels MVVM
├── util/               # Utilidades
├── navigation/         # Navegación
└── MainActivity.kt     # Actividad principal
```

## Documentación

- **[DOCUMENTACION_ZYNDRA.md](DOCUMENTACION_ZYNDRA.md)** - Documentación técnica completa
- **[GUIA_RAPIDA.md](GUIA_RAPIDA.md)** - Guía de inicio rápido
- **[CHECKLIST_VERIFICACION.md](CHECKLIST_VERIFICACION.md)** - Verificación de funcionalidades


## Características Técnicas

| Característica | Valor |
|----------------|-------|
| Lenguaje | Kotlin 100% |
| UI Framework | Jetpack Compose |
| Arquitectura | MVVM + Repository |
| Base de Datos | Room 2.6.1 | --> con transicion a Aurora
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 (Android 14+) |
| Compile SDK | 36 |

## Problemas Conocidos

- **Bluetooth:** Solo visualiza dispositivos, no establece conexiones
- **Cosméticos:** Solo burbujas aplicables, iconos y fondos pendientes
- **Sin token de contraseña:** No se bloquea al usuario por intentos fallidos

## Roadmap Futuro

- [ ] Sistema de logros y recompensas
- [ ] Notificaciones push
- [ ] Backup en la nube
- [ ] Modo offline robusto
- [ ] Tutorial interactivo
- [ ] Más tipos de cosméticos
- [ ] Analisis de imagenes
- [ ] Sistema de suscripcion
- [ ] Sistema de pity para gacha
- [ ] Tiradas x10 con descuento

## Créditos
Demis Zuñiga - Tapia - Gabriel Colmenares
**Proyecto Universitario**
- Desarrollado con Android Studio
- Tecnologías: Kotlin, Jetpack Compose, Room, CameraX, Aurora RDS 
- Arquitectura: MVVM 

## Licencia

Este es un proyecto académico desarrollado con fines educativos.

## Contacto

Al Whatsapp.

---

**Versión:** 1.0.0  
**Última actualización:** Diciembre 06-12-2025  
**Estado:** ✅ Completo y funcional o eso dicen

¡Gracias por usar Zyndra! 🎉
