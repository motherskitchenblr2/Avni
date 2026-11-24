package com.example.omniroute.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.omniroute.data.model.CircuitState
import com.example.omniroute.data.model.Provider
import com.example.omniroute.data.model.ProviderCategory
import com.example.omniroute.ui.OmniRouteViewModel
import com.example.omniroute.ui.components.CircuitBadge
import com.example.omniroute.ui.components.EditApiKeyDialog
import com.example.omniroute.ui.theme.*

@Composable
fun ProvidersScreen(
    viewModel: OmniRouteViewModel,
    modifier: Modifier = Modifier
) {
    val providers by viewModel.filteredProviders.collectAsState()
    val searchQuery by viewModel.providerSearchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val editingProvider by viewModel.editingProvider.collectAsState()

    editingProvider?.let { provider ->
        EditApiKeyDialog(
            provider = provider,
            onDismiss = { viewModel.closeEditProvider() },
            onSave = { key -> viewModel.saveProviderKey(provider.id, key) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Provider Catalog", style = MaterialTheme.typography.headlineLarge, color = OmniTextPrimary)
                Text("291 AI providers supported with unified interface", color = OmniTextSecondary, fontSize = 13.sp)
            }
            Surface(
                color = OmniCyan.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "${providers.size} Shown",
                    color = OmniCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setProviderSearch(it) },
            placeholder = { Text("Search providers or models (e.g. Gemini, Groq, Llama)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OmniTextMuted) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setProviderSearch("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = OmniTextMuted)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = OmniTextPrimary,
                unfocusedTextColor = OmniTextPrimary,
                focusedBorderColor = OmniCyan,
                unfocusedBorderColor = OmniBorder,
                focusedContainerColor = OmniSurface,
                unfocusedContainerColor = OmniSurface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_providers_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { viewModel.setCategoryFilter(null) },
                label = { Text("All (291)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = OmniCyan,
                    selectedLabelColor = OmniBg,
                    containerColor = OmniSurfaceVariant,
                    labelColor = OmniTextSecondary
                )
            )
            ProviderCategory.values().forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.setCategoryFilter(category) },
                    label = { Text(category.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OmniCyan,
                        selectedLabelColor = OmniBg,
                        containerColor = OmniSurfaceVariant,
                        labelColor = OmniTextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Provider List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(providers, key = { it.id }) { provider ->
                ProviderItemCard(
                    provider = provider,
                    onEditKey = { viewModel.openEditProvider(provider) },
                    onTestPing = { viewModel.testProvider(provider.id) }
                )
            }
        }
    }
}

@Composable
private fun ProviderItemCard(
    provider: Provider,
    onEditKey: () -> Unit,
    onTestPing: () -> Unit
) {
    Surface(
        color = OmniSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (provider.circuitState == CircuitState.OPEN) OmniRed.copy(alpha = 0.5f) else OmniBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Name, Category, Circuit State
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(provider.name, color = OmniTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(provider.category.label, color = OmniCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                CircuitBadge(state = provider.circuitState)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(provider.description, color = OmniTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata row: Latency, Quota
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Speed,
                        contentDescription = null,
                        tint = if (provider.latencyMs < 120) OmniGreen else if (provider.latencyMs < 250) OmniCyan else OmniYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${provider.latencyMs} ms",
                        color = OmniTextPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    color = OmniSurfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        provider.freeTokensQuota,
                        color = OmniTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Models Preview Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                provider.models.forEach { model ->
                    Surface(
                        color = OmniSurfaceElevated,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            model.name,
                            color = OmniTextPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = OmniBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onTestPing,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OmniCyan),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("ping_provider_${provider.id}")
                ) {
                    Icon(Icons.Default.NetworkPing, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ping Latency", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onEditKey,
                    colors = ButtonDefaults.buttonColors(containerColor = OmniSurfaceVariant, contentColor = OmniTextPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("config_key_${provider.id}")
                ) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(14.dp), tint = OmniCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (provider.isConfigured) "Manage Key" else "Set Key", fontSize = 12.sp)
                }
            }
        }
    }
}
