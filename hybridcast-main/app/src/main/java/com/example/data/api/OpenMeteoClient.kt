package com.example.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class RawForecastPayload(
    val times: List<String>,
    val values: Map<String, List<Double?>>
)

data class RawArchivePayload(
    val times: List<String>,
    val temperatures: List<Double?>,
    val precipitations: List<Double?>,
    val windSpeeds: List<Double?>
)

class OpenMeteoClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {

    suspend fun fetchMultiModelForecast(
        latitude: Double,
        longitude: Double,
        pastDays: Int = 14,
        forecastDays: Int = 7
    ): RawForecastPayload = withContext(Dispatchers.IO) {
        val url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=$latitude&longitude=$longitude" +
                "&hourly=temperature_2m,precipitation,wind_speed_10m" +
                "&models=ecmwf_ifs025,gfs_seamless,icon_seamless,gem_seamless" +
                "&past_days=$pastDays&forecast_days=$forecastDays"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "HybridCast-SIH26081/1.0 (Android Meteorological AI)")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Open-Meteo HTTP ${response.code}: ${response.message}")
            }
            val body = response.body?.string() ?: throw IOException("Empty response body")
            val json = JSONObject(body)
            val hourly = json.getJSONObject("hourly")

            val timeArray = hourly.getJSONArray("time")
            val times = ArrayList<String>(timeArray.length())
            for (i in 0 until timeArray.length()) {
                times.add(timeArray.getString(i))
            }

            val map = mutableMapOf<String, List<Double?>>()
            val keys = hourly.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                if (key == "time") continue
                val arr = hourly.getJSONArray(key)
                val vals = ArrayList<Double?>(arr.length())
                for (i in 0 until arr.length()) {
                    if (arr.isNull(i)) {
                        vals.add(null)
                    } else {
                        vals.add(arr.getDouble(i))
                    }
                }
                map[key] = vals
            }

            RawForecastPayload(times, map)
        }
    }

    suspend fun fetchArchiveObservations(
        latitude: Double,
        longitude: Double,
        startDate: String,
        endDate: String
    ): RawArchivePayload = withContext(Dispatchers.IO) {
        val url = "https://archive-api.open-meteo.com/v1/archive" +
                "?latitude=$latitude&longitude=$longitude" +
                "&start_date=$startDate&end_date=$endDate" +
                "&hourly=temperature_2m,precipitation,wind_speed_10m"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "HybridCast-SIH26081/1.0 (Android Meteorological AI)")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Open-Meteo Archive HTTP ${response.code}: ${response.message}")
            }
            val body = response.body?.string() ?: throw IOException("Empty response body")
            val json = JSONObject(body)
            val hourly = json.getJSONObject("hourly")

            val timeArray = hourly.getJSONArray("time")
            val times = ArrayList<String>(timeArray.length())
            for (i in 0 until timeArray.length()) {
                times.add(timeArray.getString(i))
            }

            val temps = parseDoubleList(hourly.optJSONArray("temperature_2m"))
            val precips = parseDoubleList(hourly.optJSONArray("precipitation"))
            val winds = parseDoubleList(hourly.optJSONArray("wind_speed_10m"))

            RawArchivePayload(times, temps, precips, winds)
        }
    }

    private fun parseDoubleList(arr: org.json.JSONArray?): List<Double?> {
        if (arr == null) return emptyList()
        val list = ArrayList<Double?>(arr.length())
        for (i in 0 until arr.length()) {
            if (arr.isNull(i)) {
                list.add(null)
            } else {
                list.add(arr.getDouble(i))
            }
        }
        return list
    }
}
