package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherVariable
import com.example.ui.components.KpiCardsRow
import com.example.ui.components.WeatherCanvasChart
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.viewmodel.WeatherViewModel

@Composable
fun ChartsScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedVar by viewModel.selectedVariable.collectAsState()
    val selectedLead by viewModel.selectedLeadFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val result by viewModel.evaluationResult.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("charts_screen")
    ) {
        // Variable Chips
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

        Spacer(modifier = Modifier.height(10.dp))

        // Lead Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lead:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FilterChip(
                selected = selectedLead == null,
                onClick = { viewModel.selectLeadFilter(null) },
                label = { Text("All", fontSize = 11.sp) },
                shape = RoundedCornerShape(8.dp)
            )
            for (d in 1..7) {
                FilterChip(
                    selected = selectedLead == d,
                    onClick = { viewModel.selectLeadFilter(if (selectedLead == d) null else d) },
                    label = { Text("+$d D", fontSize = 11.sp) },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        result?.let { res ->
            val filteredPredictions = remember(res.testPredictions, selectedLead) {
                if (selectedLead == null) res.testPredictions
                else res.testPredictions.filter { it.record.leadTimeDays == selectedLead }
            }

            WeatherCanvasChart(
                predictions = filteredPredictions,
                variable = selectedVar,
                title = "${selectedCity.displayName} Multi-Model Consensus",
                isLiveForecast = false
            )

            Spacer(modifier = Modifier.height(14.dp))

            // KPI Cards
            KpiCardsRow(
                metrics = res.bestBlenderMetrics,
                intervalCoverage = res.intervalCoverage80,
                activeBlender = res.selectedBlender
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Model Weight Distribution Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Model Weights (Learned)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    res.blenderWeights.forEach { (model, weight) ->
                        val pct = (weight * 100).toInt().coerceIn(0, 100)
                        Column(modifier = Modifier.padding(vertical = 3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = model.displayName, style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = "$pct%",
                                    style = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                    color = model.color
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            LinearProgressIndicator(
                                progress = { pct / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(CircleShape),
                                color = model.color,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        } ?: run {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SkyBluePrimary)
                }
            }
        }
    }
}
