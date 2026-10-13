# Lumina AI - Native Android & Web Companion

Lumina AI adalah asisten virtual dengan kepribadian fleksibel, UI glassmorphism modern, animasi audio equalizer yang halus, dan kemampuan voice/chat interaktif bertenaga Google Gemini.

---

## 📱 Native Android Application (Kotlin + Jetpack Compose)

Aplikasi ini telah dikonversi secara lengkap ke arsitektur native Android modern:

### Arsitektur & Teknologi:
- **UI Framework**: Jetpack Compose dengan Material 3 kustom (Glassmorphism & Midnight Dark Palette `#0A0F14`).
- **Pola Desain**: Clean Architecture & MVVM (`LuminaViewModel` + StateFlow / Coroutines).
- **Koneksi Jaringan AI**: Ktor HTTP Client dengan dukungan Server-Sent Events (SSE) streaming chunks secara real-time ke Gemini API (`streamGenerateContent`).
- **Image Loading**: Coil Compose (`rememberAsyncImagePainter`).
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`, `app/build.gradle.kts`) dengan Secrets Gradle Plugin.

### Struktur Proyek Android (`/app`):
```text
├── build.gradle.kts                   # Root Gradle build script
├── settings.gradle.kts                # Project module settings (:app)
├── gradle.properties                  # JVM & AndroidX properties
└── app/
    ├── build.gradle.kts               # Module dependencies & signing config
    ├── proguard-rules.pro             # Proguard rules
    └── src/main/
        ├── AndroidManifest.xml        # Permissions & Application/Activity config
        ├── res/                       # Colors, Strings, Adaptive Icons, Themes
        └── java/com/lumina/ai/
            ├── LuminaApplication.kt   # Application class
            ├── MainActivity.kt        # Entry point activity (edge-to-edge)
            ├── data/
            │   ├── model/Models.kt    # Data classes (AgentConfig, ChatMessage, etc.)
            │   ├── remote/GeminiApiClient.kt # Ktor SSE streaming client
            │   └── repository/LuminaRepository.kt # State repository
            ├── viewmodel/
            │   └── LuminaViewModel.kt # Main ViewModel & UI State Machine
            └── ui/
                ├── LuminaApp.kt       # Root composable with view routing
                ├── theme/             # Color.kt, Type.kt, Theme.kt
                ├── components/        # GlassCard, GlassButton, AudioWaveformIndicator
                └── screens/
                    ├── ProfileSelectorScreen.kt # Daftar karakter & quick call
                    ├── ChatScreen.kt            # Chat real-time dengan streaming bubble
                    ├── CallScreen.kt            # Audio call visualizer berdenyut
                    ├── CharacterCardsScreen.kt  # Galeri kartu karakter visual
                    ├── CharacterWizardScreen.kt # Generator karakter berbasis AI
                    ├── SettingsDialog.kt        # Pengaturan tema, aksen & API Key
                    └── UserProfileDialog.kt     # Pengaturan profil pengguna
```

### Cara Menjalankan di Android Studio:
1. Buka folder root proyek di Android Studio (Koala / Ladybug / Meerkat atau yang lebih baru).
2. Sinkronkan Gradle (`Sync Project with Gradle Files`).
3. Tambahkan `GEMINI_API_KEY=your_key` pada file `.env`.
4. Jalankan pada emulator Android (API 26+) atau perangkat fisik via `Run 'app'`.

---

## 🌐 Web App (Preview & Development)
1. Pasang dependensi: `npm install`
2. Jalankan server: `npm run dev`
