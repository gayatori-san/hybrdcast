package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class City(
    val id: String,
    val displayName: String,
    val state: String,
    val latitude: Double,
    val longitude: Double,
    val climateZone: String,
    val domainNote: String
) {
    PUNE(
        id = "pune",
        displayName = "Pune",
        state = "Maharashtra",
        latitude = 18.5204,
        longitude = 73.8567,
        climateZone = "Western Ghats Rain-Shadow (Semi-Arid)",
        domainNote = "Topographic rain-shadow zone; strong diurnal thermal cycle."
    ),
    DELHI(
        id = "delhi",
        displayName = "Delhi (NCR)",
        state = "National Capital",
        latitude = 28.6139,
        longitude = 77.2090,
        climateZone = "Indo-Gangetic Continental",
        domainNote = "Extreme seasonal thermal contrast; heatwaves and fog inversions."
    ),
    MUMBAI(
        id = "mumbai",
        displayName = "Mumbai",
        state = "Maharashtra",
        latitude = 19.0760,
        longitude = 72.8777,
        climateZone = "West Coast Coastal Maritime",
        domainNote = "Heavy coastal monsoon precipitation; high humidity dampening."
    ),
    CHENNAI(
        id = "chennai",
        displayName = "Chennai",
        state = "Tamil Nadu",
        latitude = 13.0827,
        longitude = 80.2707,
        climateZone = "Coromandel Maritime",
        domainNote = "Northeast retreat monsoon (Oct-Dec) dominant peak."
    ),
    KOLKATA(
        id = "kolkata",
        displayName = "Kolkata",
        state = "West Bengal",
        latitude = 22.5726,
        longitude = 88.3639,
        climateZone = "Eastern Tropical Wet-and-Dry",
        domainNote = "Vulnerable to Bay of Bengal tropical depressions and Nor'westers."
    ),
    GUWAHATI(
        id = "guwahati",
        displayName = "Guwahati",
        state = "Assam",
        latitude = 26.1445,
        longitude = 91.7362,
        climateZone = "North-East Subtropical Valley",
        domainNote = "IMD Report 2022 Benchmark: GFS exhibits persistent wet bias over NE India."
    ),
    JAIPUR(
        id = "jaipur",
        displayName = "Jaipur",
        state = "Rajasthan",
        latitude = 26.9124,
        longitude = 75.7873,
        climateZone = "North-West Semi-Arid / Desert Fringe",
        domainNote = "High boundary layer heating and strong nocturnal radiative cooling."
    );

    companion object {
        fun fromId(id: String): City = entries.find { it.id.equals(id, ignoreCase = true) } ?: PUNE
    }
}

enum class WeatherVariable(
    val id: String,
    val displayName: String,
    val unit: String,
    val minRealistic: Double,
    val maxRealistic: Double,
    val precipitationThreshold: Double = 0.1
) {
    TEMPERATURE("temperature_2m", "Temperature (2m)", "°C", -5.0, 52.0),
    PRECIPITATION("precipitation", "Precipitation", "mm", 0.0, 250.0, 0.1),
    WIND_SPEED("wind_speed_10m", "Wind Speed (10m)", "km/h", 0.0, 150.0);

    companion object {
        fun fromId(id: String): WeatherVariable = entries.find { it.id.equals(id, ignoreCase = true) } ?: TEMPERATURE
    }
}

enum class NwpModel(
    val code: String,
    val displayName: String,
    val agency: String,
    val resolution: String,
    val color: Color,
    val description: String
) {
    ECMWF(
        code = "ecmwf_ifs025",
        displayName = "ECMWF IFS",
        agency = "ECMWF (Europe)",
        resolution = "0.25° (~25 km)",
        color = Color(0xFF00D2FF),
        description = "Integrated Forecasting System known for superior upper-troposphere scores."
    ),
    GFS(
        code = "gfs_seamless",
        displayName = "IMD-GFS (NCEP)",
        agency = "IMD / NCEP (USA-India)",
        resolution = "T1534L64 (~12 km proxy)",
        color = Color(0xFF10B981),
        description = "Proxy for IMD operational GFS; fast physics with known NE-India wet bias."
    ),
    ICON(
        code = "icon_seamless",
        displayName = "DWD ICON",
        agency = "DWD (Germany)",
        resolution = "0.125° (~13 km)",
        color = Color(0xFFF59E0B),
        description = "Non-hydrostatic icosahedral grid model with strong mass conservation."
    ),
    GEM(
        code = "gem_seamless",
        displayName = "CMC GEM",
        agency = "ECCC (Canada)",
        resolution = "0.15° (~15 km)",
        color = Color(0xFFA855F7),
        description = "Global Environmental Multiscale model providing diverse physics representation."
    )
}

data class TidyForecastRecord(
    val timestamp: String,
    val epochMillis: Long,
    val leadTimeDays: Int,
    val modelForecasts: Map<NwpModel, Double>,
    val ensembleMean: Double,
    val ensembleSpread: Double,
    val hourOfDay: Int,
    val month: Int,
    val isMonsoonJJAS: Boolean,
    val observed: Double? = null
)

enum class BlenderType(val title: String, val shortDesc: String) {
    RIDGE("Ridge Regression (L2)", "Closed-form analytical weights with L2 shrinkage"),
    RESIDUAL_GBDT("Residual Gradient Boost", "Non-linear decision tree ensemble on ensemble mean residuals"),
    ADAPTIVE_MSE("Adaptive Inverse-MSE", "Dynamic lead-time weighting based on trailing error variance"),
    EQUAL_WEIGHT("Ensemble Simple Mean", "Unweighted arithmetic average baseline")
}

data class ContingencyTable(
    val hits: Int = 0,
    val falseAlarms: Int = 0,
    val misses: Int = 0,
    val correctNegatives: Int = 0
) {
    val totalEvents: Int get() = hits + misses
    val pod: Double get() = if (hits + misses > 0) hits.toDouble() / (hits + misses) else 0.0 // Probability of Detection (Hit Rate)
    val far: Double get() = if (hits + falseAlarms > 0) falseAlarms.toDouble() / (hits + falseAlarms) else 0.0 // False Alarm Ratio
    val csi: Double get() = if (hits + falseAlarms + misses > 0) hits.toDouble() / (hits + falseAlarms + misses) else 0.0 // Threat Score (Critical Success Index)
    val biasScore: Double get() = if (hits + misses > 0) (hits + falseAlarms).toDouble() / (hits + misses) else 1.0 // Frequency Bias
}

data class VerificationMetrics(
    val bias: Double,
    val mae: Double,
    val rmse: Double,
    val pearsonR: Double,
    val skillImprovementVsBest: Double,
    val skillImprovementVsMean: Double,
    val sampleCount: Int,
    val contingency: ContingencyTable? = null
)

data class BlendedPrediction(
    val record: TidyForecastRecord,
    val blendedValue: Double,
    val lowerBound80: Double,
    val upperBound80: Double,
    val bestBlender: BlenderType
)
