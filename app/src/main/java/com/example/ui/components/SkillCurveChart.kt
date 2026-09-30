package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherVariable
import com.example.ui.theme.BlendHighlight
import com.example.ui.theme.ModelEcmwf
import com.example.ui.theme.ModelGfs
import com.example.ui.theme.ModelIcon
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Composable
fun SkillCurveChart(
    curveData: Map<Int, Map<String, Double>>,
    variable: WeatherVariable,
    modifier: Modifier = Modifier
) {
    if (curveData.isEmpty()) {
        return
    }

    val textMeasurer = rememberTextMeasurer()
    val days = curveData.keys.sorted()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Forecast Skill vs. Lead Time (RMSE Degradation)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Verification across Lead Days 1 to 7 (Lower is Better)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pLeft = 40.dp.toPx()
                    val pRight = 16.dp.toPx()
                    val pTop = 14.dp.toPx()
                    val pBottom = 26.dp.toPx()

                    val chartW = size.width - pLeft - pRight
                    val chartH = size.height - pTop - pBottom

                    // Find min/max RMSE
                    var minRmse = Double.MAX_VALUE
                    var maxRmse = -Double.MAX_VALUE

                    for (mMap in curveData.values) {
                        for (v in mMap.values) {
                            minRmse = min(minRmse, v)
                            maxRmse = max(maxRmse, v)
                        }
                    }

                    if (minRmse >= maxRmse) {
                        minRmse = 0.5
                        maxRmse = 3.0
                    }
                    val pad = (maxRmse - minRmse) * 0.1
                    minRmse = max(0.0, minRmse - pad)
                    maxRmse += pad

                    fun getX(dayIndex: Int): Float =
                        pLeft + (dayIndex.toFloat() / (days.size - 1).coerceAtLeast(1)) * chartW

                    fun getY(rmse: Double): Float =
                        pTop + chartH - ((rmse - minRmse) / (maxRmse - minRmse)).toFloat() * chartH

                    // Horizontal Grid Lines
                    val steps = 3
                    for (i in 0..steps) {
                        val yVal = minRmse + (maxRmse - minRmse) * (i.toDouble() / steps)
                        val yPos = getY(yVal)
                        drawLine(
                            color = gridColor,
                            start = Offset(pLeft, yPos),
                            end = Offset(pLeft + chartW, yPos),
                            strokeWidth = 1f
                        )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = String.format(Locale.US, "%.1f", yVal),
                            topLeft = Offset(4f, yPos - 6.dp.toPx()),
                            style = TextStyle(
                                color = labelColor,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    // X-Axis Labels (Day 1..7)
                    for (i in days.indices) {
                        val x = getX(i)
                        drawText(
                            textMeasurer = textMeasurer,
                            text = "D+${days[i]}",
                            topLeft = Offset(x - 8.dp.toPx(), pTop + chartH + 6.dp.toPx()),
                            style = TextStyle(
                                color = labelColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    val lineSeries = listOf(
                        Triple("Hybrid AI Blend", BlendHighlight, 2.8f),
                        Triple("ECMWF IFS", ModelEcmwf, 1.6f),
                        Triple("IMD-GFS (NCEP)", ModelGfs, 1.6f),
                        Triple("DWD ICON", ModelIcon, 1.6f),
                        Triple("Ensemble Mean", Color(0xFF94A3B8), 1.6f)
                    )

                    for ((seriesName, color, strokeW) in lineSeries) {
                        val path = Path()
                        var hasFirst = false

                        for (i in days.indices) {
                            val d = days[i]
                            val rmse = curveData[d]?.get(seriesName) ?: continue
                            val x = getX(i)
                            val y = getY(rmse)
                            if (!hasFirst) {
                                path.moveTo(x, y)
                                hasFirst = true
                            } else {
                                path.lineTo(x, y)
                            }
                            drawCircle(
                                color = color,
                                radius = if (seriesName == "Hybrid AI Blend") 4.5f else 3f,
                                center = Offset(x, y)
                            )
                        }

                        if (hasFirst) {
                            drawPath(
                                path = path,
                                color = color,
                                style = Stroke(width = strokeW, cap = StrokeCap.Round)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendIndicator(label = "AI Blend", color = BlendHighlight, isBold = true)
                LegendIndicator(label = "ECMWF", color = ModelEcmwf)
                LegendIndicator(label = "IMD-GFS", color = ModelGfs)
                LegendIndicator(label = "ICON", color = ModelIcon)
                LegendIndicator(label = "Ens. Mean", color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
private fun LegendIndicator(label: String, color: Color, isBold: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(if (isBold) 9.dp else 7.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = 10.sp,
            color = if (isBold) color else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
