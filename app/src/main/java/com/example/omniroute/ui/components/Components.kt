package com.example.omniroute.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omniroute.data.model.*
import com.example.omniroute.ui.theme.*

@Composable
fun OmniCard(
    modifier: Modifier = Modifier,
    borderColor: Color = OmniBorder,
    glowColor: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OmniSurface)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(borderColor.copy(alpha = 0.8f), borderColor.copy(alpha = 0.2f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun CircuitBadge(state: CircuitState, modifier: Modifier = Modifier) {
    val (bgColor, textColor, icon) = when (state) {
        CircuitState.CLOSED -> Triple(OmniGreen.copy(alpha = 0.15f), OmniGreen, Icons.Default.CheckCircle)
        CircuitState.HALF_OPEN -> Triple(OmniYellow.copy(alpha = 0.15f), OmniYellow, Icons.Default.Sync)
        CircuitState.OPEN -> Triple(OmniRed.copy(alpha = 0.15f), OmniRed, Icons.Default.Warning)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = when (state) {
                CircuitState.CLOSED -> "CLOSED"
                CircuitState.HALF_OPEN -> "HALF-OPEN"
                CircuitState.OPEN -> "OPEN (TRIPPED)"
            },
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    accentColor: Color = OmniCyan,
    subValue: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(OmniSurfaceVariant)
            .padding(12.dp)
    ) {
        Text(title, color = OmniTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            color = accentColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        if (subValue != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(subValue, color = OmniTextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun EditApiKeyDialog(
    provider: Provider,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var keyText by remember { mutableStateOf(provider.apiKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OmniSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VpnKey, contentDescription = null, tint = OmniCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Configure ${provider.name}", color = OmniTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    "Enter API Key or Bearer Token for ${provider.name}. All keys are encrypted at rest locally.",
                    color = OmniTextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = keyText,
                    onValueChange = { keyText = it },
                    label = { Text("API Key / Bearer Token") },
                    placeholder = { Text("sk-or-v1-...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OmniTextPrimary,
                        unfocusedTextColor = OmniTextPrimary,
                        focusedBorderColor = OmniCyan,
                        unfocusedBorderColor = OmniBorder,
                        focusedLabelColor = OmniCyan,
                        unfocusedLabelColor = OmniTextMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Free tier limit: ${provider.freeTokensQuota}",
                    color = OmniGreen,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(keyText.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = OmniCyan, contentColor = OmniBg)
            ) {
                Text("Save Key", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OmniTextMuted)
            }
        }
    )
}

@Composable
fun CreateComboDialog(
    providers: List<Provider>,
    onDismiss: () -> Unit,
    onCreate: (String, String, ComboStrategy, List<ComboTarget>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedStrategy by remember { mutableStateOf(ComboStrategy.AUTO) }
    var expandedStrategy by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OmniSurface,
        title = {
            Text("Create Custom Combo", color = OmniTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Combo Name") },
                    placeholder = { Text("e.g. My Fast Combo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    placeholder = { Text("Fallback from model A to model B") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Routing Strategy", color = OmniTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box {
                    OutlinedButton(
                        onClick = { expandedStrategy = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OmniCyan)
                    ) {
                        Text(selectedStrategy.label)
                    }
                    DropdownMenu(
                        expanded = expandedStrategy,
                        onDismissRequest = { expandedStrategy = false },
                        modifier = Modifier.background(OmniSurface)
                    ) {
                        ComboStrategy.values().forEach { strategy ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(strategy.label, color = OmniTextPrimary, fontWeight = FontWeight.Bold)
                                        Text(strategy.shortDesc, color = OmniTextMuted, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    selectedStrategy = strategy
                                    expandedStrategy = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val targets = listOf(
                            ComboTarget("gemini", "Google Gemini", "gemini-2.5-flash", priority = 1),
                            ComboTarget("groq", "Groq LPU", "llama-3.3-70b-versatile", priority = 2)
                        )
                        onCreate(name, desc, selectedStrategy, targets)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OmniPurple, contentColor = Color.White)
            ) {
                Text("Create Combo", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OmniTextMuted)
            }
        }
    )
}

@Composable
fun TelemetryDetailDialog(
    log: TelemetryLog,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OmniSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Analytics, contentDescription = null, tint = OmniCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Telemetry Request Trace", color = OmniTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(log.endpoint, color = OmniCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Surface(
                        color = if (log.statusCode == 200) OmniGreen.copy(alpha = 0.2f) else OmniRed.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${log.statusCode} OK",
                            color = if (log.statusCode == 200) OmniGreen else OmniRed,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = OmniBorder)
                Spacer(modifier = Modifier.height(12.dp))

                TraceRow("Target Model", log.model)
                TraceRow("Dispatched Provider", log.provider)
                TraceRow("Combo Strategy", log.strategy)
                TraceRow("Upstream Latency", "${log.latencyMs} ms")
                TraceRow("Prompt Tokens", "${log.promptTokens} tok")
                TraceRow("Completion Tokens", "${log.completionTokens} tok")
                TraceRow("RTK Tokens Saved", "${log.savedTokens} tok (-${if (log.promptTokens > 0) ((log.savedTokens * 100) / (log.promptTokens + log.savedTokens)) else 0}%)")
                TraceRow("Circuit State", log.circuitBreaker.name)
                TraceRow("Auto-Fallback Triggered", if (log.fallbackTriggered) "YES (429 -> Failover)" else "NO (Direct hit)")
                TraceRow("Client Agent", log.clientAgent)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = OmniCyan, contentColor = OmniBg)) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun TraceRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = OmniTextMuted, fontSize = 12.sp)
        Text(value, color = OmniTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
    }
}
