package com.lumina.ai.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.lumina.ai.data.model.AgentConfig
import com.lumina.ai.data.model.ChatMessage
import com.lumina.ai.ui.components.AudioWaveformIndicator
import com.lumina.ai.ui.components.GlassCard
import com.lumina.ai.ui.theme.EmeraldSuccess
import com.lumina.ai.ui.theme.GlassBorderDark
import com.lumina.ai.ui.theme.LocalAccentColor
import com.lumina.ai.ui.theme.LocalIsDarkTheme

@Composable
fun ChatScreen(
    agent: AgentConfig,
    messages: List<ChatMessage>,
    inputText: String,
    isStreaming: Boolean,
    onInputChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onStartCall: () -> Unit,
    onBack: () -> Unit
) {
    val accent = LocalAccentColor.current
    val isDark = LocalIsDarkTheme.current
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, isStreaming) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0A0F14) else Color(0xFFF8F9FA))
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Chat Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xCC0F141C) else Color(0xEEFFFFFF))
                    .border(0.5.dp, if (isDark) GlassBorderDark else Color(0x11000000))
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = if (isDark) Color.White else Color(0xFF0A0F14)
                        )
                    }

                    // Avatar
                    Box(modifier = Modifier.size(42.dp)) {
                        val pic = agent.profilePic
                        if (!pic.isNullOrBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(pic),
                                contentDescription = agent.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(1.5.dp, accent, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(accent.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = agent.name.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(EmeraldSuccess)
                                .border(1.5.dp, if (isDark) Color(0xFF0A0F14) else Color.White, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = agent.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0A0F14)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isStreaming) {
                                AudioWaveformIndicator(active = true, barColor = accent)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Mengetik...",
                                    fontSize = 11.sp,
                                    color = accent,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = "Aktif sekarang",
                                    fontSize = 11.sp,
                                    color = if (isDark) Color(0x99FFFFFF) else Color(0x990A0F14)
                                )
                            }
                        }
                    }

                    // Voice Call Icon Button
                    IconButton(onClick = onStartCall) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.2f))
                                .border(1.dp, accent.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Panggilan Suara",
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Message Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isUser = msg.role == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        if (isUser) {
                            // User Message Bubble
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.82f)
                                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(accent, accent.copy(alpha = 0.85f))
                                        )
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        } else {
                            // Agent Message Bubble (Glassmorphism)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                                    .background(if (isDark) Color(0xB3181E27) else Color(0xEEFFFFFF))
                                    .border(1.dp, if (isDark) GlassBorderDark else Color(0x1A000000), RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Column {
                                    if (msg.text.isNotEmpty()) {
                                        Text(
                                            text = msg.text,
                                            color = if (isDark) Color(0xFFF3F4F6) else Color(0xFF111827),
                                            fontSize = 14.sp,
                                            lineHeight = 21.sp
                                        )
                                    } else if (isStreaming) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                strokeWidth = 2.dp,
                                                color = accent
                                            )
                                            Text(
                                                text = "Sedang merangkai kata...",
                                                color = if (isDark) Color(0x99FFFFFF) else Color(0x990A0F14),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    // Image attachment if available
                                    if (!msg.image.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Image(
                                            painter = rememberAsyncImagePainter(msg.image),
                                            contentDescription = "Attachment",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Input Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xEE0A0F14) else Color(0xF8FFFFFF))
                    .border(0.5.dp, if (isDark) GlassBorderDark else Color(0x11000000))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Message Text Field Card
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (inputText.isEmpty()) {
                                    Text(
                                        text = "Ketik pesan untuk ${agent.name}...",
                                        fontSize = 14.sp,
                                        color = if (isDark) Color(0x66FFFFFF) else Color(0x660A0F14)
                                    )
                                }
                                BasicTextField(
                                    value = inputText,
                                    onValueChange = onInputChanged,
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

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank() && !isStreaming) accent
                                else accent.copy(alpha = 0.4f)
                            )
                            .clickable(
                                enabled = inputText.isNotBlank() && !isStreaming,
                                onClick = onSendMessage
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
