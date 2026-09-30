package com.example.data.repository

import com.example.data.model.City
import com.example.data.model.TidyForecastRecord
import com.example.data.model.WeatherVariable

interface NwpDataAdapter {
    val adapterName: String
    val dataSourceDescription: String
    val isOperationalProxy: Boolean

    suspend fun loadForecastAndObservations(
        city: City,
        variable: WeatherVariable
    ): Pair<List<TidyForecastRecord>, List<TidyForecastRecord>>
    // Returns Pair(historicalTrainingRecordsWithObs, futureLiveForecastRecords)
}
