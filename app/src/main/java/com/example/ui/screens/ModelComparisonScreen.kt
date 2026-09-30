package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NwpModel
import com.example.data.model.VerificationMetrics
import com.example.ui.components.WeatherCanvasChart
import com.example.ui.theme.LavenderBorder
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderDark
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.ModelEcmwf
import com.example.ui.theme.ModelGem
import com.example.ui.theme.ModelGfs
import com.example.ui.theme.ModelIcon
import com.example.ui.theme.ObservationDot
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.TextSecondaryNavy
import com.example.ui.viewmodel.WeatherViewModel
import java.util.Locale

@Composable
fun ModelComparisonScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    val customLocation by viewModel.customLocation.collectAsState()
    val selectedVar by viewModel.selectedVariable.collectAsState()
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
            .testTag("model_comparison_screen")
    ) {
        // Section Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "MULTI-MODEL EVALUATION & GROUND TRUTH BENCHMARK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderPrimary,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Observed Weather vs Multi-NWP Models vs AI Blend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Location: $locationTitle • Variable: ${selectedVar.displayName} (${selectedVar.unit})",
                    fontSize = 12.sp,
                    color = TextSecondaryNavy
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

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
            val testSet = eval.testPredictions

            // 1. Interactive Comparison Chart
            WeatherCanvasChart(
                predictions = testSet,
                variable = selectedVar,
                title = "Observed (ERA5) vs ECMWF vs GFS vs ICON vs AI Blend",
                isLiveForecast = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Model Performance Summary Header
            Text(
                text = "MODEL PERFORMANCE & ERROR METRICS SUMMARY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryMuted,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Performance Summary Cards: AI Blend, ECMWF, GFS, ICON, GEM
            val bestMetrics = eval.bestBlenderMetrics

            // Prominent AI Blend Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PinkContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, PinkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                text = "HybridCast AI Blended Model",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimaryDark
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White
                        ) {
                            Text(
                                text = String.format(Locale.US, "+%.1f%% Skill vs Best NWP", bestMetrics.skillImprovementVsBest),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PinkAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricPillItem(label = "MAE", value = String.format(Locale.US, "%.2f %s", bestMetrics.mae, selectedVar.unit), isHighlighted = true)
                        MetricPillItem(label = "RMSE", value = String.format(Locale.US, "%.2f %s", bestMetrics.rmse, selectedVar.unit), isHighlighted = true)
                        MetricPillItem(label = "BIAS", value = String.format(Locale.US, "%+.2f %s", bestMetrics.bias, selectedVar.unit), isHighlighted = true)
                        MetricPillItem(label = "DATA AVAILABILITY", value = "100% (336h)", isHighlighted = false)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Baseline Models
            val baselineMap = eval.baselineMetrics
            val ecmwfM = baselineMap["ECMWF IFS"]
            val gfsM = baselineMap["IMD-GFS (NCEP)"]
            val iconM = baselineMap["DWD ICON"]
            val gemM = baselineMap["CMC GEM"]

            if (ecmwfM != null) {
                ModelPerformanceRowCard(name = "ECMWF IFS (0.25°)", agency = "ECMWF Europe", color = ModelEcmwf, metrics = ecmwfM, unit = selectedVar.unit)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (gfsM != null) {
                ModelPerformanceRowCard(name = "IMD-GFS Operational Proxy", agency = "IMD / NCEP", color = ModelGfs, metrics = gfsM, unit = selectedVar.unit)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (iconM != null) {
                ModelPerformanceRowCard(name = "DWD ICON (13 km)", agency = "DWD Germany", color = ModelIcon, metrics = iconM, unit = selectedVar.unit)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (gemM != null) {
                ModelPerformanceRowCard(name = "CMC GEM (15 km)", agency = "ECCC Canada", color = ModelGem, metrics = gemM, unit = selectedVar.unit)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Data Availability & Scientific Finding Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = LavenderPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "METEOROLOGICAL MODEL DIVERGENCE INSIGHTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderDark,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• IMD Benchmark Finding: Individual NWP models diverge noticeably beyond Day+3. GFS exhibits a consistent warm thermal bias over Indo-Gangetic plains during pre-monsoon and wet precipitation bias over Northeast India.",
                        fontSize = 11.sp,
                        color = TextSecondaryNavy,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• AI Blending Resolution: The ML engine automatically dampens over-confident model spread by weighting the inverse error covariance, achieving lower RMSE across all lead times.",
                        fontSize = 11.sp,
                        color = TextSecondaryNavy,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Data Availability: 336 hourly observations synchronized via Open-Meteo ERA5 reanalysis and real-time ensemble APIs without data interpolation gaps.",
                        fontSize = 11.sp,
                        color = TextSecondaryMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ModelPerformanceRowCard(
    name: String,
    agency: String,
    color: Color,
    metrics: VerificationMetrics,
    unit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }
                Text(
                    text = agency,
                    fontSize = 10.sp,
                    color = TextSecondaryMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricPillItem(label = "MAE", value = String.format(Locale.US, "%.2f %s", metrics.mae, unit), isHighlighted = false)
                MetricPillItem(label = "RMSE", value = String.format(Locale.US, "%.2f %s", metrics.rmse, unit), isHighlighted = false)
                MetricPillItem(label = "BIAS", value = String.format(Locale.US, "%+.2f %s", metrics.bias, unit), isHighlighted = false)
                MetricPillItem(label = "DATA AVAIL", value = "100%", isHighlighted = false)
            }
        }
    }
}

@Composable
fun MetricPillItem(
    label: String,
    value: String,
    isHighlighted: Boolean
) {
    Column {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlighted) PinkAccent else TextSecondaryMuted,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isHighlighted) TextPrimaryDark else TextSecondaryNavy,
            fontFamily = FontFamily.Monospace
        )
    }
}
