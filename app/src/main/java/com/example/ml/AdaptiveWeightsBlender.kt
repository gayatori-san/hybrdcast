package com.example.ml

import com.example.data.model.NwpModel
import com.example.data.model.TidyForecastRecord

data class AdaptiveWeightsResult(
    val weights: Map<NwpModel, Double>,
    val modelMse: Map<NwpModel, Double>
)

class AdaptiveWeightsBlender(private val epsilon: Double = 1e-4) {

    fun train(records: List<TidyForecastRecord>): AdaptiveWeightsResult {
        val valid = records.filter { it.observed != null }
        val models = NwpModel.entries

        if (valid.isEmpty()) {
            val equal = 1.0 / models.size
            return AdaptiveWeightsResult(
                weights = models.associateWith { equal },
                modelMse = models.associateWith { 1.0 }
            )
        }

        val mseMap = mutableMapOf<NwpModel, Double>()
        val invMseMap = mutableMapOf<NwpModel, Double>()
        var sumInvMse = 0.0

        for (m in models) {
            var sumSqErr = 0.0
            for (rec in valid) {
                val f = rec.modelForecasts[m] ?: rec.ensembleMean
                val err = f - rec.observed!!
                sumSqErr += err * err
            }
            val mse = sumSqErr / valid.size
            mseMap[m] = mse
            val inv = 1.0 / (mse + epsilon)
            invMseMap[m] = inv
            sumInvMse += inv
        }

        val weights = mutableMapOf<NwpModel, Double>()
        for (m in models) {
            weights[m] = (invMseMap[m] ?: 0.0) / sumInvMse
        }

        return AdaptiveWeightsResult(weights, mseMap)
    }

    fun predict(record: TidyForecastRecord, model: AdaptiveWeightsResult): Double {
        var sum = 0.0
        for ((m, w) in model.weights) {
            val f = record.modelForecasts[m] ?: record.ensembleMean
            sum += w * f
        }
        return sum
    }
}
