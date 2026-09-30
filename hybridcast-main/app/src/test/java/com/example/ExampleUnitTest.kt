package com.example

import com.example.data.model.NwpModel
import com.example.data.model.TidyForecastRecord
import com.example.data.model.WeatherVariable
import com.example.ml.Matrix
import com.example.ml.MetricsCalculator
import com.example.ml.RidgeBlender
import com.example.ml.UncertaintyEstimator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ExampleUnitTest {

    @Test
    fun testMatrixSolve_2x2System() {
        // [2 1] [x0] = [5]
        // [1 3] [x1] = [5]
        // Solution: x0 = 2.0, x1 = 1.0
        val A = Matrix(2, 2)
        A[0, 0] = 2.0; A[0, 1] = 1.0
        A[1, 0] = 1.0; A[1, 1] = 3.0

        val b = doubleArrayOf(5.0, 5.0)
        val x = A.solve(b)

        assertEquals(2.0, x[0], 1e-4)
        assertEquals(1.0, x[1], 1e-4)
    }

    @Test
    fun testMatrixMultiplyAndTranspose() {
        val A = Matrix(2, 3)
        // [1 2 3]
        // [4 5 6]
        A[0, 0] = 1.0; A[0, 1] = 2.0; A[0, 2] = 3.0
        A[1, 0] = 4.0; A[1, 1] = 5.0; A[1, 2] = 6.0

        val At = A.transpose()
        assertEquals(3, At.rows)
        assertEquals(2, At.cols)
        assertEquals(2.0, At[1, 0], 1e-6)

        val AtA = At * A
        assertEquals(3, AtA.rows)
        assertEquals(3, AtA.cols)
        // (At*A)[0,0] = 1*1 + 4*4 = 17
        assertEquals(17.0, AtA[0, 0], 1e-6)
    }

    @Test
    fun testMetricsCalculator_KnownCalculations() {
        val calc = MetricsCalculator()
        val preds = listOf(20.0, 22.0, 24.0)
        val obs = listOf(20.0, 23.0, 23.0)

        val metrics = calc.computeVerificationMetrics(
            predictions = preds,
            observations = obs,
            bestSingleModelRmse = 1.5,
            ensembleMeanRmse = 1.2,
            variable = WeatherVariable.TEMPERATURE
        )

        // Errors: (20-20)=0, (22-23)=-1, (24-23)=+1
        // BIAS = (0 - 1 + 1)/3 = 0.0
        assertEquals(0.0, metrics.bias, 1e-4)
        // MAE = (0 + 1 + 1)/3 = 0.6667
        assertEquals(2.0 / 3.0, metrics.mae, 1e-4)
        // RMSE = sqrt((0 + 1 + 1)/3) = sqrt(2/3) ~= 0.8165
        val expectedRmse = kotlin.math.sqrt(2.0 / 3.0)
        assertEquals(expectedRmse, metrics.rmse, 1e-4)
        // Pearson r > 0.8
        assertTrue("Pearson r should be strongly positive", metrics.pearsonR > 0.8)
        // Skill vs best single model: (1.5 - 0.8165)/1.5 * 100
        assertTrue("Skill should be positive", metrics.skillImprovementVsBest > 0.0)
    }

    @Test
    fun testPrecipitationContingencyTable() {
        val calc = MetricsCalculator()
        // Threshold: 0.1 mm
        val preds = listOf(0.0, 2.5, 0.0, 4.0)
        val obs = listOf(0.0, 1.0, 3.0, 5.0)

        val table = calc.computeContingency(preds, obs, threshold = 0.1)

        // Event 0: pred=0, obs=0 -> Correct Negative
        // Event 1: pred=2.5, obs=1.0 -> Hit
        // Event 2: pred=0, obs=3.0 -> Miss
        // Event 3: pred=4.0, obs=5.0 -> Hit
        assertEquals(2, table.hits)
        assertEquals(0, table.falseAlarms)
        assertEquals(1, table.misses)
        assertEquals(1, table.correctNegatives)

        // POD (Hit Rate) = Hits / (Hits + Misses) = 2 / 3
        assertEquals(2.0 / 3.0, table.pod, 1e-4)
        // FAR = False Alarms / (Hits + False Alarms) = 0
        assertEquals(0.0, table.far, 1e-4)
        // CSI (Threat Score) = Hits / (Hits + FA + Misses) = 2 / 3
        assertEquals(2.0 / 3.0, table.csi, 1e-4)
    }

    @Test
    fun testRidgeBlender_ClosedFormConvergence() {
        val blender = RidgeBlender(lambda = 0.1)
        val dummyRecords = (0 until 30).map { i ->
            val actual = 25.0 + 5.0 * kotlin.math.sin(i * 0.2)
            val ecmwf = actual + 0.2
            val gfs = actual - 0.4
            val icon = actual + 0.1
            val gem = actual + 0.3
            val mean = (ecmwf + gfs + icon + gem) / 4.0
            TidyForecastRecord(
                timestamp = "2026-09-01T12:00",
                epochMillis = 1000L * i,
                leadTimeDays = (i % 7) + 1,
                modelForecasts = mapOf(
                    NwpModel.ECMWF to ecmwf,
                    NwpModel.GFS to gfs,
                    NwpModel.ICON to icon,
                    NwpModel.GEM to gem
                ),
                ensembleMean = mean,
                ensembleSpread = 0.3,
                hourOfDay = i % 24,
                month = 9,
                isMonsoonJJAS = true,
                observed = actual
            )
        }

        val trained = blender.train(dummyRecords)
        assertTrue("Weights should be populated", trained.weights.isNotEmpty())
        assertTrue("R2 score should be high on synthetic clean data", trained.r2Score > 0.8)

        val testRec = dummyRecords[0]
        val pred = blender.predict(testRec, trained)
        assertTrue("Prediction should be close to observed", abs(pred - testRec.observed!!) < 1.0)
    }

    @Test
    fun testUncertaintyEstimatorCoverage() {
        val estimator = UncertaintyEstimator()
        val actuals = listOf(10.0, 12.0, 14.0, 16.0, 18.0)
        val lowers = listOf(9.0, 11.0, 13.0, 15.0, 17.0)
        val uppers = listOf(11.0, 13.0, 15.0, 17.0, 19.0)

        val coverage = estimator.calculateCoverage(actuals, lowers, uppers)
        assertEquals(100.0, coverage, 1e-4)
    }
}
