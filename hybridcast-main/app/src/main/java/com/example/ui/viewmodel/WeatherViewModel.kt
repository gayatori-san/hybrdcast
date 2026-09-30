package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.City
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
    HOME("Home"),
    FORECAST("Forecast"),
    CHARTS("Charts"),
    TRUST("Trust"),
    SCORES("Scores")
}

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository(),
    private val blendingEngine: BlendingEngine = BlendingEngine()
) : ViewModel() {

    private val _selectedCity = MutableStateFlow(City.PUNE)
    val selectedCity: StateFlow<City> = _selectedCity.asStateFlow()

    private val _selectedVariable = MutableStateFlow(WeatherVariable.TEMPERATURE)
    val selectedVariable: StateFlow<WeatherVariable> = _selectedVariable.asStateFlow()

    private val _selectedLeadFilter = MutableStateFlow<Int?>(null)
    val selectedLeadFilter: StateFlow<Int?> = _selectedLeadFilter.asStateFlow()

    private val _activeTab = MutableStateFlow(ScreenTab.HOME)
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
        if (_selectedCity.value != city) {
            _selectedCity.value = city
            loadData()
        }
    }

    fun selectVariable(variable: WeatherVariable) {
        if (_selectedVariable.value != variable) {
            _selectedVariable.value = variable
            loadData()
        }
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
                val (historical, live) = repository.loadForecastAndObservations(
                    city = _selectedCity.value,
                    variable = _selectedVariable.value
                )
                currentHistorical = historical
                currentLive = live

                val result = blendingEngine.execute(
                    historicalRecords = historical,
                    liveRecords = live,
                    variable = _selectedVariable.value
                )
                _evaluationResult.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Unable to load data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun exportVerificationCsv(context: Context) {
        val result = _evaluationResult.value ?: return
        val city = _selectedCity.value
        val variable = _selectedVariable.value

        val sb = StringBuilder()
        sb.append("HybridCast Verification Report\n")
        sb.append("City: ${city.displayName}, ${city.state}\n")
        sb.append("Variable: ${variable.displayName}\n")
        sb.append("Blender: ${result.selectedBlender.title}\n\n")

        sb.append("Model,BIAS,MAE,RMSE,Skill_vs_Best\n")
        sb.append("HybridCast Blend,${fmt(result.bestBlenderMetrics.bias)},${fmt(result.bestBlenderMetrics.mae)},${fmt(result.bestBlenderMetrics.rmse)},+${fmt(result.bestBlenderMetrics.skillImprovementVsBest)}%\n")

        for ((name, m) in result.baselineMetrics) {
            sb.append("$name,${fmt(m.bias)},${fmt(m.mae)},${fmt(m.rmse)},—\n")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Report")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    private fun fmt(d: Double): String = String.format(Locale.US, "%.2f", d)
}
