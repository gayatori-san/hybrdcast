package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContingencyTable
import com.example.data.model.VerificationMetrics
import com.example.data.model.WeatherVariable
import com.example.ui.components.SkillCurveChart
import com.example.ui.theme.BlendHighlight
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.viewmodel.WeatherViewModel
import java.util.Locale

@Composable
fun EvaluationScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedVar by viewModel.selectedVariable.collectAsState()
    val result by viewModel.evaluationResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("evaluation_screen")
    ) {
        // Clean Header Row with Export CSV
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${selectedCity.displayName} Scores",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = selectedVar.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = SkyBluePrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = { viewModel.exportVerificationCsv(context) },
                modifier = Modifier.testTag("export_eval_csv_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        result?.let { res ->
            // Clean Score Table
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SkyBluePrimary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Model", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                        Text("BIAS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                        Text("MAE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                        Text("RMSE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                        Text("Skill", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.0f))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // AI Blend Row
                    ScoreRow("✨ Blend", res.bestBlenderMetrics, isBlend = true)

                    // Model Rows
                    res.baselineMetrics.forEach { (name, m) ->
                        ScoreRow(name, m, isBlend = false)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Skill vs Lead Curve
            SkillCurveChart(
                curveData = res.leadTimeRmseCurve,
                variable = selectedVar
            )

            // Categorical Rain Section if precipitation
            if (selectedVar == WeatherVariable.PRECIPITATION && res.bestBlenderMetrics.contingency != null) {
                Spacer(modifier = Modifier.height(14.dp))
                val c = res.bestBlenderMetrics.contingency
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Hit Rate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(String.format(Locale.US, "%.0f%%", c.pod * 100), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                        Column {
                            Text("False Alarm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(String.format(Locale.US, "%.0f%%", c.far * 100), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        }
                        Column {
                            Text("Threat Score", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(String.format(Locale.US, "%.2f", c.csi), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SkyBluePrimary)
                        }
                    }
                }
            }

            // Warning Banner if lead day divergence
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEF2F2),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚠️ Warning: Forecast error increases with lead time (Day 5-7)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB91C1C)
                    )
                }
            }
        } ?: run {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SkyBluePrimary)
                }
            }
        }
    }
}

@Composable
private fun ScoreRow(
    name: String,
    metrics: VerificationMetrics,
    isBlend: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isBlend) BlendHighlight.copy(alpha = 0.08f) else Color.Transparent, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isBlend) FontWeight.Bold else FontWeight.Normal,
            color = if (isBlend) BlendHighlight else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = String.format(Locale.US, "%.2f", metrics.bias),
            style = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = String.format(Locale.US, "%.2f", metrics.mae),
            style = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = String.format(Locale.US, "%.2f", metrics.rmse),
            style = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = if (isBlend) BlendHighlight else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = if (isBlend) String.format(Locale.US, "+%.0f%%", metrics.skillImprovementVsBest) else "—",
            style = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = if (isBlend) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.0f)
        )
    }
}
