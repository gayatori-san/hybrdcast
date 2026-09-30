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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun ResearchScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("research_screen")
    ) {
        // Research Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LavenderContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
                ) {
                    Text(
                        text = "SIH 26081 METHODOLOGY & SCIENTIFIC GROUNDING",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LavenderDark,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Context-Dependent NWP Reliability",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Why fixed unweighted averaging fails in operational meteorology and how dynamic machine learning blending overcomes systematic physical parameterization biases.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryNavy,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Pipeline from Problem Statement
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("methodology_pipeline_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "END-TO-END SYSTEM BLENDING PIPELINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderPrimary,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(14.dp))

                val pipelineSteps = listOf(
                    Pair("Historical Forecasts + Observed Weather", "14-day rolling training data from Open-Meteo REST & ERA5 ground truth."),
                    Pair("Data Preprocessing", "Temporal alignment, lead time indexing, UTC timezone synchronization."),
                    Pair("Feature Engineering", "Diurnal sine/cosine harmonics, inter-model spread, JJAS monsoon flag."),
                    Pair("AI Model Training", "Non-leaking 70/15/15 chronological split on Ridge and Residual Decision Trees."),
                    Pair("Dynamic Model Weights", "Context-aware inverse error covariance calculation per lead time."),
                    Pair("Bias Correction", "Non-linear subtraction of systematic regional drift (e.g. GFS NE wet bias)."),
                    Pair("Blended Forecast", "Optimal weighted linear consensus combination beating individual models."),
                    Pair("Uncertainty Estimation", "80% confidence intervals dynamically scaled by ensemble spread σ."),
                    Pair("Validation", "Standard Operating Procedure (IMD 2021) verification: MAE, RMSE, Bias, POD, FAR, CSI.")
                )

                pipelineSteps.forEachIndexed { idx, (stepTitle, stepDesc) ->
                    PipelineVerticalStep(
                        stepNumber = idx + 1,
                        title = stepTitle,
                        description = stepDesc,
                        isLast = idx == pipelineSteps.size - 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // The Scientific Rationale: Context-Dependent Reliability
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(LavenderContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = LavenderPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Context-Dependent Model Reliability",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "A fundamental insight of operational meteorology is that NO single NWP model is universally optimal everywhere or at all lead times:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryNavy
                )

                Spacer(modifier = Modifier.height(8.dp))

                BulletPoint(
                    title = "Geographic & Topographic Sensitivity",
                    desc = "ECMWF IFS excels in large-scale mid-latitude and upper-troposphere dynamics, whereas high-resolution ICON captures complex mountainous terrain in the Western Ghats."
                )
                BulletPoint(
                    title = "Diurnal Peak Thermal Phase Lags",
                    desc = "GFS models often anticipate boundary layer heating 1-2 hours earlier than observed over central India, creating a systematic daytime warm bias."
                )
                BulletPoint(
                    title = "Precipitation Regimes",
                    desc = "During convective thunderstorm events, coarse hydrostatic grids under-predict peak rainfall rates, whereas GFS tends to over-spread light rain over broader footprints."
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rather than assigning every NWP model a fixed arbitrary weight, HybridCast continuously estimates model skill conditioned on lead time, season, and atmospheric state.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = LavenderPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mathematical Formulation Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "MATHEMATICAL FORMULATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderPrimary,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormulaCard(
                    title = "1. Closed-Form L2 Ridge Blending",
                    equation = "w = (XᵀX + λI)⁻¹ Xᵀy",
                    explanation = "X contains multi-model forecasts with an intercept column. L2 penalty λ=0.5 dampens collinearity between correlated NWP models."
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormulaCard(
                    title = "2. Residual Tree Gradient Boosting",
                    equation = "rᵢ = yᵢ - M̄ᵢ ;  f(x) = Σ γₘ Tₘ(x)",
                    explanation = "Trains shallow decision trees directly on ensemble mean residuals, capturing non-linear diurnal phase errors."
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormulaCard(
                    title = "3. Dynamic 80% Uncertainty Bounds",
                    equation = "Half-Width = Δq₈₀ · [0.5 + 0.5 (σ_M / σ̄)]",
                    explanation = "Empirical 80th percentile residual quantile scaled by the ratio of current ensemble spread σ_M to mean historical spread."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Official Scientific Citations
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bookmarks,
                        contentDescription = null,
                        tint = LavenderPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "OFFICIAL GOVERNMENT & RESEARCH CITATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LavenderPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                CitationItem(
                    title = "India Meteorological Department (IMD) NWP Report (2022)",
                    finding = "Documented persistent GFS wet bias over North-East India (Guwahati) and dry bias for extreme precipitation events (>65 mm/day)."
                )
                CitationItem(
                    title = "NCMRWF Annual Scientific Report (2024-25)",
                    finding = "Demonstrated 20% to 80% RMSE reduction when applying machine learning post-processing models to multi-model reanalysis."
                )
                CitationItem(
                    title = "IMD Standard Operating Procedure (SOP 2021)",
                    finding = "Prescribed standard meteorological metrics (BIAS, MAE, RMSE, Pearson r, POD, FAR, CSI) for quantitative forecast verification."
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PipelineVerticalStep(
    stepNumber: Int,
    title: String,
    description: String,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(LavenderContainer, CircleShape)
                    .border(1.dp, LavenderPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$stepNumber",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderPrimary
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(34.dp)
                        .background(LavenderBorder)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 10.sp,
                color = TextSecondaryMuted,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun BulletPoint(
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .background(LavenderPrimary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Text(
                text = desc,
                fontSize = 10.sp,
                color = TextSecondaryMuted,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun FormulaCard(
    title: String,
    equation: String,
    explanation: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = LavenderContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = equation,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LavenderPrimary,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = explanation,
                fontSize = 10.sp,
                color = TextSecondaryNavy
            )
        }
    }
}

@Composable
fun CitationItem(
    title: String,
    finding: String
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = finding,
            fontSize = 10.sp,
            color = TextSecondaryMuted,
            lineHeight = 14.sp
        )
    }
}
