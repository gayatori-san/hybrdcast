package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NwpModel
import com.example.data.model.WeatherVariable
import com.example.ui.components.WeatherCanvasChart
import com.example.ui.theme.LavenderBorder
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderDark
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.ModelEcmwf
import com.example.ui.theme.ModelGem
import com.example.ui.theme.ModelGfs
import com.example.ui.theme.ModelIcon
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.TextSecondaryNavy
import com.example.ui.viewmodel.WeatherViewModel
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ForecastScreen(
    viewModel: WeatherViewModel,
    onOpenLocationPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    val customLocation by viewModel.customLocation.collectAsState()
    val selectedVar by viewModel.selectedVariable.collectAsState()
    val timeHorizon by viewModel.timeHorizonHours.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val result by viewModel.evaluationResult.collectAsState()

    val scrollState = rememberScrollState()

    val locationTitle = customLocation?.name ?: "${selectedCity.displayName}, ${selectedCity.state}"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("forecast_screen")
    ) {
        // Location & Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIVE OPERATIONAL FORECAST",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LavenderPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = locationTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onOpenLocationPicker,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderContainer),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = LavenderPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Switch Location",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderDark
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.refreshData() },
                        modifier = Modifier
                            .size(36.dp)
                            .background(LavenderContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = LavenderPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Variable Switcher: Temperature | Rainfall | Wind Speed | Humidity | Surface Pressure
        Text(
            text = "ATMOSPHERIC VARIABLE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryMuted,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
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
                                WeatherVariable.HUMIDITY -> Icons.Default.WaterDrop
                                WeatherVariable.SURFACE_PRESSURE -> Icons.Default.Compress
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isSelected) Color.White else LavenderPrimary
                        )
                    },
                    label = {
                        Text(
                            text = v.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LavenderPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = TextPrimaryDark
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) LavenderPrimary else LavenderBorder,
                        borderWidth = 1.dp,
                        enabled = true,
                        selected = isSelected
                    ),
                    modifier = Modifier.testTag("var_chip_${v.id}")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Time Horizon Filter: 24h | 48h | 72h | 7 days
        Text(
            text = "FORECAST LEAD HORIZON",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryMuted,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val horizons = listOf(
                Pair(24, "24h (D+1)"),
                Pair(48, "48h (D+2)"),
                Pair(72, "72h (D+3)"),
                Pair(168, "7 Days")
            )
            horizons.forEach { (h, label) ->
                val isSelected = timeHorizon == h
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) LavenderPrimary else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) LavenderPrimary else LavenderBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectTimeHorizon(h) }
                        .testTag("horizon_tab_${h}h")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimaryDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LavenderPrimary)
            }
        } else if (result != null) {
            val eval = result!!
            val rawLive = eval.livePredictions
            // Filter by selected time horizon hours
            val filteredLive = rawLive.take(timeHorizon)
            val currentPoint = filteredLive.firstOrNull()

            // 1. Interactive Time-Series Canvas Chart
            WeatherCanvasChart(
                predictions = filteredLive,
                variable = selectedVar,
                title = "Live Forecast: AI Blend vs NWP Models (${timeHorizon}h)",
                isLiveForecast = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. AI Blended Forecast vs Individual Models Display
            if (currentPoint != null) {
                val blendVal = currentPoint.blendedValue
                val record = currentPoint.record
                val simpleAvg = record.ensembleMean

                // Find best individual NWP model forecast (e.g. ECMWF)
                val ecmwfVal = record.modelForecasts[NwpModel.ECMWF] ?: blendVal
                val gfsVal = record.modelForecasts[NwpModel.GFS] ?: blendVal
                val iconVal = record.modelForecasts[NwpModel.ICON] ?: blendVal
                val gemVal = record.modelForecasts[NwpModel.GEM] ?: blendVal

                // Best single model by validation is ECMWF IFS
                val bestSingleVal = ecmwfVal
                val diffVsBest = blendVal - bestSingleVal
                val diffVsAvg = blendVal - simpleAvg

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("blended_forecast_summary_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(PinkAccent, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI BLENDED FORECAST",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = LavenderContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
                            ) {
                                Text(
                                    text = eval.selectedBlender.title.substringBefore("(").trim(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LavenderDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large Forecast Value
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1f", blendVal),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = selectedVar.unit,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PinkAccent,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Prediction Range / Uncertainty Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LavenderContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = LavenderPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "80% Uncertainty Bounds",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LavenderDark
                                        )
                                    }
                                    Text(
                                        text = String.format(Locale.US, "Spread σ = %.2f %s", record.ensembleSpread, selectedVar.unit),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = TextSecondaryNavy
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Prediction Range:",
                                        fontSize = 12.sp,
                                        color = TextSecondaryNavy
                                    )
                                    Text(
                                        text = String.format(
                                            Locale.US,
                                            "%.1f %s  –  %.1f %s (±%.1f %s)",
                                            currentPoint.lowerBound80,
                                            selectedVar.unit,
                                            currentPoint.upperBound80,
                                            selectedVar.unit,
                                            (currentPoint.upperBound80 - currentPoint.lowerBound80) / 2.0,
                                            selectedVar.unit
                                        ),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimaryDark,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Model Comparison Grid: ECMWF | GFS | ICON | GEM
                        Text(
                            text = "INDIVIDUAL OPERATIONAL NWP MODEL PREDICTIONS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryMuted,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ModelPredictionMiniPill(
                                name = "ECMWF IFS",
                                value = ecmwfVal,
                                unit = selectedVar.unit,
                                color = ModelEcmwf,
                                modifier = Modifier.weight(1f)
                            )
                            ModelPredictionMiniPill(
                                name = "IMD-GFS",
                                value = gfsVal,
                                unit = selectedVar.unit,
                                color = ModelGfs,
                                modifier = Modifier.weight(1f)
                            )
                            ModelPredictionMiniPill(
                                name = "DWD ICON",
                                value = iconVal,
                                unit = selectedVar.unit,
                                color = ModelIcon,
                                modifier = Modifier.weight(1f)
                            )
                            ModelPredictionMiniPill(
                                name = "CMC GEM",
                                value = gemVal,
                                unit = selectedVar.unit,
                                color = ModelGem,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Differences Comparison Strip: Best NWP vs Simple Avg vs AI Blend
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Best NWP (ECMWF)", fontSize = 10.sp, color = TextSecondaryMuted)
                                    Text(
                                        text = String.format(Locale.US, "%.1f %s", bestSingleVal, selectedVar.unit),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Simple Ensemble Avg", fontSize = 10.sp, color = TextSecondaryMuted)
                                    Text(
                                        text = String.format(Locale.US, "%.1f %s", simpleAvg, selectedVar.unit),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "AI Blend vs Best", fontSize = 10.sp, color = LavenderPrimary, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = String.format(Locale.US, "%+.2f %s", diffVsBest, selectedVar.unit),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (abs(diffVsBest) < 0.05) TextSecondaryNavy else PinkAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ModelPredictionMiniPill(
    name: String,
    value: Double,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryNavy,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = String.format(Locale.US, "%.1f %s", value, unit),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimaryDark,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
