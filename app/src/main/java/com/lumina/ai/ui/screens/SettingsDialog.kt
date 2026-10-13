package com.lumina.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lumina.ai.data.model.GlobalAppearance
import com.lumina.ai.data.model.UserProfile
import com.lumina.ai.ui.components.GlassButton
import com.lumina.ai.ui.components.GlassCard
import com.lumina.ai.ui.theme.LocalAccentColor
import com.lumina.ai.ui.theme.LocalIsDarkTheme

@Composable
fun SettingsDialog(
    isOpen: Boolean,
    appearance: GlobalAppearance,
    userProfile: UserProfile,
    onClose: () -> Unit,
    onSaveAppearance: (GlobalAppearance) -> Unit,
    onSaveUserProfile: (UserProfile) -> Unit
) {
    if (!isOpen) return

    val accent = LocalAccentColor.current
    val isDark = LocalIsDarkTheme.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Tampilan, 1: Gemini API

    var tempDarkTheme by remember(appearance) { mutableStateOf(appearance.isBackgroundDark) }
    var tempAccentColor by remember(appearance) { mutableStateOf(appearance.accentColor) }
    var tempApiKey by remember(userProfile) { mutableStateOf(userProfile.geminiApiKey ?: "") }
    var tempTextModel by remember(userProfile) { mutableStateOf(userProfile.textModel) }

    val presetColors = listOf("#6366F1", "#EC4899", "#8B5CF6", "#10B981", "#3B82F6", "#F59E0B")

    Dialog(onDismissRequest = onClose) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pengaturan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0A0F14)
                    )
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = if (isDark) Color.White else Color(0xFF0A0F14)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0x33FFFFFF) else Color(0x11000000))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 0) accent else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tampilan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 1) accent else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Gemini API",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (selectedTab == 0) {
                    // Dark theme toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Mode Gelap (Midnight)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color.White else Color(0xFF0A0F14)
                            )
                            Text(
                                text = "Tema gelap elegan dengan glassmorphism",
                                fontSize = 11.sp,
                                color = if (isDark) Color(0x99FFFFFF) else Color(0x990A0F14)
                            )
                        }
                        Switch(
                            checked = tempDarkTheme,
                            onCheckedChange = {
                                tempDarkTheme = it
                                onSaveAppearance(appearance.copy(isBackgroundDark = it))
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = accent)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Accent Colors
                    Text(
                        text = "Warna Aksen",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF0A0F14)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        presetColors.forEach { hex ->
                            val color = com.lumina.ai.ui.theme.parseHexColor(hex)
                            val isSelected = tempAccentColor.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (isSelected) 3.dp else 0.dp,
                                        Color.White,
                                        CircleShape
                                    )
                                    .clickable {
                                        tempAccentColor = hex
                                        onSaveAppearance(appearance.copy(accentColor = hex))
                                    }
                            )
                        }
                    }
                } else {
                    // Gemini API Key Input
                    Text(
                        text = "Gemini API Key",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF0A0F14)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = tempApiKey,
                                onValueChange = { tempApiKey = it },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = if (isDark) Color.White else Color(0xFF0A0F14),
                                    fontSize = 13.sp
                                ),
                                cursorBrush = SolidColor(accent),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Model AI Utama",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF0A0F14)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "gemini-2.5-flash (Direkomendasikan untuk kecepatan & penalaran tinggi)",
                        fontSize = 12.sp,
                        color = if (isDark) Color(0x99FFFFFF) else Color(0x990A0F14)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save button
                GlassButton(
                    onClick = {
                        onSaveUserProfile(
                            userProfile.copy(
                                geminiApiKey = tempApiKey.ifBlank { null },
                                textModel = tempTextModel
                            )
                        )
                        onSaveAppearance(
                            appearance.copy(
                                isBackgroundDark = tempDarkTheme,
                                accentColor = tempAccentColor
                            )
                        )
                        onClose()
                    },
                    isPrimary = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("Terapkan Pengaturan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
