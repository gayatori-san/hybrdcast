package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LocationPickerDialog
import com.example.ui.screens.AiBlendingScreen
import com.example.ui.screens.ForecastScreen
import com.example.ui.screens.ModelComparisonScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.ResearchScreen
import com.example.ui.screens.ValidationScreen
import com.example.ui.theme.LavenderBorder
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderDark
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.TextSecondaryNavy
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
                val selectedCity by weatherViewModel.selectedCity.collectAsState()
                val customLocation by weatherViewModel.customLocation.collectAsState()

                var showLocationSheet by remember { mutableStateOf(false) }

                val locationLabel = customLocation?.name ?: "${selectedCity.displayName}, ${selectedCity.state}"

                if (activeTab != ScreenTab.OVERVIEW) {
                    BackHandler {
                        weatherViewModel.selectTab(ScreenTab.OVERVIEW)
                    }
                }

                if (showLocationSheet) {
                    LocationPickerDialog(
                        currentCity = selectedCity,
                        customLocation = customLocation,
                        onSelectCity = { weatherViewModel.selectCity(it) },
                        onApplyCustomLocation = { name, lat, lon ->
                            weatherViewModel.setCustomLocation(name, lat, lon)
                        },
                        onDismiss = { showLocationSheet = false }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            // Top Bar Header
                            TopAppBar(
                                title = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .background(LavenderContainer, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = LavenderPrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "HybridCast",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = TextPrimaryDark
                                                )
                                                Text(
                                                    text = "AI–NWP Blending Platform",
                                                    fontSize = 10.sp,
                                                    color = TextSecondaryMuted,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }

                                        // Location Badge Button
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = LavenderContainer,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
                                            modifier = Modifier
                                                .clickable { showLocationSheet = true }
                                                .testTag("top_bar_location_badge")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = LavenderPrimary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = locationLabel,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = LavenderDark,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.testTag("app_top_bar")
                            )

                            // Top Navigation Row: Overview | Forecast | Model Comparison | AI Blending | Validation | Research
                            TopNavScrollableRow(
                                activeTab = activeTab,
                                onSelectTab = { weatherViewModel.selectTab(it) }
                            )

                            // Thin separator
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(LavenderBorder)
                            )
                        }
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
                            ScreenTab.OVERVIEW -> OverviewScreen(
                                viewModel = weatherViewModel,
                                onOpenLocationPicker = { showLocationSheet = true }
                            )
                            ScreenTab.FORECAST -> ForecastScreen(
                                viewModel = weatherViewModel,
                                onOpenLocationPicker = { showLocationSheet = true }
                            )
                            ScreenTab.MODEL_COMPARISON -> ModelComparisonScreen(
                                viewModel = weatherViewModel
                            )
                            ScreenTab.AI_BLENDING -> AiBlendingScreen(
                                viewModel = weatherViewModel
                            )
                            ScreenTab.VALIDATION -> ValidationScreen(
                                viewModel = weatherViewModel
                            )
                            ScreenTab.RESEARCH -> ResearchScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopNavScrollableRow(
    activeTab: ScreenTab,
    onSelectTab: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScreenTab.entries.forEach { tab ->
            val isSelected = activeTab == tab
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) LavenderPrimary else Color.Transparent,
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
                modifier = Modifier
                    .clickable { onSelectTab(tab) }
                    .testTag("top_nav_item_${tab.name.lowercase()}")
            ) {
                Text(
                    text = tab.title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextSecondaryNavy,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
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
        Triple(ScreenTab.OVERVIEW, Icons.Default.Home, "Overview"),
        Triple(ScreenTab.FORECAST, Icons.Default.WbSunny, "Forecast"),
        Triple(ScreenTab.MODEL_COMPARISON, Icons.Default.CompareArrows, "Compare"),
        Triple(ScreenTab.AI_BLENDING, Icons.Default.Hub, "Blending"),
        Triple(ScreenTab.VALIDATION, Icons.Default.Verified, "Validation"),
        Triple(ScreenTab.RESEARCH, Icons.Default.MenuBook, "Research")
    )

    NavigationBar(
        modifier = modifier.testTag("bottom_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
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
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LavenderPrimary,
                    selectedTextColor = LavenderPrimary,
                    indicatorColor = LavenderContainer,
                    unselectedIconColor = TextSecondaryMuted,
                    unselectedTextColor = TextSecondaryMuted
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
            )
        }
    }
}

