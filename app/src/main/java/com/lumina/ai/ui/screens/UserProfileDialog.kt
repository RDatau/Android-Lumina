package com.lumina.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.lumina.ai.data.model.UserProfile
import com.lumina.ai.ui.components.GlassButton
import com.lumina.ai.ui.components.GlassCard
import com.lumina.ai.ui.theme.LocalAccentColor
import com.lumina.ai.ui.theme.LocalIsDarkTheme

@Composable
fun UserProfileDialog(
    isOpen: Boolean,
    profile: UserProfile,
    onClose: () -> Unit,
    onSaveProfile: (UserProfile) -> Unit
) {
    if (!isOpen) return

    val accent = LocalAccentColor.current
    val isDark = LocalIsDarkTheme.current

    var tempName by remember(profile) { mutableStateOf(profile.name) }
    var tempPersonalityInfo by remember(profile) { mutableStateOf(profile.personalityInfo) }

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
                        text = "Profil Pengguna",
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

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Icon
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Nama Panggilan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color(0xFF0A0F14)
                )
                Spacer(modifier = Modifier.height(6.dp))
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                        BasicTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = if (isDark) Color.White else Color(0xFF0A0F14),
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(accent),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Tentang Kamu / Catatan untuk AI",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color(0xFF0A0F14)
                )
                Text(
                    text = "AI akan mengingat hobi, kesukaan, dan caramu berbicara berdasarkan info ini.",
                    fontSize = 11.sp,
                    color = if (isDark) Color(0x99FFFFFF) else Color(0x990A0F14),
                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                )
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(14.dp)) {
                        BasicTextField(
                            value = tempPersonalityInfo,
                            onValueChange = { tempPersonalityInfo = it },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = if (isDark) Color.White else Color(0xFF0A0F14),
                                fontSize = 13.sp
                            ),
                            cursorBrush = SolidColor(accent),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                GlassButton(
                    onClick = {
                        onSaveProfile(
                            profile.copy(
                                name = tempName,
                                personalityInfo = tempPersonalityInfo
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
                    Text("Simpan Profil", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
