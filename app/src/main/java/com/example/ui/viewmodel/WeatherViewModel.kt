package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BlenderType
import com.example.data.model.City
import com.example.data.model.CustomLocation
import com.example.data.model.TidyForecastRecord
import com.example.data.model.WeatherVariable
import com.example.data.repository.WeatherRepository
import com.example.ml.BlendingEngine
import com.example.ml.EngineEvaluationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

enum class ScreenTab(val title: String) {
    OVERVIEW("Overview"),
    FORECAST("Forecast"),
    MODEL_COMPARISON("Model Comparison"),
    AI_BLENDING("AI Blending"),
    VALIDATION("Validation"),
    RESEARCH("Research")
}

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository(),
    private val blendingEngine: BlendingEngine = BlendingEngine()
) : ViewModel() {

    private val _selectedCity = MutableStateFlow(City.PUNE)
    val selectedCity: StateFlow<City> = _selectedCity.asStateFlow()

    private val _customLocation = MutableStateFlow<CustomLocation?>(null)
    val customLocation: StateFlow<CustomLocation?> = _customLocation.asStateFlow()

    private val _selectedVariable = MutableStateFlow(WeatherVariable.TEMPERATURE)
    val selectedVariable: StateFlow<WeatherVariable> = _selectedVariable.asStateFlow()

    private val _timeHorizonHours = MutableStateFlow(72) // 24, 48, 72, 168 (7 days)
    val timeHorizonHours: StateFlow<Int> = _timeHorizonHours.asStateFlow()

    private val _selectedBlenderOverride = MutableStateFlow<BlenderType?>(null)
    val selectedBlenderOverride: StateFlow<BlenderType?> = _selectedBlenderOverride.asStateFlow()

    private val _selectedLeadFilter = MutableStateFlow<Int?>(null)
    val selectedLeadFilter: StateFlow<Int?> = _selectedLeadFilter.asStateFlow()

    private val _activeTab = MutableStateFlow(ScreenTab.OVERVIEW)
    val activeTab: StateFlow<ScreenTab> = _activeTab.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _evaluationResult = MutableStateFlow<EngineEvaluationResult?>(null)
    val evaluationResult: StateFlow<EngineEvaluationResult?> = _evaluationResult.asStateFlow()

    private var currentHistorical: List<TidyForecastRecord> = emptyList()
    private var currentLive: List<TidyForecastRecord> = emptyList()

    init {
        loadData()
    }

    fun selectTab(tab: ScreenTab) {
        _activeTab.value = tab
    }

    fun selectCity(city: City) {
        _customLocation.value = null
        if (_selectedCity.value != city) {
            _selectedCity.value = city
            loadData()
        }
    }

    fun setCustomLocation(name: String, lat: Double, lon: Double) {
        _customLocation.value = CustomLocation(name, lat, lon)
        loadData()
    }

    fun clearCustomLocation() {
        if (_customLocation.value != null) {
            _customLocation.value = null
            loadData()
        }
    }

    fun selectVariable(variable: WeatherVariable) {
        if (_selectedVariable.value != variable) {
            _selectedVariable.value = variable
            loadData()
        }
    }

    fun selectTimeHorizon(hours: Int) {
        _timeHorizonHours.value = hours
    }

    fun selectBlenderAlgorithm(blender: BlenderType?) {
        _selectedBlenderOverride.value = blender
    }

    fun selectLeadFilter(leadDay: Int?) {
        _selectedLeadFilter.value = leadDay
    }

    fun refreshData() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val custom = _customLocation.value
                val (historical, live) = if (custom != null) {
                    repository.loadCustomCoordinates(
                        lat = custom.latitude,
                        lon = custom.longitude,
                        name = custom.name,
                        variable = _selectedVariable.value
                    )
                } else {
                    repository.loadForecastAndObservations(
                        city = _selectedCity.value,
                        variable = _selectedVariable.value
                    )
                }

                currentHistorical = historical
                currentLive = live

                val result = blendingEngine.execute(
                    historicalRecords = historical,
                    liveRecords = live,
                    variable = _selectedVariable.value
                )
                _evaluationResult.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Unable to load meteorological data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun exportVerificationCsv(context: Context) {
        val result = _evaluationResult.value ?: return
        val city = _selectedCity.value
        val variable = _selectedVariable.value
        val locationLabel = _customLocation.value?.name ?: "${city.displayName}, ${city.state}"

        val sb = StringBuilder()
        sb.append("HybridCast AI-NWP Verification SOP 2021 Report\n")
        sb.append("Location: $locationLabel\n")
        sb.append("Variable: ${variable.displayName} (${variable.unit})\n")
        sb.append("Selected Blender: ${result.selectedBlender.title}\n\n")

        sb.append("Model,BIAS,MAE,RMSE,Skill_Gain_vs_Best\n")
        sb.append("AI Blended Forecast,${fmt(result.bestBlenderMetrics.bias)},${fmt(result.bestBlenderMetrics.mae)},${fmt(result.bestBlenderMetrics.rmse)},+${fmt(result.bestBlenderMetrics.skillImprovementVsBest)}%\n")

        for ((name, m) in result.baselineMetrics) {
            sb.append("$name,${fmt(m.bias)},${fmt(m.mae)},${fmt(m.rmse)},—\n")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Verification CSV Report")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    private fun fmt(d: Double): String = String.format(Locale.US, "%.2f", d)
}
