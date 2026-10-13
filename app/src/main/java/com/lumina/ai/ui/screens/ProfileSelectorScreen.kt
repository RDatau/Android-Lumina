package com.lumina.ai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.lumina.ai.data.model.AgentConfig
import com.lumina.ai.data.model.ChatMessage
import com.lumina.ai.ui.components.GlassButton
import com.lumina.ai.ui.components.GlassCard
import com.lumina.ai.ui.theme.EmeraldSuccess
import com.lumina.ai.ui.theme.GlassBorderDark
import com.lumina.ai.ui.theme.LocalAccentColor
import com.lumina.ai.ui.theme.LocalIsDarkTheme

@Composable
fun ProfileSelectorScreen(
    profiles: List<AgentConfig>,
    onSelectAgent: (AgentConfig) -> Unit,
    onStartCall: (AgentConfig) -> Unit,
    onOpenWizard: () -> Unit,
    onOpenCardsView: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenUserProfile: () -> Unit,
    lastMessagesMap: Map<String, List<ChatMessage>> = emptyMap()
) {
    val accent = LocalAccentColor.current
    val isDark = LocalIsDarkTheme.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredProfiles = remember(profiles, searchQuery) {
        if (searchQuery.isBlank()) profiles
        else profiles.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.personality.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0A0F14) else Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Lumina",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0A0F14)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accent.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = accent
                            )
                        }
                    }
                    Text(
                        text = "Teman ngobrol yang fleksibel & seru",
                        fontSize = 12.sp,
                        color = if (isDark) Color(0x99FFFFFF) else Color(0x990A0F14)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onOpenCardsView) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Visual Cards",
                            tint = if (isDark) Color.White else Color(0xFF0A0F14)
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (isDark) Color.White else Color(0xFF0A0F14)
                        )
                    }
                    IconButton(onClick = onOpenUserProfile) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.25f))
                                .border(1.dp, accent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (isDark) Color(0x80FFFFFF) else Color(0x800A0F14),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Cari teman atau karakter...",
                                fontSize = 14.sp,
                                color = if (isDark) Color(0x66FFFFFF) else Color(0x660A0F14)
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = if (isDark) Color.White else Color(0xFF0A0F14),
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(accent),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Character list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredProfiles, key = { it.id }) { agent ->
                    val lastMsg = lastMessagesMap[agent.id]?.lastOrNull()
                    val snippet = lastMsg?.text ?: agent.personality

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAgent(agent) },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier.size(54.dp)
                            ) {
                                val pic = agent.profilePic
                                if (!pic.isNullOrBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(pic),
                                        contentDescription = agent.name,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(1.5.dp, accent.copy(alpha = 0.5f), CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(accent.copy(alpha = 0.25f))
                                            .border(1.5.dp, accent, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = agent.name.take(1).uppercase(),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                // Online badge
                                Box(
                                    modifier = Modifier
                                        .size(13.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                        .border(2.dp, if (isDark) Color(0xFF0A0F14) else Color.White, CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Name & Snippet
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = agent.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) Color.White else Color(0xFF0A0F14)
                                    )
                                    Text(
                                        text = "Online",
                                        fontSize = 11.sp,
                                        color = EmeraldSuccess,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = snippet,
                                    fontSize = 13.sp,
                                    color = if (isDark) Color(0x99FFFFFF) else Color(0x990A0F14),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Quick Call Button
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(accent.copy(alpha = 0.15f))
                                    .border(1.dp, accent.copy(alpha = 0.4f), CircleShape)
                                    .clickable { onStartCall(agent) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Panggil",
                                    tint = accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        // Floating Action Button to create character
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            GlassButton(
                onClick = onOpenWizard,
                isPrimary = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Buat Karakter",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Buat Karakter",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
