package com.example.omniroute.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.omniroute.data.model.CircuitState
import com.example.omniroute.data.model.TelemetryLog
import com.example.omniroute.ui.OmniRouteViewModel
import com.example.omniroute.ui.components.TelemetryDetailDialog
import com.example.omniroute.ui.theme.*

@Composable
fun TrafficScreen(
    viewModel: OmniRouteViewModel,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.telemetryLogs.collectAsState()
    val selectedLog by viewModel.selectedTelemetry.collectAsState()

    selectedLog?.let { log ->
        TelemetryDetailDialog(
            log = log,
            onDismiss = { viewModel.selectTelemetry(null) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Traffic Inspector", style = MaterialTheme.typography.headlineLarge, color = OmniTextPrimary)
                Text("Live request logs, failover events & token metrics", color = OmniTextSecondary, fontSize = 13.sp)
            }

            Surface(
                color = OmniGreen.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(OmniGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LIVE LOGS", color = OmniGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Analytics, contentDescription = null, tint = OmniTextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Traffic Recorded Yet", color = OmniTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Send a prompt in the Playground to see real-time logs", color = OmniTextMuted, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(logs, key = { it.id }) { log ->
                    TelemetryItemCard(
                        log = log,
                        onClick = { viewModel.selectTelemetry(log) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryItemCard(
    log: TelemetryLog,
    onClick: () -> Unit
) {
    Surface(
        color = OmniSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (log.fallbackTriggered) OmniRed.copy(alpha = 0.5f) else OmniBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("telemetry_item_${log.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (log.statusCode == 200) OmniGreen.copy(alpha = 0.2f) else OmniRed.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${log.statusCode}",
                            color = if (log.statusCode == 200) OmniGreen else OmniRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        log.endpoint,
                        color = OmniTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    "${log.latencyMs} ms",
                    color = OmniCyan,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(log.provider, color = OmniTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(" · ${log.model}", color = OmniTextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }

                if (log.fallbackTriggered) {
                    Surface(
                        color = OmniRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = OmniRed, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("FAILOVER", color = OmniRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Text(
                        "Saved ${log.savedTokens} tok",
                        color = OmniGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
