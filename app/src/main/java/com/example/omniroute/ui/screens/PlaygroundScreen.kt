package com.example.omniroute.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omniroute.data.model.ChatMessage
import com.example.omniroute.ui.OmniRouteViewModel
import com.example.omniroute.ui.theme.*

@Composable
fun PlaygroundScreen(
    viewModel: OmniRouteViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val activeCombo by viewModel.activeCombo.collectAsState()
    val promptInput by viewModel.promptInput.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val simulate429 by viewModel.simulate429OnPrimary.collectAsState()
    val compressionEnabled by viewModel.compressionEnabled.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OmniBg)
    ) {
        // Top Playground Bar: Active Combo, Compression Toggle, 429 Toggle, Clear
        Surface(
            color = OmniSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, OmniBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gateway Playground", color = OmniTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Text("Active: ${activeCombo?.name ?: "Auto Router"}", color = OmniCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.clearChat() }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Chat", tint = OmniTextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Feature pills: Compression & 429 simulation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // RTK Compression Pill
                    Surface(
                        color = if (compressionEnabled) OmniGreen.copy(alpha = 0.15f) else OmniSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (compressionEnabled) OmniGreen.copy(alpha = 0.5f) else OmniBorder
                        ),
                        modifier = Modifier.testTag("toggle_compression_pill")
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { viewModel.toggleCompression() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Compress,
                                contentDescription = null,
                                tint = if (compressionEnabled) OmniGreen else OmniTextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (compressionEnabled) "RTK Compression ON (-89%)" else "RTK Compression OFF",
                                color = if (compressionEnabled) OmniGreen else OmniTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 429 Failover Simulation Pill
                    Surface(
                        color = if (simulate429) OmniRed.copy(alpha = 0.15f) else OmniSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (simulate429) OmniRed.copy(alpha = 0.5f) else OmniBorder
                        ),
                        modifier = Modifier.testTag("toggle_429_pill")
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { viewModel.toggleSimulate429() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (simulate429) OmniRed else OmniTextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (simulate429) "Simulate 429 Rate Limit: ON" else "Simulate 429: OFF",
                                color = if (simulate429) OmniRed else OmniTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Chat message history
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageBubble(message = message)
            }
        }

        // Quick prompts suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val suggestions = listOf(
                "Explain OmniRoute 13-factor combo routing",
                "Show Kotlin code for proxy client",
                "How does RTK Caveman compression save tokens?",
                "Simulate failover test"
            )
            suggestions.forEach { text ->
                Surface(
                    color = OmniSurfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OmniBorder)
                ) {
                    Text(
                        text = text,
                        color = OmniTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .clickable { viewModel.setPromptInput(text) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Input bar
        Surface(
            color = OmniSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, OmniBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { viewModel.setPromptInput(it) },
                    placeholder = { Text("Ask anything or test router fallback...", color = OmniTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OmniTextPrimary,
                        unfocusedTextColor = OmniTextPrimary,
                        focusedBorderColor = OmniCyan,
                        unfocusedBorderColor = OmniBorder,
                        focusedContainerColor = OmniSurfaceVariant,
                        unfocusedContainerColor = OmniSurfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 4,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_prompt_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.sendPrompt() },
                    enabled = promptInput.isNotBlank() && !isGenerating,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = OmniCyan,
                        contentColor = OmniBg,
                        disabledContainerColor = OmniSurfaceVariant,
                        disabledContentColor = OmniTextMuted
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .testTag("btn_send_prompt")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = OmniCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Send Message", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Sender info & compression ratio for user
        if (isUser && message.originalTokens != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Prompt: ${message.originalTokens} tok → ${message.compressedTokens} tok (-${message.compressionRatio?.toInt()}%)",
                    color = OmniGreen,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
        }

        Surface(
            color = if (isUser) OmniCyan.copy(alpha = 0.15f) else OmniSurface,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) OmniCyan.copy(alpha = 0.4f) else if (message.fallbackOccurred) OmniRed.copy(alpha = 0.5f) else OmniBorder
            ),
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Failover banner if fallback triggered
                if (message.fallbackOccurred && message.fallbackReason != null) {
                    Surface(
                        color = OmniRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = OmniRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                message.fallbackReason,
                                color = OmniRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = message.content,
                    color = OmniTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Assistant telemetry footer: Provider, Model, Latency, Speed
                if (!isUser && message.providerUsed != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = OmniBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(message.providerUsed, color = OmniCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(" · ${message.modelUsed}", color = OmniTextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }

                        if (message.latencyMs != null) {
                            Text(
                                "${message.latencyMs}ms (${message.tokensPerSec?.toInt() ?: 0} tps)",
                                color = OmniTextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
