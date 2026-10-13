package com.lumina.ai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.lumina.ai.ui.components.GlassButton
import com.lumina.ai.ui.components.GlassCard
import com.lumina.ai.ui.theme.LocalAccentColor
import com.lumina.ai.ui.theme.LocalIsDarkTheme

@Composable
fun CharacterWizardScreen(
    prompt: String,
    isGenerating: Boolean,
    generatedName: String,
    generatedPersona: String,
    profilePic: String?,
    onPromptChanged: (String) -> Unit,
    onGenerate: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val accent = LocalAccentColor.current
    val isDark = LocalIsDarkTheme.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0A0F14) else Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = if (isDark) Color.White else Color(0xFF0A0F14)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Character Wizard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0A0F14)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Ciptakan Karakter Impianmu ✨",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF0A0F14)
            )

            Text(
                text = "Tuliskan konsep atau sifat karakter yang kamu mau, dan AI akan otomatis merancang profil lengkapnya untukmu.",
                fontSize = 13.sp,
                color = if (isDark) Color(0x99FFFFFF) else Color(0x990A0F14),
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            // Prompt Box
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.padding(14.dp)) {
                    if (prompt.isEmpty()) {
                        Text(
                            text = "Contoh: Gadis gamer tsundere yang suka ngajak mabar dan diam-diam perhatian...",
                            fontSize = 14.sp,
                            color = if (isDark) Color(0x66FFFFFF) else Color(0x660A0F14)
                        )
                    }
                    BasicTextField(
                        value = prompt,
                        onValueChange = onPromptChanged,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = if (isDark) Color.White else Color(0xFF0A0F14),
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(accent),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Generate Button
            GlassButton(
                onClick = onGenerate,
                enabled = prompt.isNotBlank() && !isGenerating,
                isPrimary = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Text("Sedang Meracik Karakter...", color = Color.White, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("Generate dengan AI", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Preview Section
            AnimatedVisibility(visible = generatedName.isNotBlank()) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Text(
                        text = "Hasil Rancangan Karakter:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF0A0F14),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!profilePic.isNullOrBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(profilePic),
                                        contentDescription = generatedName,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, accent, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(accent.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = generatedName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else Color(0xFF0A0F14)
                                    )
                                    Text(
                                        text = "Siap diajak ngobrol",
                                        fontSize = 12.sp,
                                        color = accent
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = generatedPersona,
                                fontSize = 13.sp,
                                color = if (isDark) Color(0xCCFFFFFF) else Color(0xCC0A0F14),
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            GlassButton(
                                onClick = onSave,
                                isPrimary = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text("Simpan & Mulai Chat", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
