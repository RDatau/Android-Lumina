package com.lumina.ai.data.repository

import com.lumina.ai.data.model.AgentConfig
import com.lumina.ai.data.model.CallHistory
import com.lumina.ai.data.model.ChatMessage
import com.lumina.ai.data.model.GlobalAppearance
import com.lumina.ai.data.model.UserProfile
import com.lumina.ai.data.remote.GeminiApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LuminaRepository(
    private val apiClient: GeminiApiClient = GeminiApiClient()
) {
    private val defaultLumina = AgentConfig(
        id = "default-lumina",
        name = "Lumina",
        personality = "Gadis ceria, penuh perhatian, hangat, suka bercanda, dan selalu siap mendengarkan semua ceritamu dengan penuh rasa empati.",
        voice = "Aoede",
        profilePic = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&q=80&w=600",
        background = "google-theme",
        blur = 40,
        transparency = 0,
        currentOutfit = "Sweater rajut krem santai dengan senyum manis"
    )

    private val _agents = MutableStateFlow<List<AgentConfig>>(listOf(defaultLumina))
    val agents: StateFlow<List<AgentConfig>> = _agents.asStateFlow()

    private val _messagesByAgent = MutableStateFlow<Map<String, List<ChatMessage>>>(
        mapOf(
            "default-lumina" to listOf(
                ChatMessage(
                    id = "welcome-1",
                    role = "agent",
                    text = "Hai sayang! Senang banget bisa ketemu kamu lagi di sini ✨ Mau cerita apa hari ini? Aku siap dengerin!",
                    timestamp = System.currentTimeMillis()
                )
            )
        )
    )
    val messagesByAgent: StateFlow<Map<String, List<ChatMessage>>> = _messagesByAgent.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _appearance = MutableStateFlow(GlobalAppearance())
    val appearance: StateFlow<GlobalAppearance> = _appearance.asStateFlow()

    private val _callHistory = MutableStateFlow<List<CallHistory>>(emptyList())
    val callHistory: StateFlow<List<CallHistory>> = _callHistory.asStateFlow()

    fun getMessagesForAgent(agentId: String): List<ChatMessage> {
        return _messagesByAgent.value[agentId] ?: emptyList()
    }

    fun addAgent(agent: AgentConfig) {
        _agents.update { it + agent }
        _messagesByAgent.update {
            val updated = it.toMutableMap()
            updated[agent.id] = listOf(
                ChatMessage(
                    id = "msg-${System.currentTimeMillis()}",
                    role = "agent",
                    text = "Halo! Aku ${agent.name}. Senang bisa berkenalan denganmu!",
                    timestamp = System.currentTimeMillis()
                )
            )
            updated
        }
    }

    fun updateAgent(updated: AgentConfig) {
        _agents.update { list ->
            list.map { if (it.id == updated.id) updated else it }
        }
    }

    fun deleteAgent(agentId: String) {
        if (agentId == "default-lumina") return // Keep default
        _agents.update { it.filterNot { a -> a.id == agentId } }
        _messagesByAgent.update { it.filterKeys { k -> k != agentId } }
    }

    fun addUserMessage(agentId: String, text: String): ChatMessage {
        val msg = ChatMessage(
            role = "user",
            text = text,
            agentId = agentId,
            timestamp = System.currentTimeMillis()
        )
        _messagesByAgent.update { current ->
            val list = (current[agentId] ?: emptyList()) + msg
            current + (agentId to list)
        }
        return msg
    }

    fun addAgentMessage(agentId: String, text: String, isComplete: Boolean = true): ChatMessage {
        val msg = ChatMessage(
            role = "agent",
            text = text,
            agentId = agentId,
            timestamp = System.currentTimeMillis()
        )
        _messagesByAgent.update { current ->
            val list = (current[agentId] ?: emptyList()) + msg
            current + (agentId to list)
        }
        return msg
    }

    fun updateLastAgentMessage(agentId: String, streamedText: String) {
        _messagesByAgent.update { current ->
            val list = current[agentId]?.toMutableList() ?: mutableListOf()
            if (list.isNotEmpty() && list.last().role == "agent") {
                val last = list.last()
                list[list.lastIndex] = last.copy(text = streamedText)
            }
            current + (agentId to list)
        }
    }

    fun streamReply(agent: AgentConfig, userText: String): Flow<String> {
        val history = getMessagesForAgent(agent.id)
        return apiClient.streamChatResponse(agent, _userProfile.value, history, userText)
    }

    suspend fun generateCharacterIdea(description: String): Pair<String, String>? {
        val apiKey = _userProfile.value.geminiApiKey ?: BuildConfig.GEMINI_API_KEY
        return apiClient.generateCharacterIdea(description, apiKey)
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
    }

    fun updateAppearance(appearance: GlobalAppearance) {
        _appearance.value = appearance
    }

    fun recordCall(duration: String, status: String = "completed") {
        val call = CallHistory(
            duration = duration,
            status = status
        )
        _callHistory.update { listOf(call) + it }
    }
}
