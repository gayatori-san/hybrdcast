package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlendedPrediction
import com.example.data.model.NwpModel
import com.example.data.model.WeatherVariable
import com.example.ui.theme.BlendHighlight
import com.example.ui.theme.ModelEcmwf
import com.example.ui.theme.ModelGem
import com.example.ui.theme.ModelGfs
import com.example.ui.theme.ModelIcon
import com.example.ui.theme.ObservationDot
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.UncertaintyFill
import com.example.ui.theme.UncertaintyStroke
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeatherCanvasChart(
    predictions: List<BlendedPrediction>,
    variable: WeatherVariable,
    modifier: Modifier = Modifier,
    title: String = "Multi-Model Forecast vs Ground Truth",
    isLiveForecast: Boolean = false
) {
    if (predictions.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No forecast data available for selected filter",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val modelVisibility = remember {
        mutableStateMapOf<String, Boolean>().apply {
            put("Blend", true)
            put("Interval", true)
            put("Obs", true)
            put("ECMWF", true)
            put("GFS", true)
            put("ICON", true)
            put("GEM", true)
        }
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weather_canvas_chart_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isLiveForecast) "Lead Day +1 to +7 (Operational Outlook)" else "Past 14 Days Verification Set (ERA5 Ground Truth)",
                        style = MaterialTheme.typography.labelSmall,
                        color = SkyBluePrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                val primaryColor = SkyBluePrimary

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(predictions.size) {
                            detectTapGestures(
                                onTap = { offset ->
                                    val paddingLeft = 40.dp.toPx()
                                    val paddingRight = 16.dp.toPx()
                                    val chartWidth = size.width - paddingLeft - paddingRight
                                    if (chartWidth > 0 && offset.x >= paddingLeft && offset.x <= size.width - paddingRight) {
                                        val frac = (offset.x - paddingLeft) / chartWidth
                                        val idx = (frac * (predictions.size - 1)).toInt().coerceIn(0, predictions.size - 1)
                                        selectedIndex = if (selectedIndex == idx) null else idx
                                    } else {
                                        selectedIndex = null
                                    }
                                }
                            )
                        }
                        .pointerInput(predictions.size) {
                            detectDragGestures(
                                onDrag = { change, _ ->
                                    val paddingLeft = 40.dp.toPx()
                                    val paddingRight = 16.dp.toPx()
                                    val chartWidth = size.width - paddingLeft - paddingRight
                                    if (chartWidth > 0) {
                                        val frac = ((change.position.x - paddingLeft) / chartWidth).coerceIn(0f, 1f)
                                        val idx = (frac * (predictions.size - 1)).toInt().coerceIn(0, predictions.size - 1)
                                        selectedIndex = idx
                                    }
                                }
                            )
                        }
                ) {
                    val pLeft = 40.dp.toPx()
                    val pRight = 16.dp.toPx()
                    val pTop = 14.dp.toPx()
                    val pBottom = 26.dp.toPx()

                    val chartW = size.width - pLeft - pRight
                    val chartH = size.height - pTop - pBottom

                    var minVal = Double.MAX_VALUE
                    var maxVal = -Double.MAX_VALUE

                    for (p in predictions) {
                        minVal = min(minVal, p.lowerBound80)
                        maxVal = max(maxVal, p.upperBound80)
                        p.record.observed?.let {
                            minVal = min(minVal, it)
                            maxVal = max(maxVal, it)
                        }
                        for (v in p.record.modelForecasts.values) {
                            minVal = min(minVal, v)
                            maxVal = max(maxVal, v)
                        }
                    }

                    if (minVal >= maxVal) {
                        minVal -= 1.0
                        maxVal += 1.0
                    }
                    val pad = (maxVal - minVal) * 0.08
                    minVal -= pad
                    maxVal += pad

                    fun getX(idx: Int): Float = pLeft + (idx.toFloat() / (predictions.size - 1).coerceAtLeast(1)) * chartW
                    fun getY(v: Double): Float = pTop + chartH - ((v - minVal) / (maxVal - minVal)).toFloat() * chartH

                    // Grid lines & labels
                    val ySteps = 4
                    for (i in 0..ySteps) {
                        val yVal = minVal + (maxVal - minVal) * (i.toDouble() / ySteps)
                        val yPos = getY(yVal)
                        drawLine(
                            color = gridColor,
                            start = Offset(pLeft, yPos),
                            end = Offset(pLeft + chartW, yPos),
                            strokeWidth = 1f
                        )
                        val label = String.format(Locale.US, "%.1f", yVal)
                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            topLeft = Offset(4f, yPos - 7.dp.toPx()),
                            style = TextStyle(
                                color = labelColor,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    // 80% CI Envelope
                    if (modelVisibility["Interval"] == true) {
                        val bandPath = Path()
                        bandPath.moveTo(getX(0), getY(predictions[0].upperBound80))
                        for (i in 1 until predictions.size) {
                            bandPath.lineTo(getX(i), getY(predictions[i].upperBound80))
                        }
                        for (i in predictions.size - 1 downTo 0) {
                            bandPath.lineTo(getX(i), getY(predictions[i].lowerBound80))
                        }
                        bandPath.close()

                        drawPath(bandPath, color = UncertaintyFill)

                        val upperPath = Path()
                        val lowerPath = Path()
                        upperPath.moveTo(getX(0), getY(predictions[0].upperBound80))
                        lowerPath.moveTo(getX(0), getY(predictions[0].lowerBound80))
                        for (i in 1 until predictions.size) {
                            upperPath.lineTo(getX(i), getY(predictions[i].upperBound80))
                            lowerPath.lineTo(getX(i), getY(predictions[i].lowerBound80))
                        }
                        drawPath(upperPath, color = UncertaintyStroke, style = Stroke(width = 1.2f))
                        drawPath(lowerPath, color = UncertaintyStroke, style = Stroke(width = 1.2f))
                    }

                    // Individual NWP Models
                    for (model in NwpModel.entries) {
                        val key = when (model) {
                            NwpModel.ECMWF -> "ECMWF"
                            NwpModel.GFS -> "GFS"
                            NwpModel.ICON -> "ICON"
                            NwpModel.GEM -> "GEM"
                        }
                        if (modelVisibility[key] == true) {
                            val modelPath = Path()
                            var first = true
                            for (i in predictions.indices) {
                                val v = predictions[i].record.modelForecasts[model] ?: predictions[i].record.ensembleMean
                                val x = getX(i)
                                val y = getY(v)
                                if (first) {
                                    modelPath.moveTo(x, y)
                                    first = false
                                } else {
                                    modelPath.lineTo(x, y)
                                }
                            }
                            drawPath(
                                path = modelPath,
                                color = model.color.copy(alpha = 0.75f),
                                style = Stroke(width = 1.6f, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Hybrid AI Blend Line
                    if (modelVisibility["Blend"] == true) {
                        val blendPath = Path()
                        blendPath.moveTo(getX(0), getY(predictions[0].blendedValue))
                        for (i in 1 until predictions.size) {
                            blendPath.lineTo(getX(i), getY(predictions[i].blendedValue))
                        }
                        drawPath(
                            path = blendPath,
                            color = BlendHighlight.copy(alpha = 0.22f),
                            style = Stroke(width = 6f, cap = StrokeCap.Round)
                        )
                        drawPath(
                            path = blendPath,
                            color = BlendHighlight,
                            style = Stroke(width = 3f, cap = StrokeCap.Round)
                        )
                    }

                    // Observation Dots
                    if (!isLiveForecast && modelVisibility["Obs"] == true) {
                        for (i in predictions.indices) {
                            val obs = predictions[i].record.observed
                            if (obs != null) {
                                val x = getX(i)
                                val y = getY(obs)
                                drawCircle(
                                    color = ObservationDot,
                                    radius = 3.2f,
                                    center = Offset(x, y)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 3.2f,
                                    center = Offset(x, y),
                                    style = Stroke(width = 1f)
                                )
                            }
                        }
                    }

                    // Vertical scrubbing line
                    selectedIndex?.let { idx ->
                        if (idx in predictions.indices) {
                            val xPos = getX(idx)
                            drawLine(
                                color = primaryColor,
                                start = Offset(xPos, pTop),
                                end = Offset(xPos, pTop + chartH),
                                strokeWidth = 2f
                            )
                            val blendY = getY(predictions[idx].blendedValue)
                            drawCircle(
                                color = BlendHighlight,
                                radius = 6f,
                                center = Offset(xPos, blendY)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.5f,
                                center = Offset(xPos, blendY)
                            )
                        }
                    }
                }
            }

            // Scrubbed Point Details Card
            selectedIndex?.let { idx ->
                val p = predictions.getOrNull(idx)
                if (p != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SkyBluePrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${p.record.timestamp.replace("T", " ")} (D+${p.record.leadTimeDays})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyBluePrimary
                                )
                                Text(
                                    text = "Spread σ: ${String.format(Locale.US, "%.2f", p.record.ensembleSpread)} ${variable.unit}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(BlendHighlight, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Blend: ${String.format(Locale.US, "%.2f", p.blendedValue)} ${variable.unit}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BlendHighlight
                                    )
                                }
                                Text(
                                    text = "80% CI: [${String.format(Locale.US, "%.1f", p.lowerBound80)} – ${String.format(Locale.US, "%.1f", p.upperBound80)}]",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (p.record.observed != null) {
                                    Text(
                                        text = "Obs: ${String.format(Locale.US, "%.2f", p.record.observed)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ObservationDot
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // User-friendly Model Logo Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ModelLogoChip(
                    label = "AI Blend",
                    icon = Icons.Default.AutoAwesome,
                    color = BlendHighlight,
                    selected = modelVisibility["Blend"] == true,
                    onClick = { modelVisibility["Blend"] = !(modelVisibility["Blend"] ?: true) }
                )
                if (!isLiveForecast) {
                    ModelLogoChip(
                        label = "Obs (ERA5)",
                        icon = Icons.Default.FiberManualRecord,
                        color = ObservationDot,
                        selected = modelVisibility["Obs"] == true,
                        onClick = { modelVisibility["Obs"] = !(modelVisibility["Obs"] ?: true) }
                    )
                }
                ModelLogoChip(
                    label = "80% CI",
                    icon = Icons.Default.Shield,
                    color = SkyBluePrimary,
                    selected = modelVisibility["Interval"] == true,
                    onClick = { modelVisibility["Interval"] = !(modelVisibility["Interval"] ?: true) }
                )
                ModelLogoChip(
                    label = "ECMWF",
                    icon = Icons.Default.Public,
                    color = ModelEcmwf,
                    selected = modelVisibility["ECMWF"] == true,
                    onClick = { modelVisibility["ECMWF"] = !(modelVisibility["ECMWF"] ?: true) }
                )
                ModelLogoChip(
                    label = "GFS",
                    icon = Icons.Default.Radar,
                    color = ModelGfs,
                    selected = modelVisibility["GFS"] == true,
                    onClick = { modelVisibility["GFS"] = !(modelVisibility["GFS"] ?: true) }
                )
                ModelLogoChip(
                    label = "ICON",
                    icon = Icons.Default.Speed,
                    color = ModelIcon,
                    selected = modelVisibility["ICON"] == true,
                    onClick = { modelVisibility["ICON"] = !(modelVisibility["ICON"] ?: true) }
                )
                ModelLogoChip(
                    label = "GEM",
                    icon = Icons.Default.Language,
                    color = ModelGem,
                    selected = modelVisibility["GEM"] == true,
                    onClick = { modelVisibility["GEM"] = !(modelVisibility["GEM"] ?: true) }
                )
            }
        }
    }
}

@Composable
private fun ModelLogoChip(
    label: String,
    icon: ImageVector,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.15f),
            selectedLabelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = if (selected) color.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(30.dp)
    )
}
