package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.City
import com.example.ui.theme.BlendHighlight
import com.example.ui.theme.GlassPillBackground
import com.example.ui.theme.GlassPillBorder
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyHeroGradient
import com.example.ui.viewmodel.ScreenTab
import com.example.ui.viewmodel.WeatherViewModel

@Composable
fun HomeScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .testTag("home_screen")
    ) {
        // Hero Top Banner with Sky Blue Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SkyHeroGradient)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HybridCast",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Many models. One better forecast.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // 3D Weather Sun Cloud Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sun_cloud_weather_3d_1790695890455),
                            contentDescription = "Weather Icon",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clean 3-Step Logo Pills (Zero text paragraphs)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HomeFlowPill(icon = Icons.Default.Public, title = "4 Models", subtitle = "NWP Physics", color = Color(0xFF60A5FA), modifier = Modifier.weight(1f))
                    HomeFlowPill(icon = Icons.Default.AutoAwesome, title = "AI Blend", subtitle = "Machine Learning", color = Color(0xFFFDE047), modifier = Modifier.weight(1f))
                    HomeFlowPill(icon = Icons.Default.Shield, title = "+16% Skill", subtitle = "Calibrated 80% CI", color = Color(0xFF4ADE80), modifier = Modifier.weight(1f))
                }
            }
        }

        // Content Body: Action Button & Cities
        Column(modifier = Modifier.padding(20.dp)) {
            // Big Start Button
            Button(
                onClick = { viewModel.selectTab(ScreenTab.FORECAST) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("home_start_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View Live Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cities List with Logos & Temperatures
            Text(
                text = "Cities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            City.entries.forEach { city ->
                val temp = when (city) {
                    City.PUNE -> "26°C"
                    City.DELHI -> "29°C"
                    City.MUMBAI -> "28°C"
                    City.CHENNAI -> "31°C"
                    City.KOLKATA -> "30°C"
                    City.GUWAHATI -> "27°C"
                    City.JAIPUR -> "32°C"
                }
                val warning = when (city) {
                    City.GUWAHATI -> "⚠️ GFS Wet Bias"
                    City.MUMBAI -> "🌧️ High Rain Risk"
                    else -> null
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.selectCity(city)
                            viewModel.selectTab(ScreenTab.FORECAST)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SkyBluePrimary.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = SkyBluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${city.displayName} (${city.state})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (warning != null) {
                                    Text(
                                        text = warning,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (city == City.GUWAHATI) Color(0xFFD97706) else Color(0xFF2563EB)
                                    )
                                } else {
                                    Text(
                                        text = city.climateZone,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (city == City.MUMBAI || city == City.GUWAHATI) Icons.Default.Cloud else Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = if (city == City.MUMBAI || city == City.GUWAHATI) SkyBluePrimary else Color(0xFFF59E0B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = temp,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BlendHighlight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeFlowPill(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GlassPillBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassPillBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
