package com.example.data.model

import java.util.Locale
import kotlin.math.max
import kotlin.math.min

data class DailyForecastSummary(
    val dayNumber: Int,
    val dateLabel: String,
    val avgBlend: Double,
    val minVal: Double,
    val maxVal: Double,
    val avgLower80: Double,
    val avgUpper80: Double,
    val highSpreadDivergence: Boolean
)

fun computeDailySummaries(predictions: List<BlendedPrediction>): List<DailyForecastSummary> {
    val list = mutableListOf<DailyForecastSummary>()
    for (d in 1..7) {
        val dayPreds = predictions.filter { it.record.leadTimeDays == d }
        if (dayPreds.isNotEmpty()) {
            val avgBlend = dayPreds.map { it.blendedValue }.average()
            val minVal = dayPreds.minOf { it.blendedValue }
            val maxVal = dayPreds.maxOf { it.blendedValue }
            val avgLower = dayPreds.map { it.lowerBound80 }.average()
            val avgUpper = dayPreds.map { it.upperBound80 }.average()
            val avgSpread = dayPreds.map { it.record.ensembleSpread }.average()
            val isHighSpread = avgSpread > 2.0

            val dateStr = dayPreds.first().record.timestamp.substringBefore("T")
            list.add(
                DailyForecastSummary(
                    dayNumber = d,
                    dateLabel = dateStr,
                    avgBlend = avgBlend,
                    minVal = minVal,
                    maxVal = maxVal,
                    avgLower80 = avgLower,
                    avgUpper80 = avgUpper,
                    highSpreadDivergence = isHighSpread
                )
            )
        }
    }
    return list
}
