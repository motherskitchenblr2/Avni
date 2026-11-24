package com.example.omniroute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omniroute.ui.NavigationTab
import com.example.omniroute.ui.OmniRouteViewModel
import com.example.omniroute.ui.screens.*
import com.example.omniroute.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: OmniRouteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OmniRouteTheme {
                OmniRouteApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun OmniRouteApp(viewModel: OmniRouteViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()

    // Handle back button when not on dashboard
    if (currentTab != NavigationTab.DASHBOARD) {
        BackHandler {
            viewModel.setTab(NavigationTab.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OmniBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = OmniSurface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == NavigationTab.DASHBOARD,
                    onClick = { viewModel.setTab(NavigationTab.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OmniBg,
                        selectedTextColor = OmniCyan,
                        indicatorColor = OmniCyan,
                        unselectedIconColor = OmniTextMuted,
                        unselectedTextColor = OmniTextMuted
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )

                NavigationBarItem(
                    selected = currentTab == NavigationTab.PROVIDERS,
                    onClick = { viewModel.setTab(NavigationTab.PROVIDERS) },
                    icon = { Icon(Icons.Default.Hub, contentDescription = "Providers") },
                    label = { Text("Providers", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OmniBg,
                        selectedTextColor = OmniCyan,
                        indicatorColor = OmniCyan,
                        unselectedIconColor = OmniTextMuted,
                        unselectedTextColor = OmniTextMuted
                    ),
                    modifier = Modifier.testTag("nav_providers")
                )

                NavigationBarItem(
                    selected = currentTab == NavigationTab.COMBOS,
                    onClick = { viewModel.setTab(NavigationTab.COMBOS) },
                    icon = { Icon(Icons.AutoMirrored.Filled.AltRoute, contentDescription = "Combos") },
                    label = { Text("Combos", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OmniBg,
                        selectedTextColor = OmniCyan,
                        indicatorColor = OmniCyan,
                        unselectedIconColor = OmniTextMuted,
                        unselectedTextColor = OmniTextMuted
                    ),
                    modifier = Modifier.testTag("nav_combos")
                )

                NavigationBarItem(
                    selected = currentTab == NavigationTab.PLAYGROUND,
                    onClick = { viewModel.setTab(NavigationTab.PLAYGROUND) },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Playground") },
                    label = { Text("Playground", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OmniBg,
                        selectedTextColor = OmniCyan,
                        indicatorColor = OmniCyan,
                        unselectedIconColor = OmniTextMuted,
                        unselectedTextColor = OmniTextMuted
                    ),
                    modifier = Modifier.testTag("nav_playground")
                )

                NavigationBarItem(
                    selected = currentTab == NavigationTab.TRAFFIC,
                    onClick = { viewModel.setTab(NavigationTab.TRAFFIC) },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Inspector") },
                    label = { Text("Inspector", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OmniBg,
                        selectedTextColor = OmniCyan,
                        indicatorColor = OmniCyan,
                        unselectedIconColor = OmniTextMuted,
                        unselectedTextColor = OmniTextMuted
                    ),
                    modifier = Modifier.testTag("nav_traffic")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(OmniBg)
        ) {
            when (currentTab) {
                NavigationTab.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToPlayground = { viewModel.setTab(NavigationTab.PLAYGROUND) }
                )
                NavigationTab.PROVIDERS -> ProvidersScreen(viewModel = viewModel)
                NavigationTab.COMBOS -> CombosScreen(viewModel = viewModel)
                NavigationTab.PLAYGROUND -> PlaygroundScreen(viewModel = viewModel)
                NavigationTab.TRAFFIC -> TrafficScreen(viewModel = viewModel)
            }
        }
    }
}
