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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContingencyTable
import com.example.data.model.VerificationMetrics
import com.example.data.model.WeatherVariable
import com.example.ui.components.SkillCurveChart
import com.example.ui.theme.LavenderBorder
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderDark
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkContainer
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.TextSecondaryNavy
import com.example.ui.viewmodel.WeatherViewModel
import java.util.Locale

@Composable
fun ValidationScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
            .testTag("validation_screen")
    ) {
        // Research SOP Header with CSV Export
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "METEOROLOGICAL VERIFICATION DASHBOARD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Standard Verification SOP 2021",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Location: $locationTitle • Variable: ${selectedVar.displayName}",
                            fontSize = 11.sp,
                            color = TextSecondaryNavy
                        )
                    }

                    Button(
                        onClick = { viewModel.exportVerificationCsv(context) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("export_csv_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export CSV",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Export CSV",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
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
        } else if (result == null) {
            // Insufficient validation data notice as requested
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Insufficient validation data.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Historical observation time-series records could not be aligned. Tap refresh to query Open-Meteo ERA5 archive reanalysis.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            val eval = result!!
            val bestMetric = eval.bestBlenderMetrics
            val baselineMap = eval.baselineMetrics

            // 1. Research-Grade Model Comparison Table
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verification_comparison_table"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MODEL VERIFICATION MATRIX (TEST SET)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Chronological Split: 15% Test",
                            fontSize = 10.sp,
                            color = TextSecondaryMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Table Header
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LavenderContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "METHOD / MODEL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LavenderDark, modifier = Modifier.weight(1.8f), fontFamily = FontFamily.Monospace)
                            Text(text = "MAE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LavenderDark, modifier = Modifier.weight(1.0f), fontFamily = FontFamily.Monospace)
                            Text(text = "RMSE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LavenderDark, modifier = Modifier.weight(1.0f), fontFamily = FontFamily.Monospace)
                            Text(text = "BIAS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LavenderDark, modifier = Modifier.weight(1.0f), fontFamily = FontFamily.Monospace)
                            Text(text = "SKILL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LavenderDark, modifier = Modifier.weight(1.0f), fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // AI Blended Row (Highlighted)
                    ValidationTableRow(
                        name = "AI Blending (${eval.selectedBlender.title.substringBefore("(").trim()})",
                        mae = bestMetric.mae,
                        rmse = bestMetric.rmse,
                        bias = bestMetric.bias,
                        skill = String.format(Locale.US, "+%.1f%%", bestMetric.skillImprovementVsBest),
                        isHighlighted = true,
                        unit = selectedVar.unit
                    )

                    // Individual NWP models
                    val ecmwf = baselineMap["ECMWF IFS"]
                    if (ecmwf != null) {
                        ValidationTableRow(name = "ECMWF IFS (0.25°)", mae = ecmwf.mae, rmse = ecmwf.rmse, bias = ecmwf.bias, skill = "Baseline", isHighlighted = false, unit = selectedVar.unit)
                    }

                    val gfs = baselineMap["IMD-GFS (NCEP)"]
                    if (gfs != null) {
                        ValidationTableRow(name = "IMD-GFS (NCEP Proxy)", mae = gfs.mae, rmse = gfs.rmse, bias = gfs.bias, skill = String.format(Locale.US, "%.1f%%", gfs.skillImprovementVsBest), isHighlighted = false, unit = selectedVar.unit)
                    }

                    val icon = baselineMap["DWD ICON"]
                    if (icon != null) {
                        ValidationTableRow(name = "DWD ICON (13 km)", mae = icon.mae, rmse = icon.rmse, bias = icon.bias, skill = String.format(Locale.US, "%.1f%%", icon.skillImprovementVsBest), isHighlighted = false, unit = selectedVar.unit)
                    }

                    val gem = baselineMap["CMC GEM"]
                    if (gem != null) {
                        ValidationTableRow(name = "CMC GEM (15 km)", mae = gem.mae, rmse = gem.rmse, bias = gem.bias, skill = String.format(Locale.US, "%.1f%%", gem.skillImprovementVsBest), isHighlighted = false, unit = selectedVar.unit)
                    }

                    // Simple Ensemble Average
                    val avg = baselineMap["Ensemble Equal Mean"]
                    if (avg != null) {
                        ValidationTableRow(name = "Simple Ensemble Average", mae = avg.mae, rmse = avg.rmse, bias = avg.bias, skill = String.format(Locale.US, "%.1f%%", avg.skillImprovementVsBest), isHighlighted = false, unit = selectedVar.unit)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Skill Curves across Lead Time (D+1 to D+7)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LEAD TIME SKILL DEGRADATION (RMSE D+1..D+7)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LavenderPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Evaluating error growth and chaotic physics spread across lead horizons.",
                        fontSize = 11.sp,
                        color = TextSecondaryNavy
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SkillCurveChart(
                        curveData = eval.leadTimeRmseCurve,
                        variable = selectedVar,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Contingency Verification (POD, FAR, CSI) for Precipitation
            val contingency = bestMetric.contingency
            if (contingency != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "CATEGORICAL CONTINGENCY VERIFICATION (RAIN THRESHOLD >= 0.1mm)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ContingencyStatTile(
                                label = "HIT RATE (POD)",
                                value = String.format(Locale.US, "%.1f%%", contingency.pod * 100),
                                desc = "Probability of Detection",
                                modifier = Modifier.weight(1f)
                            )
                            ContingencyStatTile(
                                label = "FALSE ALARM (FAR)",
                                value = String.format(Locale.US, "%.1f%%", contingency.far * 100),
                                desc = "False Alarm Ratio",
                                modifier = Modifier.weight(1f)
                            )
                            ContingencyStatTile(
                                label = "THREAT SCORE (CSI)",
                                value = String.format(Locale.US, "%.2f", contingency.csi),
                                desc = "Critical Success Index",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Hits: ${contingency.hits} • False Alarms: ${contingency.falseAlarms} • Misses: ${contingency.misses} • Correct Neg: ${contingency.correctNegatives}",
                                fontSize = 10.sp,
                                color = TextSecondaryMuted,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = String.format(Locale.US, "Frequency Bias: %.2f", contingency.biasScore),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LavenderPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ValidationTableRow(
    name: String,
    mae: Double,
    rmse: Double,
    bias: Double,
    skill: String,
    isHighlighted: Boolean,
    unit: String
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isHighlighted) PinkContainer else Color.Transparent,
        border = if (isHighlighted) androidx.compose.foundation.BorderStroke(1.dp, PinkBorder) else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (isHighlighted) PinkAccent else TextPrimaryDark,
                modifier = Modifier.weight(1.8f)
            )
            Text(
                text = String.format(Locale.US, "%.2f", mae),
                fontSize = 11.sp,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                color = TextSecondaryNavy,
                modifier = Modifier.weight(1.0f),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = String.format(Locale.US, "%.2f", rmse),
                fontSize = 11.sp,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                color = TextSecondaryNavy,
                modifier = Modifier.weight(1.0f),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = String.format(Locale.US, "%+.2f", bias),
                fontSize = 11.sp,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                color = TextSecondaryNavy,
                modifier = Modifier.weight(1.0f),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = skill,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) PinkAccent else LavenderPrimary,
                modifier = Modifier.weight(1.0f),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun ContingencyStatTile(
    label: String,
    value: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = LavenderContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = LavenderDark,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimaryDark,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 8.sp,
                color = TextSecondaryMuted,
                maxLines = 1
            )
        }
    }
}
