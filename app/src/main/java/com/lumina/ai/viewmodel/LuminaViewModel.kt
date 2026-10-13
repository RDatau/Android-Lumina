package com.lumina.ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumina.ai.data.model.AgentConfig
import com.lumina.ai.data.model.AppState
import com.lumina.ai.data.model.ChatMessage
import com.lumina.ai.data.model.GlobalAppearance
import com.lumina.ai.data.model.UserProfile
import com.lumina.ai.data.repository.LuminaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LuminaUiState(
    val currentAppState: AppState = AppState.PROFILE_SELECT,
    val selectedAgent: AgentConfig? = null,
    val currentMessages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isStreaming: Boolean = false,
    val streamingBuffer: String = "",
    // Call State
    val isCallActive: Boolean = false,
    val callDurationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val callStatus: String = "CONNECTING...",
    val micActivity: Float = 0f,
    val agentSpeechActivity: Float = 0f,
    // Wizard State
    val wizardPrompt: String = "",
    val isGeneratingWizard: Boolean = false,
    val generatedWizardName: String = "",
    val generatedWizardPersona: String = "",
    val wizardProfilePic: String? = null,
    // Modals
    val isSettingsOpen: Boolean = false,
    val isUserProfileOpen: Boolean = false,
    val toastMessage: String? = null
)

class LuminaViewModel(
    private val repository: LuminaRepository = LuminaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LuminaUiState())
    val uiState: StateFlow<LuminaUiState> = _uiState.asStateFlow()

    val agents = repository.agents
    val userProfile = repository.userProfile
    val appearance = repository.appearance

    private var callTimerJob: Job? = null
    private var streamingJob: Job? = null

    init {
        // Default to first agent if needed
        viewModelScope.launch {
            agents.collect { list ->
                if (_uiState.value.selectedAgent == null && list.isNotEmpty()) {
                    _uiState.update { it.copy(selectedAgent = list.first()) }
                }
            }
        }
        viewModelScope.launch {
            repository.messagesByAgent.collect { map ->
                val agentId = _uiState.value.selectedAgent?.id ?: return@collect
                _uiState.update { it.copy(currentMessages = map[agentId] ?: emptyList()) }
            }
        }
    }

    fun navigateTo(state: AppState) {
        _uiState.update { it.copy(currentAppState = state) }
    }

    fun selectAgent(agent: AgentConfig) {
        val msgs = repository.getMessagesForAgent(agent.id)
        _uiState.update {
            it.copy(
                selectedAgent = agent,
                currentMessages = msgs,
                currentAppState = AppState.CHAT
            )
        }
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val agent = _uiState.value.selectedAgent ?: return
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isStreaming) return

        _uiState.update { it.copy(inputText = "", isStreaming = true, streamingBuffer = "") }
        repository.addUserMessage(agent.id, text)

        streamingJob?.cancel()
        streamingJob = viewModelScope.launch {
            // Placeholder empty agent response to be filled by stream
            repository.addAgentMessage(agent.id, "", isComplete = false)
            val buffer = StringBuilder()

            repository.streamReply(agent, text).collect { chunk ->
                buffer.append(chunk)
                _uiState.update { it.copy(streamingBuffer = buffer.toString()) }
                repository.updateLastAgentMessage(agent.id, buffer.toString())
            }

            _uiState.update { it.copy(isStreaming = false, streamingBuffer = "") }
        }
    }

    fun startCall() {
        val agent = _uiState.value.selectedAgent ?: return
        _uiState.update {
            it.copy(
                currentAppState = AppState.CALL,
                isCallActive = true,
                callDurationSeconds = 0,
                callStatus = "CONNECTING...",
                isMuted = false
            )
        }

        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            delay(1200)
            _uiState.update { it.copy(callStatus = "TERHUBUNG") }
            delay(800)
            _uiState.update { it.copy(callStatus = "SEDANG BERBICARA...") }

            while (true) {
                delay(1000)
                _uiState.update {
                    val nextDuration = it.callDurationSeconds + 1
                    // Simulated waveform pulsation
                    val simulatedSpeech = if (nextDuration % 6 < 3) 0.85f else 0.15f
                    it.copy(
                        callDurationSeconds = nextDuration,
                        agentSpeechActivity = simulatedSpeech,
                        callStatus = if (simulatedSpeech > 0.5f) "SEDANG BERBICARA..." else "MENDENGARKAN..."
                    )
                }
            }
        }
    }

    fun toggleMute() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
    }

    fun endCall() {
        callTimerJob?.cancel()
        val durationSec = _uiState.value.callDurationSeconds
        val formattedDuration = String.format("%02d:%02d", durationSec / 60, durationSec % 60)
        repository.recordCall(formattedDuration)

        _uiState.update {
            it.copy(
                isCallActive = false,
                currentAppState = AppState.CHAT,
                callStatus = "PANGGILAN BERAKHIR"
            )
        }
    }

    fun onWizardPromptChanged(prompt: String) {
        _uiState.update { it.copy(wizardPrompt = prompt) }
    }

    fun generateWizardCharacter() {
        val prompt = _uiState.value.wizardPrompt.trim()
        if (prompt.isEmpty() || _uiState.value.isGeneratingWizard) return

        _uiState.update { it.copy(isGeneratingWizard = true) }
        viewModelScope.launch {
            val idea = repository.generateCharacterIdea(prompt)
            if (idea != null) {
                _uiState.update {
                    it.copy(
                        isGeneratingWizard = false,
                        generatedWizardName = idea.first,
                        generatedWizardPersona = idea.second,
                        wizardProfilePic = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=600"
                    )
                }
            } else {
                _uiState.update { it.copy(isGeneratingWizard = false) }
            }
        }
    }

    fun saveWizardCharacter() {
        val name = _uiState.value.generatedWizardName.ifBlank { "Teman Baru" }
        val persona = _uiState.value.generatedWizardPersona.ifBlank { _uiState.value.wizardPrompt }
        val newAgent = AgentConfig(
            id = "agent-${System.currentTimeMillis()}",
            name = name,
            personality = persona,
            profilePic = _uiState.value.wizardProfilePic
        )
        repository.addAgent(newAgent)
        selectAgent(newAgent)
    }

    fun openSettings() {
        _uiState.update { it.copy(isSettingsOpen = true) }
    }

    fun closeSettings() {
        _uiState.update { it.copy(isSettingsOpen = false) }
    }

    fun openUserProfile() {
        _uiState.update { it.copy(isUserProfileOpen = true) }
    }

    fun closeUserProfile() {
        _uiState.update { it.copy(isUserProfileOpen = false) }
    }

    fun updateAppearance(appearance: GlobalAppearance) {
        repository.updateAppearance(appearance)
    }

    fun updateUserProfile(profile: UserProfile) {
        repository.updateUserProfile(profile)
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(toastMessage = msg) }
            delay(2500)
            _uiState.update { it.copy(toastMessage = null) }
        }
    }
}
