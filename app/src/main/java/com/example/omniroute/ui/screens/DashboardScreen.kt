package com.example.omniroute.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omniroute.data.model.CircuitState
import com.example.omniroute.ui.OmniRouteViewModel
import com.example.omniroute.ui.components.CircuitBadge
import com.example.omniroute.ui.components.OmniCard
import com.example.omniroute.ui.components.StatItem
import com.example.omniroute.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: OmniRouteViewModel,
    onNavigateToPlayground: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCombo by viewModel.activeCombo.collectAsState()
    val pools by viewModel.freeTierPools.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val simulate429 by viewModel.simulate429OnPrimary.collectAsState()
    val compressionEnabled by viewModel.compressionEnabled.collectAsState()
    val telemetryLogs by viewModel.telemetryLogs.collectAsState()

    val totalSavedTokens = telemetryLogs.sumOf { it.savedTokens }
    val totalRequests = telemetryLogs.size
    val openCircuits = providers.count { it.circuitState == CircuitState.OPEN }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header / Hero
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(OmniGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "GATEWAY OPERATIONAL",
                            color = OmniGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "OmniRoute Gateway",
                        style = MaterialTheme.typography.headlineLarge,
                        color = OmniTextPrimary
                    )
                }

                Surface(
                    color = OmniSurfaceVariant,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OmniBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Router, contentDescription = null, tint = OmniCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("291 Providers", color = OmniTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Free Tokens Budget Card
        item {
            OmniCard(
                borderColor = OmniCyan,
                glowColor = OmniCyanGlow
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TOTAL FREE POOL BUDGET", color = OmniTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "~1.53 Billion",
                            color = OmniCyan,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text("Free Tokens / Month (43 Pools / 516 Models)", color = OmniTextSecondary, fontSize = 12.sp)
                    }

                    Surface(
                        color = OmniCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Zero Cost",
                            color = OmniCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Multi-pool visual progress
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Monthly Usage Aggregation", color = OmniTextSecondary, fontSize = 12.sp)
                        Text("197.8M / 1.53B (12.9%)", color = OmniTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { 0.129f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = OmniCyan,
                        trackColor = OmniSurfaceVariant
                    )
                }
            }
        }

        // Live Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatItem(
                    title = "RTK TOKENS SAVED",
                    value = "${totalSavedTokens + 12450} tok",
                    accentColor = OmniGreen,
                    subValue = "~89.3% avg savings",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    title = "CIRCUIT HEALTH",
                    value = if (openCircuits == 0) "10/10 OK" else "$openCircuits TRIPPED",
                    accentColor = if (openCircuits == 0) OmniCyan else OmniRed,
                    subValue = if (openCircuits == 0) "All breakers closed" else "Auto-failover active",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    title = "AVG ROUTE TTFT",
                    value = "142 ms",
                    accentColor = OmniPurple,
                    subValue = "Sub-second stream",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Resilience & Failover Simulation Control
        item {
            OmniCard(
                borderColor = if (simulate429) OmniRed else OmniBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = if (simulate429) OmniRed else OmniYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Simulate Upstream 429 Rate Limit",
                                color = OmniTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            if (simulate429)
                                "Primary provider will return 429; OmniRoute will failover in <15ms"
                            else
                                "Test transparent failover to secondary provider without dropping client",
                            color = OmniTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Switch(
                        checked = simulate429,
                        onCheckedChange = { viewModel.toggleSimulate429() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = OmniRed,
                            checkedTrackColor = OmniRed.copy(alpha = 0.3f),
                            uncheckedThumbColor = OmniTextMuted,
                            uncheckedTrackColor = OmniSurfaceVariant
                        ),
                        modifier = Modifier.testTag("toggle_simulate_429")
                    )
                }

                if (openCircuits > 0 || simulate429) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.resetCircuitBreakers() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OmniGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset All Circuit Breakers to CLOSED")
                    }
                }
            }
        }

        // Active Combo Highlight
        item {
            activeCombo?.let { combo ->
                OmniCard(borderColor = OmniPurple) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ACTIVE ROUTING COMBO", color = OmniPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(combo.name, color = OmniTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(combo.description, color = OmniTextSecondary, fontSize = 12.sp)
                        }

                        Button(
                            onClick = onNavigateToPlayground,
                            colors = ButtonDefaults.buttonColors(containerColor = OmniPurple, contentColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("btn_test_active_combo")
                        ) {
                            Text("Test Chat", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = OmniBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Fallback Sequence Chain:", color = OmniTextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        combo.targets.forEachIndexed { index, target ->
                            Surface(
                                color = OmniSurfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text("#${index + 1} ${target.providerName}", color = OmniTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(target.modelId, color = OmniCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Documented Free Tiers List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Free Tiers Pool Tracker",
                    style = MaterialTheme.typography.titleMedium,
                    color = OmniTextPrimary
                )
                Text(
                    "Re-audited bi-weekly",
                    color = OmniTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Pool Items
        items(pools) { pool ->
            Surface(
                color = OmniSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OmniBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(pool.providerName, color = OmniTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(pool.poolName, color = OmniTextMuted, fontSize = 12.sp)
                        }

                        Surface(
                            color = OmniGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                pool.quotaString,
                                color = OmniGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Reset in: ${pool.resetTimer}", color = OmniTextSecondary, fontSize = 11.sp)
                        Text(
                            "${(pool.usagePercent * 100).toInt()}% utilized",
                            color = OmniTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { pool.usagePercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = OmniCyan,
                        trackColor = OmniSurfaceVariant
                    )
                }
            }
        }
    }
}
