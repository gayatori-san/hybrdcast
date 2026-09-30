package com.example.ml

import kotlin.math.abs
import kotlin.math.sqrt

class Matrix(val rows: Int, val cols: Int, val data: DoubleArray) {
    constructor(rows: Int, cols: Int, init: (Int, Int) -> Double = { _, _ -> 0.0 }) : this(
        rows,
        cols,
        DoubleArray(rows * cols).also { arr ->
            var idx = 0
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    arr[idx++] = init(r, c)
                }
            }
        }
    )

    operator fun get(r: Int, c: Int): Double = data[r * cols + c]
    operator fun set(r: Int, c: Int, value: Double) {
        data[r * cols + c] = value
    }

    fun transpose(): Matrix {
        val res = Matrix(cols, rows)
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                res[c, r] = this[r, c]
            }
        }
        return res
    }

    operator fun times(other: Matrix): Matrix {
        require(cols == other.rows) { "Dimension mismatch: $cols != ${other.rows}" }
        val res = Matrix(rows, other.cols)
        for (i in 0 until rows) {
            for (k in 0 until cols) {
                val a = this[i, k]
                if (a == 0.0) continue
                for (j in 0 until other.cols) {
                    res[i, j] = res[i, j] + a * other[k, j]
                }
            }
        }
        return res
    }

    fun timesVector(v: DoubleArray): DoubleArray {
        require(cols == v.size) { "Dimension mismatch: $cols != ${v.size}" }
        val out = DoubleArray(rows)
        for (r in 0 until rows) {
            var sum = 0.0
            for (c in 0 until cols) {
                sum += this[r, c] * v[c]
            }
            out[r] = sum
        }
        return out
    }

    fun addIdentityScaled(lambda: Double): Matrix {
        require(rows == cols) { "Must be square matrix" }
        val res = Matrix(rows, cols) { r, c ->
            var v = this[r, c]
            if (r == c) v += lambda
            v
        }
        return res
    }

    /**
     * Solves A * x = b using Gauss-Jordan elimination with partial pivoting.
     * Guaranteed to work for non-singular square matrices.
     */
    fun solve(b: DoubleArray): DoubleArray {
        require(rows == cols) { "Matrix must be square: $rows x $cols" }
        require(rows == b.size) { "RHS dimension mismatch: $rows != ${b.size}" }
        val n = rows

        // Augmented matrix [A | b]
        val aug = Array(n) { r ->
            DoubleArray(n + 1) { c ->
                if (c < n) this[r, c] else b[r]
            }
        }

        for (p in 0 until n) {
            // Find pivot
            var maxRow = p
            var maxVal = abs(aug[p][p])
            for (i in p + 1 until n) {
                val v = abs(aug[i][p])
                if (v > maxVal) {
                    maxVal = v
                    maxRow = i
                }
            }

            if (maxVal < 1e-12) {
                // Singular or near-singular: small regularizer fallback
                aug[p][p] += 1e-6
            }

            // Swap rows
            if (maxRow != p) {
                val temp = aug[p]
                aug[p] = aug[maxRow]
                aug[maxRow] = temp
            }

            // Normalize pivot row
            val pivot = aug[p][p]
            for (j in p until n + 1) {
                aug[p][j] /= pivot
            }

            // Eliminate column in all other rows
            for (i in 0 until n) {
                if (i != p) {
                    val factor = aug[i][p]
                    if (factor != 0.0) {
                        for (j in p until n + 1) {
                            aug[i][j] -= factor * aug[p][j]
                        }
                    }
                }
            }
        }

        val x = DoubleArray(n)
        for (i in 0 until n) {
            x[i] = aug[i][n]
        }
        return x
    }

    companion object {
        fun identity(size: Int): Matrix = Matrix(size, size) { r, c -> if (r == c) 1.0 else 0.0 }
    }
}
