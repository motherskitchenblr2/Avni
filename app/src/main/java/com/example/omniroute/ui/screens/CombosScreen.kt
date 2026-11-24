package com.example.omniroute.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.omniroute.data.model.Combo
import com.example.omniroute.data.model.ComboStrategy
import com.example.omniroute.ui.OmniRouteViewModel
import com.example.omniroute.ui.components.CreateComboDialog
import com.example.omniroute.ui.theme.*

@Composable
fun CombosScreen(
    viewModel: OmniRouteViewModel,
    modifier: Modifier = Modifier
) {
    val combos by viewModel.combos.collectAsState()
    val activeCombo by viewModel.activeCombo.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val isCreateComboOpen by viewModel.isCreateComboOpen.collectAsState()

    var showStrategyGuide by remember { mutableStateOf(false) }

    if (isCreateComboOpen) {
        CreateComboDialog(
            providers = providers,
            onDismiss = { viewModel.closeCreateCombo() },
            onCreate = { name, desc, strategy, targets ->
                viewModel.createCombo(name, desc, strategy, targets)
            }
        )
    }

    Scaffold(
        containerColor = OmniBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateCombo() },
                containerColor = OmniPurple,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_create_combo")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Custom Combo")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Model Combos", style = MaterialTheme.typography.headlineLarge, color = OmniTextPrimary)
                        Text("19 routing strategies with transparent auto-fallback", color = OmniTextSecondary, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { showStrategyGuide = !showStrategyGuide },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OmniCyan),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showStrategyGuide) "Hide Guide" else "19 Strategies", fontSize = 11.sp)
                    }
                }
            }

            // Strategy Guide Expandable Section
            if (showStrategyGuide) {
                item {
                    Surface(
                        color = OmniSurfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OmniBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("19 Supported Routing Strategies", color = OmniCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            ComboStrategy.values().forEach { strategy ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(strategy.label, color = OmniTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text(strategy.shortDesc, color = OmniTextMuted, fontSize = 11.sp)
                                }
                                if (strategy != ComboStrategy.values().last()) {
                                    HorizontalDivider(color = OmniBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Combos List
            items(combos, key = { it.id }) { combo ->
                val isActive = combo.id == activeCombo?.id
                ComboItemCard(
                    combo = combo,
                    isActive = isActive,
                    onActivate = { viewModel.setActiveCombo(combo) }
                )
            }
        }
    }
}

@Composable
private fun ComboItemCard(
    combo: Combo,
    isActive: Boolean,
    onActivate: () -> Unit
) {
    Surface(
        color = OmniSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isActive) 1.5.dp else 1.dp,
            color = if (isActive) OmniPurple else OmniBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(combo.name, color = OmniTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (isActive) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = OmniPurple.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "ACTIVE GATEWAY",
                                    color = OmniPurple,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(combo.strategy.label, color = OmniCyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                if (!isActive) {
                    Button(
                        onClick = onActivate,
                        colors = ButtonDefaults.buttonColors(containerColor = OmniSurfaceVariant, contentColor = OmniTextPrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_activate_combo_${combo.id}")
                    ) {
                        Text("Set Active", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(combo.description, color = OmniTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))
            Text("Failover & Routing Targets (${combo.targets.size} models):", color = OmniTextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                combo.targets.forEachIndexed { idx, target ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(OmniSurfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (idx == 0) OmniCyan.copy(alpha = 0.2f) else OmniBorder,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    if (idx == 0) "PRIMARY" else "FALLBACK #$idx",
                                    color = if (idx == 0) OmniCyan else OmniTextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(target.providerName, color = OmniTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Text(target.modelId, color = OmniTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Timeout: ${combo.timeoutMs / 1000}s · Retries: ${combo.retryCount}", color = OmniTextMuted, fontSize = 11.sp)
                Text("Failover on: 429, 500, 502, 503, 504", color = OmniTextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}
