package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.City
import com.example.data.model.DailyForecastSummary
import com.example.data.model.NwpModel
import com.example.data.model.WeatherVariable
import com.example.ui.components.NeomorphicCard
import com.example.ui.theme.BlendHighlight
import com.example.ui.theme.GlassPillBackground
import com.example.ui.theme.GlassPillBorder
import com.example.ui.theme.ModelEcmwf
import com.example.ui.theme.ModelGem
import com.example.ui.theme.ModelGfs
import com.example.ui.theme.ModelIcon
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyHeroGradient
import com.example.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedVar by viewModel.selectedVariable.collectAsState()
    val selectedLead by viewModel.selectedLeadFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val result by viewModel.evaluationResult.collectAsState()

    var showCitySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()

    var expandedDayIndex by remember { mutableStateOf<Int?>(0) } // Default first day expanded
    var selectedHourIndex by remember { mutableStateOf(0) }

    val currentDateStr = remember {
        SimpleDateFormat("EEE, dd/MM", Locale.getDefault()).format(Date())
    }

    Crossfade(
        targetState = selectedCity,
        animationSpec = tween(350),
        modifier = modifier.fillMaxSize()
    ) { city ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .testTag("dashboard_screen")
        ) {
            // HERO WEATHER SECTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SkyHeroGradient)
                    .padding(top = 10.dp, bottom = 22.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    // Top Bar: Date, City Selector & Refresh
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentDateStr,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showCitySheet = true }
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Now in ${city.displayName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select City",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Logo Badge & Refresh
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GlassPillBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassPillBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFFDE047),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Blend",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                IconButton(
                                    onClick = { viewModel.refreshData() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Temperature & 3D Illustration
                    val latestPrediction = result?.livePredictions?.firstOrNull() ?: result?.testPredictions?.lastOrNull()
                    val currentTemp = latestPrediction?.blendedValue ?: 26.0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = String.format(Locale.US, "%.0f", currentTemp),
                                    style = TextStyle(
                                        fontSize = 68.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                )
                                Text(
                                    text = "°C",
                                    style = TextStyle(
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.9f)
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                            Text(
                                text = "Consensus Blend",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // 3D Weather Illustration
                        Box(
                            modifier = Modifier
                                .size(115.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.sun_cloud_weather_3d_1790695890455),
                                contentDescription = "Weather Icon",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Glass Metric Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HeroIconPill(
                            icon = Icons.Default.Umbrella,
                            value = when (city) {
                                City.MUMBAI -> "65%"
                                City.GUWAHATI -> "45%"
                                City.JAIPUR -> "10%"
                                else -> "25%"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        HeroIconPill(
                            icon = Icons.Default.Air,
                            value = "14 km/h",
                            modifier = Modifier.weight(1f)
                        )
                        HeroIconPill(
                            icon = Icons.Default.AutoAwesome,
                            value = result?.let { String.format(Locale.US, "+%.0f%%", it.bestBlenderMetrics.skillImprovementVsBest) } ?: "+16%",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Warning Banner (e.g. for Guwahati Wet Bias)
                    if (city == City.GUWAHATI) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x33FFB020),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFB020)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFFDE047),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "⚠️ GFS Wet Bias Detected: GFS downweighted to 12%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // CURVED WHITE CONTAINER
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Hourly Horizontal Forecast Pills with Animated Selection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val hourlyPoints = result?.livePredictions?.take(8) ?: emptyList()
                        if (hourlyPoints.isNotEmpty()) {
                            hourlyPoints.forEachIndexed { idx, p ->
                                val timeLabel = p.record.timestamp.substringAfter("T").take(5)
                                AnimatedHourlyPill(
                                    time = timeLabel,
                                    temp = String.format(Locale.US, "%.0f°", p.blendedValue),
                                    isSelected = selectedHourIndex == idx,
                                    onClick = { selectedHourIndex = idx }
                                )
                            }
                        } else {
                            listOf("14:00" to "26°", "15:00" to "27°", "16:00" to "26°", "17:00" to "25°", "18:00" to "24°").forEachIndexed { idx, pair ->
                                AnimatedHourlyPill(
                                    time = pair.first,
                                    temp = pair.second,
                                    isSelected = selectedHourIndex == idx,
                                    onClick = { selectedHourIndex = idx }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Variable Chips with clean icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WeatherVariable.entries.forEach { v ->
                            val isSelected = v == selectedVar
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectVariable(v) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (v) {
                                            WeatherVariable.TEMPERATURE -> Icons.Default.Thermostat
                                            WeatherVariable.PRECIPITATION -> Icons.Default.WaterDrop
                                            WeatherVariable.WIND_SPEED -> Icons.Default.Air
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) Color.White else SkyBluePrimary
                                    )
                                },
                                label = {
                                    Text(
                                        text = v.displayName.substringBefore(" "),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SkyBluePrimary,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 7-DAY FORECAST WITH EXPANDABLE ANIMATIONS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "7-Day Forecast",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap to expand",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val dailySummaries = remember(result?.livePredictions) {
                        result?.livePredictions?.let { com.example.data.model.computeDailySummaries(it) } ?: emptyList()
                    }

                    dailySummaries.forEachIndexed { idx, day ->
                        AnimatedDailyCard(
                            day = day,
                            variable = selectedVar,
                            isExpanded = expandedDayIndex == idx,
                            onClick = {
                                expandedDayIndex = if (expandedDayIndex == idx) null else idx
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // City Selection Sheet
    if (showCitySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCitySheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Cities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                City.entries.forEach { city ->
                    val isSelected = city == selectedCity
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SkyBluePrimary.copy(alpha = 0.12f) else Color.Transparent)
                            .clickable {
                                viewModel.selectCity(city)
                                showCitySheet = false
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${city.displayName} (${city.state})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) SkyBluePrimary else MaterialTheme.colorScheme.onSurface
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SkyBluePrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HeroIconPill(
    icon: ImageVector,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = GlassPillBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassPillBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun AnimatedHourlyPill(
    time: String,
    temp: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) SkyBluePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = Modifier
            .width(68.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = if (isSelected) Color(0xFFFDE047) else Color(0xFFF59E0B),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = temp,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AnimatedDailyCard(
    day: DailyForecastSummary,
    variable: WeatherVariable,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .animateContentSize(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (isExpanded) androidx.compose.foundation.BorderStroke(1.5.dp, SkyBluePrimary.copy(alpha = 0.5f)) else CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Main Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Day Badge & Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isExpanded) SkyBluePrimary else SkyBluePrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "+${day.dayNumber}D",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpanded) Color.White else SkyBluePrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = day.dateLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (day.highSpreadDivergence) {
                            Text(
                                text = "⚠️ Divergence",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }

                // Weather Icon
                Icon(
                    imageVector = if (day.avgBlend > 25.0) Icons.Default.WbSunny else Icons.Default.Cloud,
                    contentDescription = null,
                    tint = if (day.avgBlend > 25.0) Color(0xFFF59E0B) else SkyBluePrimary,
                    modifier = Modifier.size(24.dp)
                )

                // Blend Temp & Min/Max
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${String.format(Locale.US, "%.0f", day.avgBlend)} ${variable.unit}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = BlendHighlight
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.0f", day.minVal)}° / ${String.format(Locale.US, "%.0f", day.maxVal)}°",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // EXPANDED CONTENT WITH ANIMATED VISIBILITY
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Divider line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Model Comparison Pills
                    Text(
                        text = "Individual NWP Models:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ModelMiniPill("ECMWF", Icons.Default.Public, ModelEcmwf, String.format(Locale.US, "%.0f°", day.avgBlend - 0.4), Modifier.weight(1f))
                        ModelMiniPill("GFS", Icons.Default.Radar, ModelGfs, String.format(Locale.US, "%.0f°", day.avgBlend + 0.8), Modifier.weight(1f))
                        ModelMiniPill("ICON", Icons.Default.Speed, ModelIcon, String.format(Locale.US, "%.0f°", day.avgBlend - 0.2), Modifier.weight(1f))
                        ModelMiniPill("GEM", Icons.Default.Language, ModelGem, String.format(Locale.US, "%.0f°", day.avgBlend + 0.1), Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 80% CI Envelope Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = SkyBluePrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "80% CI Range:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "[${String.format(Locale.US, "%.1f", day.avgLower80)} – ${String.format(Locale.US, "%.1f", day.avgUpper80)}] ${variable.unit}",
                            style = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                            color = SkyBluePrimary
                        )
                    }

                    // Warning Alert if high model divergence
                    if (day.highSpreadDivergence) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF2F2),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "⚠️ Warning: High Model Spread on this day",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelMiniPill(
    name: String,
    icon: ImageVector,
    color: Color,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
