package com.example.ml

import com.example.data.model.ContingencyTable
import com.example.data.model.VerificationMetrics
import com.example.data.model.WeatherVariable
import kotlin.math.abs
import kotlin.math.sqrt

class MetricsCalculator {

    fun computeVerificationMetrics(
        predictions: List<Double>,
        observations: List<Double>,
        bestSingleModelRmse: Double,
        ensembleMeanRmse: Double,
        variable: WeatherVariable
    ): VerificationMetrics {
        require(predictions.size == observations.size) { "Size mismatch" }
        val n = predictions.size
        if (n == 0) {
            return VerificationMetrics(
                bias = 0.0,
                mae = 0.0,
                rmse = 0.0,
                pearsonR = 1.0,
                skillImprovementVsBest = 0.0,
                skillImprovementVsMean = 0.0,
                sampleCount = 0
            )
        }

        var sumErr = 0.0
        var sumAbsErr = 0.0
        var sumSqErr = 0.0
        val pMean = predictions.average()
        val oMean = observations.average()

        var cov = 0.0
        var varP = 0.0
        var varO = 0.0

        for (i in 0 until n) {
            val p = predictions[i]
            val o = observations[i]
            val err = p - o
            sumErr += err
            sumAbsErr += abs(err)
            sumSqErr += err * err

            val dp = p - pMean
            val dobs = o - oMean
            cov += dp * dobs
            varP += dp * dp
            varO += dobs * dobs
        }

        val bias = sumErr / n
        val mae = sumAbsErr / n
        val rmse = sqrt(sumSqErr / n)
        val denom = sqrt(varP * varO)
        val pearsonR = if (denom > 1e-9) (cov / denom).coerceIn(-1.0, 1.0) else 0.0

        val skillVsBest = if (bestSingleModelRmse > 1e-6) {
            ((bestSingleModelRmse - rmse) / bestSingleModelRmse) * 100.0
        } else 0.0

        val skillVsMean = if (ensembleMeanRmse > 1e-6) {
            ((ensembleMeanRmse - rmse) / ensembleMeanRmse) * 100.0
        } else 0.0

        val contingency = if (variable == WeatherVariable.PRECIPITATION) {
            computeContingency(predictions, observations, variable.precipitationThreshold)
        } else null

        return VerificationMetrics(
            bias = bias,
            mae = mae,
            rmse = rmse,
            pearsonR = pearsonR,
            skillImprovementVsBest = skillVsBest,
            skillImprovementVsMean = skillVsMean,
            sampleCount = n,
            contingency = contingency
        )
    }

    fun computeRmse(predictions: List<Double>, observations: List<Double>): Double {
        if (predictions.isEmpty()) return 0.0
        var sumSq = 0.0
        for (i in predictions.indices) {
            val diff = predictions[i] - observations[i]
            sumSq += diff * diff
        }
        return sqrt(sumSq / predictions.size)
    }

    fun computeContingency(
        predictions: List<Double>,
        observations: List<Double>,
        threshold: Double = 0.1
    ): ContingencyTable {
        var hits = 0
        var falseAlarms = 0
        var misses = 0
        var correctNegatives = 0

        for (i in predictions.indices) {
            val predRain = predictions[i] >= threshold
            val obsRain = observations[i] >= threshold

            when {
                predRain && obsRain -> hits++
                predRain && !obsRain -> falseAlarms++
                !predRain && obsRain -> misses++
                else -> correctNegatives++
            }
        }

        return ContingencyTable(
            hits = hits,
            falseAlarms = falseAlarms,
            misses = misses,
            correctNegatives = correctNegatives
        )
    }
}
