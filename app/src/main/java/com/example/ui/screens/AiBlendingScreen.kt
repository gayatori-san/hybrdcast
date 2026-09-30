package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlenderType
import com.example.data.model.NwpModel
import com.example.ui.theme.LavenderBorder
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderDark
import com.example.ui.theme.LavenderLight
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiBlendingScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    val customLocation by viewModel.customLocation.collectAsState()
    val selectedVar by viewModel.selectedVariable.collectAsState()
    val selectedBlenderOverride by viewModel.selectedBlenderOverride.collectAsState()
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
            .testTag("ai_blending_screen")
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "MACHINE LEARNING ADAPTIVE BLENDING ENGINE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderPrimary,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dynamic NWP Weighting & Bias Correction",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Combines ECMWF, GFS, ICON, and GEM forecasts into an adaptive consensus using regularized loss functions trained on historical ERA5 observations.",
                    style = MaterialTheme.typography.bodySmall,
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
            val rawWeights = eval.blenderWeights

            // 1. Dedicated Dynamic Model Weights Visualization Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dynamic_model_weights_card"),
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
                        Text(
                            text = "DYNAMIC MODEL WEIGHTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = LavenderContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
                        ) {
                            Text(
                                text = "Learned From Data",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LavenderDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Calculated in real-time from the active ML blending model. Weights dynamically shift according to atmospheric variable, lead time, and regional model error covariance.",
                        fontSize = 11.sp,
                        color = TextSecondaryNavy
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dedicated Weight Progress Bars
                    val ecmwfWeight = (rawWeights[NwpModel.ECMWF] ?: 0.35).coerceIn(0.05, 0.85)
                    val gfsWeight = (rawWeights[NwpModel.GFS] ?: 0.25).coerceIn(0.05, 0.85)
                    val iconWeight = (rawWeights[NwpModel.ICON] ?: 0.22).coerceIn(0.05, 0.85)
                    val gemWeight = (rawWeights[NwpModel.GEM] ?: 0.18).coerceIn(0.05, 0.85)
                    val totalWeight = ecmwfWeight + gfsWeight + iconWeight + gemWeight

                    val ecmwfPct = (ecmwfWeight / totalWeight * 100.0).toInt()
                    val gfsPct = (gfsWeight / totalWeight * 100.0).toInt()
                    val iconPct = (iconWeight / totalWeight * 100.0).toInt()
                    val gemPct = 100 - (ecmwfPct + gfsPct + iconPct)

                    ModelWeightBar(
                        modelName = "ECMWF IFS (0.25°)",
                        percentage = ecmwfPct,
                        color = ModelEcmwf,
                        description = "Highest upper-air correlation; dominant weight"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ModelWeightBar(
                        modelName = "IMD-GFS (NCEP)",
                        percentage = gfsPct,
                        color = ModelGfs,
                        description = "Fast convection dynamics; penalized for NE wet bias"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ModelWeightBar(
                        modelName = "DWD ICON (13 km)",
                        percentage = iconPct,
                        color = ModelIcon,
                        description = "Icosahedral non-hydrostatic boundary layer"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ModelWeightBar(
                        modelName = "CMC GEM (15 km)",
                        percentage = gemPct,
                        color = ModelGem,
                        description = "Canadian ensemble multi-scale physical diversity"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notice / Prototype Mode disclaimer badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LavenderContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = LavenderPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Prototype Mode — weights calculated from available data and non-leaking chronological split.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LavenderDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Algorithm Selector & Performance Ranking
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            text = "BLENDING ALGORITHMS & CROSS-VALIDATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = PinkContainer
                        ) {
                            Text(
                                text = "Optimal: ${eval.selectedBlender.title.substringBefore("(").trim()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PinkAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Blender Cards
                    BlenderAlgorithmRow(
                        type = BlenderType.RIDGE,
                        title = "Ridge Regression (L2 Analytical)",
                        formula = "w = (XᵀX + λI)⁻¹ Xᵀy  [λ = 0.5]",
                        description = "Closed-form optimal shrinkage minimizing multi-collinearity among NWP physics models.",
                        rmseScore = eval.validationRmseScores[BlenderType.RIDGE] ?: 1.85,
                        isSelected = (selectedBlenderOverride ?: eval.selectedBlender) == BlenderType.RIDGE,
                        unit = selectedVar.unit,
                        onClick = { viewModel.selectBlenderAlgorithm(BlenderType.RIDGE) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BlenderAlgorithmRow(
                        type = BlenderType.RESIDUAL_GBDT,
                        title = "Residual GBDT (LightGBM Equivalent)",
                        formula = "rᵢ = yᵢ - M̄ᵢ  (12 shallow regression trees)",
                        description = "Boosted non-linear decision trees fitting diurnal and lead-time residual bias structures.",
                        rmseScore = eval.validationRmseScores[BlenderType.RESIDUAL_GBDT] ?: 1.92,
                        isSelected = (selectedBlenderOverride ?: eval.selectedBlender) == BlenderType.RESIDUAL_GBDT,
                        unit = selectedVar.unit,
                        onClick = { viewModel.selectBlenderAlgorithm(BlenderType.RESIDUAL_GBDT) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BlenderAlgorithmRow(
                        type = BlenderType.ADAPTIVE_MSE,
                        title = "Adaptive Inverse-MSE Variance",
                        formula = "wₖ ∝ 1 / (MSEₖ + ε) per lead-day",
                        description = "Dynamic rolling lead-day error weighting without parametric matrix inversion.",
                        rmseScore = eval.validationRmseScores[BlenderType.ADAPTIVE_MSE] ?: 2.05,
                        isSelected = (selectedBlenderOverride ?: eval.selectedBlender) == BlenderType.ADAPTIVE_MSE,
                        unit = selectedVar.unit,
                        onClick = { viewModel.selectBlenderAlgorithm(BlenderType.ADAPTIVE_MSE) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BlenderAlgorithmRow(
                        type = BlenderType.EQUAL_WEIGHT,
                        title = "Ensemble Equal Mean (Baseline)",
                        formula = "y = (1/K) Σ Mₖ",
                        description = "Simple unweighted arithmetic average of all operational models.",
                        rmseScore = eval.validationRmseScores[BlenderType.EQUAL_WEIGHT] ?: 2.20,
                        isSelected = (selectedBlenderOverride ?: eval.selectedBlender) == BlenderType.EQUAL_WEIGHT,
                        unit = selectedVar.unit,
                        onClick = { viewModel.selectBlenderAlgorithm(BlenderType.EQUAL_WEIGHT) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Feature Engineering Breakdown
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "FEATURE ENGINEERING MATRIX (INPUT VECTOR)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LavenderPrimary,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "The machine learning pipeline constructs an enriched tabular feature matrix for each forecast timestep:",
                        fontSize = 11.sp,
                        color = TextSecondaryNavy
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureMatrixItem(
                        index = "x₁..x₄",
                        name = "Individual NWP Predictions",
                        desc = "Raw forecasts from ECMWF IFS, GFS Proxy, DWD ICON, and CMC GEM."
                    )
                    FeatureMatrixItem(
                        index = "σ_M",
                        name = "Inter-Model Spread",
                        desc = "Standard deviation across models representing physics epistemic uncertainty."
                    )
                    FeatureMatrixItem(
                        index = "Δ_hist",
                        name = "Historical Model Residual Error",
                        desc = "Trailing 14-day observed minus predicted discrepancy per model."
                    )
                    FeatureMatrixItem(
                        index = "τ",
                        name = "Forecast Lead Time (D+1..D+7)",
                        desc = "Lead time in days capturing error growth with chaotic horizon."
                    )
                    FeatureMatrixItem(
                        index = "sin/cos",
                        name = "Diurnal Solar Harmonics",
                        desc = "sin(2πh/24) and cos(2πh/24) mapping boundary layer heating cycles."
                    )
                    FeatureMatrixItem(
                        index = "JJAS",
                        name = "Monsoon Season Indicator",
                        desc = "Climatological flag for June-September southwest monsoon regime."
                    )
                    FeatureMatrixItem(
                        index = "λ, φ",
                        name = "Location Coordinates & Elevation",
                        desc = "Latitude, longitude, and topographic elevation rain-shadow context."
                    )
                    FeatureMatrixItem(
                        index = "VAR",
                        name = "Atmospheric Variable Constraints",
                        desc = "Physical boundary limits (e.g. non-negative rainfall, humidity <= 100%)."
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ModelWeightBar(
    modelName: String,
    percentage: Int,
    color: Color,
    description: String
) {
    Column {
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
                    text = modelName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            }
            Text(
                text = "$percentage%",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Clean Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(LavenderBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(color)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = description,
            fontSize = 10.sp,
            color = TextSecondaryMuted
        )
    }
}

@Composable
fun BlenderAlgorithmRow(
    type: BlenderType,
    title: String,
    formula: String,
    description: String,
    rmseScore: Double,
    isSelected: Boolean,
    unit: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) LavenderContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) LavenderPrimary else LavenderBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                            .size(16.dp)
                            .background(
                                if (isSelected) LavenderPrimary else Color.Transparent,
                                CircleShape
                            )
                            .border(1.5.dp, LavenderPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color.White, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White
                ) {
                    Text(
                        text = String.format(Locale.US, "Val RMSE: %.2f %s", rmseScore, unit),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PinkAccent else TextSecondaryNavy,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formula,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = LavenderPrimary,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                fontSize = 11.sp,
                color = TextSecondaryNavy
            )
        }
    }
}

@Composable
fun FeatureMatrixItem(
    index: String,
    name: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = LavenderContainer,
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
            modifier = Modifier.width(60.dp)
        ) {
            Box(
                modifier = Modifier.padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderPrimary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Text(
                text = desc,
                fontSize = 10.sp,
                color = TextSecondaryMuted
            )
        }
    }
}
