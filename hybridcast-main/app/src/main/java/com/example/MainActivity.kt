package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ChartsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EvaluationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.TrustMapScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.viewmodel.ScreenTab
import com.example.ui.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val activeTab by weatherViewModel.activeTab.collectAsState()

                if (activeTab != ScreenTab.HOME) {
                    BackHandler {
                        weatherViewModel.selectTab(ScreenTab.HOME)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "HybridCast",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.testTag("app_top_bar")
                        )
                    },
                    bottomBar = {
                        HybridCastBottomNav(
                            currentTab = activeTab,
                            onTabSelected = { weatherViewModel.selectTab(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (activeTab) {
                            ScreenTab.HOME -> HomeScreen(viewModel = weatherViewModel)
                            ScreenTab.FORECAST -> DashboardScreen(viewModel = weatherViewModel)
                            ScreenTab.CHARTS -> ChartsScreen(viewModel = weatherViewModel)
                            ScreenTab.TRUST -> TrustMapScreen(viewModel = weatherViewModel)
                            ScreenTab.SCORES -> EvaluationScreen(viewModel = weatherViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HybridCastBottomNav(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple(ScreenTab.HOME, Icons.Default.Home, "Home"),
        Triple(ScreenTab.FORECAST, Icons.Default.WbSunny, "Forecast"),
        Triple(ScreenTab.CHARTS, Icons.Default.ShowChart, "Charts"),
        Triple(ScreenTab.TRUST, Icons.Default.Shield, "Trust"),
        Triple(ScreenTab.SCORES, Icons.Default.FactCheck, "Scores")
    )

    NavigationBar(
        modifier = modifier.testTag("bottom_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        items.forEach { (tab, icon, label) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SkyBluePrimary,
                    selectedTextColor = SkyBluePrimary,
                    indicatorColor = SkyBluePrimary.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
            )
        }
    }
}
