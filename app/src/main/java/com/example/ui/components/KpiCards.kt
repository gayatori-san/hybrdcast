package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlenderType
import com.example.data.model.VerificationMetrics
import com.example.ui.theme.SkyBluePrimary
import java.util.Locale

@Composable
fun KpiCardsRow(
    metrics: VerificationMetrics,
    intervalCoverage: Double,
    activeBlender: BlenderType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiCard(
                title = "Blend beats best model by",
                value = String.format(Locale.US, "+%.1f%%", metrics.skillImprovementVsBest),
                subtitle = "Superior to top single NWP",
                tooltip = "Skill Score: Percent reduction in RMSE forecast error achieved by HybridCast compared to the best individual supercomputer physics simulation (ECMWF/GFS).",
                icon = Icons.Default.TrendingUp,
                accentColor = Color(0xFF10B981),
                testTag = "kpi_skill_improvement",
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                title = "Confidence check: inside band",
                value = String.format(Locale.US, "%.1f%%", intervalCoverage),
                subtitle = "Target: 80% coverage",
                tooltip = "Empirical Interval Coverage: The percentage of real-world ground truth observations that landed safely inside HybridCast's calibrated 80% prediction envelope.",
                icon = Icons.Default.Shield,
                accentColor = SkyBluePrimary,
                testTag = "kpi_interval_coverage",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiCard(
                title = "Active AI Blender",
                value = when (activeBlender) {
                    BlenderType.RIDGE -> "Ridge L2"
                    BlenderType.RESIDUAL_GBDT -> "GBDT Tree"
                    BlenderType.ADAPTIVE_MSE -> "Adapt-MSE"
                    BlenderType.EQUAL_WEIGHT -> "Simple Mean"
                },
                subtitle = "Auto-selected on Val set",
                tooltip = "The blending algorithm that scored the lowest validation RMSE on out-of-sample data. Automatically chosen between closed-form Ridge, Residual GBDT, and Inverse-MSE.",
                icon = Icons.Default.ElectricBolt,
                accentColor = Color(0xFFF59E0B),
                testTag = "kpi_active_blender",
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                title = "IMD Verification Status",
                value = "SOP 2021",
                subtitle = "RMSE: ${String.format(Locale.US, "%.2f", metrics.rmse)} (r=${String.format(Locale.US, "%.2f", metrics.pearsonR)})",
                tooltip = "Strictly conforms to IMD NWP & Forecast Verification SOP 2021 with spatial matching, lead time stratification (Day 1-7), and Pearson correlation tracking.",
                icon = Icons.Default.CheckCircle,
                accentColor = Color(0xFF8B5CF6),
                testTag = "kpi_verification_sop",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    tooltip: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    NeomorphicCard(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                GlossaryTooltipIcon(term = title, definition = tooltip)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
