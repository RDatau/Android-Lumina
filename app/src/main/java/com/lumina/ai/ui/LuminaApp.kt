package com.lumina.ai.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumina.ai.data.model.AppState
import com.lumina.ai.ui.screens.CallScreen
import com.lumina.ai.ui.screens.CharacterCardsScreen
import com.lumina.ai.ui.screens.CharacterWizardScreen
import com.lumina.ai.ui.screens.ChatScreen
import com.lumina.ai.ui.screens.ProfileSelectorScreen
import com.lumina.ai.ui.screens.SettingsDialog
import com.lumina.ai.ui.screens.UserProfileDialog
import com.lumina.ai.ui.theme.LuminaTheme
import com.lumina.ai.viewmodel.LuminaViewModel

@Composable
fun LuminaApp(
    viewModel: LuminaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val agents by viewModel.agents.collectAsState()
    val appearance by viewModel.appearance.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    LuminaTheme(
        darkTheme = appearance.isBackgroundDark,
        accentColorHex = appearance.accentColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(
                targetState = uiState.currentAppState,
                label = "navigation_crossfade"
            ) { state ->
                when (state) {
                    AppState.PROFILE_SELECT, AppState.LOGIN, AppState.SETUP -> {
                        ProfileSelectorScreen(
                            profiles = agents,
                            onSelectAgent = { viewModel.selectAgent(it) },
                            onStartCall = {
                                viewModel.selectAgent(it)
                                viewModel.startCall()
                            },
                            onOpenWizard = { viewModel.navigateTo(AppState.WIZARD) },
                            onOpenCardsView = { viewModel.navigateTo(AppState.CHARACTER_CARDS) },
                            onOpenSettings = { viewModel.openSettings() },
                            onOpenUserProfile = { viewModel.openUserProfile() }
                        )
                    }

                    AppState.CHAT -> {
                        val currentAgent = uiState.selectedAgent ?: agents.firstOrNull()
                        if (currentAgent != null) {
                            ChatScreen(
                                agent = currentAgent,
                                messages = uiState.currentMessages,
                                inputText = uiState.inputText,
                                isStreaming = uiState.isStreaming,
                                onInputChanged = { viewModel.onInputTextChanged(it) },
                                onSendMessage = { viewModel.sendMessage() },
                                onStartCall = { viewModel.startCall() },
                                onBack = { viewModel.navigateTo(AppState.PROFILE_SELECT) }
                            )
                        } else {
                            viewModel.navigateTo(AppState.PROFILE_SELECT)
                        }
                    }

                    AppState.CALL -> {
                        val currentAgent = uiState.selectedAgent ?: agents.firstOrNull()
                        if (currentAgent != null) {
                            CallScreen(
                                agent = currentAgent,
                                callStatus = uiState.callStatus,
                                callDurationSeconds = uiState.callDurationSeconds,
                                isMuted = uiState.isMuted,
                                onToggleMute = { viewModel.toggleMute() },
                                onEndCall = { viewModel.endCall() }
                            )
                        } else {
                            viewModel.navigateTo(AppState.PROFILE_SELECT)
                        }
                    }

                    AppState.CHARACTER_CARDS -> {
                        CharacterCardsScreen(
                            profiles = agents,
                            onSelectAgent = { viewModel.selectAgent(it) },
                            onStartCall = {
                                viewModel.selectAgent(it)
                                viewModel.startCall()
                            },
                            onOpenWizard = { viewModel.navigateTo(AppState.WIZARD) },
                            onBack = { viewModel.navigateTo(AppState.PROFILE_SELECT) }
                        )
                    }

                    AppState.WIZARD -> {
                        CharacterWizardScreen(
                            prompt = uiState.wizardPrompt,
                            isGenerating = uiState.isGeneratingWizard,
                            generatedName = uiState.generatedWizardName,
                            generatedPersona = uiState.generatedWizardPersona,
                            profilePic = uiState.wizardProfilePic,
                            onPromptChanged = { viewModel.onWizardPromptChanged(it) },
                            onGenerate = { viewModel.generateWizardCharacter() },
                            onSave = { viewModel.saveWizardCharacter() },
                            onBack = { viewModel.navigateTo(AppState.PROFILE_SELECT) }
                        )
                    }

                    else -> {
                        ProfileSelectorScreen(
                            profiles = agents,
                            onSelectAgent = { viewModel.selectAgent(it) },
                            onStartCall = {
                                viewModel.selectAgent(it)
                                viewModel.startCall()
                            },
                            onOpenWizard = { viewModel.navigateTo(AppState.WIZARD) },
                            onOpenCardsView = { viewModel.navigateTo(AppState.CHARACTER_CARDS) },
                            onOpenSettings = { viewModel.openSettings() },
                            onOpenUserProfile = { viewModel.openUserProfile() }
                        )
                    }
                }
            }

            // Settings Dialog
            SettingsDialog(
                isOpen = uiState.isSettingsOpen,
                appearance = appearance,
                userProfile = userProfile,
                onClose = { viewModel.closeSettings() },
                onSaveAppearance = { viewModel.updateAppearance(it) },
                onSaveUserProfile = { viewModel.updateUserProfile(it) }
            )

            // User Profile Dialog
            UserProfileDialog(
                isOpen = uiState.isUserProfileOpen,
                profile = userProfile,
                onClose = { viewModel.closeUserProfile() },
                onSaveProfile = { viewModel.updateUserProfile(it) }
            )

            // Notification Toast
            uiState.toastMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xDD000000))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(text = msg, color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}
