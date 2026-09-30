package com.example.ml

import com.example.data.model.WeatherVariable
import kotlin.math.abs
import kotlin.math.max

data class UncertaintyParameters(
    val q10Residual: Double,
    val q90Residual: Double,
    val base80Width: Double,
    val meanSpread: Double
)

class UncertaintyEstimator {

    fun calibrate(
        validationResiduals: List<Double>,
        validationSpreads: List<Double>
    ): UncertaintyParameters {
        if (validationResiduals.isEmpty()) {
            return UncertaintyParameters(-1.5, 1.5, 3.0, 1.0)
        }

        val sortedAbsResiduals = validationResiduals.map { abs(it) }.sorted()
        // 80% coverage corresponds to the 80th percentile of absolute residuals
        val p80Index = ((sortedAbsResiduals.size - 1) * 0.80).toInt().coerceIn(0, sortedAbsResiduals.size - 1)
        val p80Residual = max(0.2, sortedAbsResiduals[p80Index])

        val meanSpread = max(0.1, if (validationSpreads.isNotEmpty()) validationSpreads.average() else 1.0)

        return UncertaintyParameters(
            q10Residual = -p80Residual,
            q90Residual = p80Residual,
            base80Width = p80Residual * 2.0,
            meanSpread = meanSpread
        )
    }

    fun computeBounds(
        blendedValue: Double,
        currentSpread: Double,
        variable: WeatherVariable,
        params: UncertaintyParameters
    ): Pair<Double, Double> {
        val spreadFactor = (0.5 + 0.5 * (currentSpread / params.meanSpread)).coerceIn(0.6, 2.2)
        val halfWidth = (params.base80Width / 2.0) * spreadFactor

        var lower = blendedValue - halfWidth
        var upper = blendedValue + halfWidth

        // Physical clamping
        if (variable == WeatherVariable.PRECIPITATION || variable == WeatherVariable.WIND_SPEED) {
            lower = max(0.0, lower)
            upper = max(lower, upper)
        }

        return Pair(lower, upper)
    }

    fun calculateCoverage(
        testActuals: List<Double>,
        lowerBounds: List<Double>,
        upperBounds: List<Double>
    ): Double {
        if (testActuals.isEmpty()) return 80.0
        var insideCount = 0
        for (i in testActuals.indices) {
            val y = testActuals[i]
            if (y >= lowerBounds[i] && y <= upperBounds[i]) {
                insideCount++
            }
        }
        return (insideCount.toDouble() / testActuals.size) * 100.0
    }
}
