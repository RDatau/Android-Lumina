package com.lumina.ai.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class AppState {
    LOGIN,
    SETUP,
    PROFILE_SELECT,
    CHAT,
    CALL,
    CHARACTER_CARDS,
    WIZARD,
    SETTINGS,
    USER_PROFILE
}

@Serializable
data class AgentConfig(
    val id: String = System.currentTimeMillis().toString(),
    val name: String,
    val personality: String,
    val voice: String = "Aoede",
    val voiceChat: String? = null,
    val voiceCall: String? = null,
    val profilePic: String? = null,
    val background: String = "google-theme",
    val blur: Int = 40,
    val transparency: Int = 0,
    val isEnrichPersonaEnabled: Boolean = false,
    val enrichedPersona: String? = null,
    val currentOutfit: String? = null,
    val currentAccessories: String? = null,
    val lastPapTimestamp: Long? = null,
    val imageModel: String? = null,
    val callModel: String? = null
)

@Serializable
data class Attachment(
    val name: String,
    val mimeType: String,
    val data: String, // base64
    val id: String? = null
)

@Serializable
data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val role: String, // "user" or "agent"
    val text: String,
    val image: String? = null,
    val images: List<String> = emptyList(),
    val activeImageIndex: Int = 0,
    val imagePrompt: String? = null,
    val attachments: List<Attachment> = emptyList(),
    val audio: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val outfit: String? = null,
    val userOutfit: String? = null,
    val accessories: String? = null,
    val agentId: String? = null,
    val sessionId: String? = null,
    val callTranscript: String? = null,
    val audioTitle: String? = null,
    val isError: Boolean = false,
    val errorType: String? = null
)

@Serializable
data class ChatSession(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val messages: List<ChatMessage> = emptyList(),
    val activeMessageId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class CallHistory(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val duration: String,
    val status: String // "missed" or "completed"
)

@Serializable
data class UserProfile(
    val name: String = "User",
    val personalityInfo: String = "",
    val profilePic: String? = null,
    val geminiApiKey: String? = null,
    val useGoogleSearch: Boolean = false,
    val currentOutfit: String? = null,
    val textModel: String = "gemini-2.5-flash",
    val ttsModel: String = "gemini-2.5-flash",
    val imageModel: String = "imagen-3.0-generate-002",
    val callModel: String = "gemini-2.0-flash",
    val voiceChat: String = "Aoede",
    val voiceCall: String = "Aoede",
    val isEnrichPersonaEnabled: Boolean = false,
    val enableNotificationSound: Boolean = true,
    val enableVibration: Boolean = true,
    val enableSystemNotifications: Boolean = true
)

@Serializable
data class GlobalAppearance(
    val background: String = "google-theme",
    val blur: Int = 40,
    val transparency: Int = 0,
    val isBackgroundDark: Boolean = true,
    val accentColor: String = "#6366F1",
    val accentColorMode: String = "default"
)

@Serializable
data class ActiveGenerationTask(
    val id: String,
    val agentId: String,
    val agentName: String,
    val agentPic: String? = null,
    val type: String, // "text" | "pap" | "audio"
    val statusText: String,
    val startedAt: Long = System.currentTimeMillis()
)
