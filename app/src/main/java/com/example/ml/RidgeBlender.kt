package com.example.ml

import com.example.data.model.NwpModel
import com.example.data.model.TidyForecastRecord
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class RidgeModelResult(
    val weights: Map<NwpModel, Double>,
    val intercept: Double,
    val diurnalSinWeight: Double,
    val diurnalCosWeight: Double,
    val lambda: Double,
    val r2Score: Double
)

class RidgeBlender(private val lambda: Double = 0.5) {

    fun train(records: List<TidyForecastRecord>): RidgeModelResult {
        val validRecords = records.filter { it.observed != null }
        if (validRecords.isEmpty()) {
            return fallbackResult()
        }

        val n = validRecords.size
        val models = NwpModel.entries.toTypedArray()
        val numFeatures = models.size + 3 // 4 models + hourSin + hourCos + intercept (1.0)

        val X = Matrix(n, numFeatures)
        val y = DoubleArray(n)

        for (i in 0 until n) {
            val rec = validRecords[i]
            y[i] = rec.observed!!
            for (m in models.indices) {
                X[i, m] = rec.modelForecasts[models[m]] ?: rec.ensembleMean
            }
            val hourAngle = 2.0 * PI * rec.hourOfDay / 24.0
            X[i, models.size] = sin(hourAngle)
            X[i, models.size + 1] = cos(hourAngle)
            X[i, models.size + 2] = 1.0 // Intercept
        }

        // Closed-form solution: w = (X^T * X + lambda * I)^-1 * (X^T * y)
        val Xt = X.transpose()
        val XtX = Xt * X
        val regularized = XtX.addIdentityScaled(lambda)
        val Xty = Xt.timesVector(y)

        val w = regularized.solve(Xty)

        val weightsMap = mutableMapOf<NwpModel, Double>()
        for (m in models.indices) {
            weightsMap[models[m]] = w[m]
        }

        // Calculate R2
        var ssTot = 0.0
        var ssRes = 0.0
        val yMean = y.average()
        for (i in 0 until n) {
            val rec = validRecords[i]
            val pred = predictRow(rec, weightsMap, w[numFeatures - 1], w[models.size], w[models.size + 1])
            ssRes += (rec.observed!! - pred) * (rec.observed - pred)
            ssTot += (rec.observed - yMean) * (rec.observed - yMean)
        }
        val r2 = if (ssTot > 1e-9) 1.0 - (ssRes / ssTot) else 0.0

        return RidgeModelResult(
            weights = weightsMap,
            intercept = w[numFeatures - 1],
            diurnalSinWeight = w[models.size],
            diurnalCosWeight = w[models.size + 1],
            lambda = lambda,
            r2Score = r2
        )
    }

    fun predict(record: TidyForecastRecord, model: RidgeModelResult): Double {
        return predictRow(
            record = record,
            weights = model.weights,
            intercept = model.intercept,
            sinWeight = model.diurnalSinWeight,
            cosWeight = model.diurnalCosWeight
        )
    }

    private fun predictRow(
        record: TidyForecastRecord,
        weights: Map<NwpModel, Double>,
        intercept: Double,
        sinWeight: Double,
        cosWeight: Double
    ): Double {
        var sum = intercept
        for ((m, w) in weights) {
            val f = record.modelForecasts[m] ?: record.ensembleMean
            sum += w * f
        }
        val hourAngle = 2.0 * PI * record.hourOfDay / 24.0
        sum += sinWeight * sin(hourAngle)
        sum += cosWeight * cos(hourAngle)
        return sum
    }

    private fun fallbackResult(): RidgeModelResult {
        val count = NwpModel.entries.size
        return RidgeModelResult(
            weights = NwpModel.entries.associateWith { 1.0 / count },
            intercept = 0.0,
            diurnalSinWeight = 0.0,
            diurnalCosWeight = 0.0,
            lambda = lambda,
            r2Score = 0.0
        )
    }
}
