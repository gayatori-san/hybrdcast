package com.example.ml

import com.example.data.model.NwpModel
import com.example.data.model.TidyForecastRecord
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ResidualTreeBlender(
    private val numTrees: Int = 12,
    private val maxDepth: Int = 3,
    private val learningRate: Double = 0.1,
    private val minSamplesLeaf: Int = 4
) {

    sealed class Node {
        data class Leaf(val value: Double) : Node()
        data class Split(
            val featureIndex: Int,
            val threshold: Double,
            val left: Node,
            val right: Node
        ) : Node()
    }

    data class TrainedModel(
        val trees: List<Node>,
        val learningRate: Double,
        val baseResidual: Double
    )

    fun train(records: List<TidyForecastRecord>): TrainedModel {
        val valid = records.filter { it.observed != null }
        if (valid.isEmpty()) {
            return TrainedModel(emptyList(), learningRate, 0.0)
        }

        val X = valid.map { extractFeatures(it) }
        val y = valid.map { it.observed!! - it.ensembleMean } // residual of ensemble mean

        val baseResidual = y.average()
        val currentPredictions = DoubleArray(valid.size) { baseResidual }
        val trees = mutableListOf<Node>()

        for (t in 0 until numTrees) {
            // Compute pseudo-residuals
            val residuals = DoubleArray(valid.size) { i -> y[i] - currentPredictions[i] }
            val tree = buildTree(X, residuals, (0 until valid.size).toList(), depth = 0)
            trees.add(tree)

            // Update predictions
            for (i in 0 until valid.size) {
                val step = predictTree(tree, X[i])
                currentPredictions[i] += learningRate * step
            }
        }

        return TrainedModel(trees, learningRate, baseResidual)
    }

    fun predict(record: TidyForecastRecord, model: TrainedModel): Double {
        var residualPred = model.baseResidual
        val features = extractFeatures(record)
        for (tree in model.trees) {
            residualPred += model.learningRate * predictTree(tree, features)
        }
        return record.ensembleMean + residualPred
    }

    private fun extractFeatures(record: TidyForecastRecord): DoubleArray {
        val models = NwpModel.entries
        val arr = DoubleArray(models.size + 4)
        for (i in models.indices) {
            arr[i] = record.modelForecasts[models[i]] ?: record.ensembleMean
        }
        arr[models.size] = record.ensembleSpread
        val hourAngle = 2.0 * PI * record.hourOfDay / 24.0
        arr[models.size + 1] = sin(hourAngle)
        arr[models.size + 2] = cos(hourAngle)
        arr[models.size + 3] = record.leadTimeDays.toDouble()
        return arr
    }

    private fun buildTree(
        X: List<DoubleArray>,
        y: DoubleArray,
        indices: List<Int>,
        depth: Int
    ): Node {
        if (depth >= maxDepth || indices.size <= minSamplesLeaf) {
            val leafVal = if (indices.isNotEmpty()) indices.map { y[it] }.average() else 0.0
            return Node.Leaf(leafVal)
        }

        var bestVar = Double.MAX_VALUE
        var bestFeature = -1
        var bestThreshold = 0.0
        var bestLeft = listOf<Int>()
        var bestRight = listOf<Int>()

        val numFeatures = X[0].size

        // Evaluate candidate splits across features
        for (f in 0 until numFeatures) {
            val values = indices.map { X[it][f] }.distinct().sorted()
            if (values.size <= 1) continue

            // Sample candidate thresholds
            val step = maxOf(1, values.size / 6)
            for (vIdx in 0 until values.size - 1 step step) {
                val thresh = (values[vIdx] + values[vIdx + 1]) / 2.0
                val left = indices.filter { X[it][f] <= thresh }
                val right = indices.filter { X[it][f] > thresh }

                if (left.size < minSamplesLeaf || right.size < minSamplesLeaf) continue

                val leftVar = variance(left.map { y[it] }) * left.size
                val rightVar = variance(right.map { y[it] }) * right.size
                val totalVar = leftVar + rightVar

                if (totalVar < bestVar) {
                    bestVar = totalVar
                    bestFeature = f
                    bestThreshold = thresh
                    bestLeft = left
                    bestRight = right
                }
            }
        }

        if (bestFeature == -1 || bestLeft.isEmpty() || bestRight.isEmpty()) {
            val leafVal = indices.map { y[it] }.average()
            return Node.Leaf(leafVal)
        }

        val leftChild = buildTree(X, y, bestLeft, depth + 1)
        val rightChild = buildTree(X, y, bestRight, depth + 1)
        return Node.Split(bestFeature, bestThreshold, leftChild, rightChild)
    }

    private fun predictTree(node: Node, features: DoubleArray): Double {
        return when (node) {
            is Node.Leaf -> node.value
            is Node.Split -> {
                if (features[node.featureIndex] <= node.threshold) {
                    predictTree(node.left, features)
                } else {
                    predictTree(node.right, features)
                }
            }
        }
    }

    private fun variance(vals: List<Double>): Double {
        if (vals.size <= 1) return 0.0
        val mean = vals.average()
        return vals.map { (it - mean) * (it - mean) }.average()
    }
}
