package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.util.Locale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.City
import com.example.data.model.CustomLocation
import com.example.ui.theme.LavenderBorder
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderDark
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.TextSecondaryNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerDialog(
    currentCity: City,
    customLocation: CustomLocation?,
    onSelectCity: (City) -> Unit,
    onApplyCustomLocation: (name: String, lat: Double, lon: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showCustomInput by remember { mutableStateOf(customLocation != null) }
    var customName by remember { mutableStateOf(customLocation?.name ?: "") }
    var latText by remember { mutableStateOf(customLocation?.latitude?.toString() ?: "") }
    var lonText by remember { mutableStateOf(customLocation?.longitude?.toString() ?: "") }
    var inputError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("location_picker_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LOCATION DOMAIN SELECTOR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LavenderPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Select Indian City or Custom Coordinates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .background(LavenderContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LavenderPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle between Indian Cities & Custom Coordinates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (!showCustomInput) LavenderPrimary else LavenderContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showCustomInput = false }
                        .testTag("tab_indian_cities")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Major Indian Cities (9)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!showCustomInput) Color.White else LavenderDark
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (showCustomInput) LavenderPrimary else LavenderContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showCustomInput = true }
                        .testTag("tab_custom_coords")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Custom Lat / Lon",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showCustomInput) Color.White else LavenderDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!showCustomInput) {
                // List of 9 Indian Cities
                Text(
                    text = "SUPPORTED METEOROLOGICAL STATIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryMuted,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))

                City.entries.forEach { city ->
                    val isSelected = (customLocation == null) && (city == currentCity)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) LavenderContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) LavenderPrimary else LavenderBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                onSelectCity(city)
                                onDismiss()
                            }
                            .testTag("city_item_${city.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = city.displayName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(${city.state})",
                                        fontSize = 11.sp,
                                        color = TextSecondaryMuted
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = String.format(Locale.US, "%.4f° N, %.4f° E • %s", city.latitude, city.longitude, city.climateZone),
                                    fontSize = 10.sp,
                                    color = TextSecondaryNavy,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(LavenderPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Custom Lat/Lon Form
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ENTER CUSTOM COORDINATES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("Location Name (Optional)") },
                            placeholder = { Text("e.g. Western Ghats AWS") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LavenderPrimary,
                                unfocusedBorderColor = LavenderBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = latText,
                                onValueChange = { latText = it },
                                label = { Text("Latitude (-90 to 90)") },
                                placeholder = { Text("e.g. 18.5204") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LavenderPrimary,
                                    unfocusedBorderColor = LavenderBorder
                                )
                            )

                            OutlinedTextField(
                                value = lonText,
                                onValueChange = { lonText = it },
                                label = { Text("Longitude (-180 to 180)") },
                                placeholder = { Text("e.g. 73.8567") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LavenderPrimary,
                                    unfocusedBorderColor = LavenderBorder
                                )
                            )
                        }

                        if (inputError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = inputError!!,
                                fontSize = 11.sp,
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val lat = latText.toDoubleOrNull()
                                val lon = lonText.toDoubleOrNull()
                                if (lat == null || lat < -90.0 || lat > 90.0) {
                                    inputError = "Please enter a valid Latitude between -90 and 90."
                                    return@Button
                                }
                                if (lon == null || lon < -180.0 || lon > 180.0) {
                                    inputError = "Please enter a valid Longitude between -180 and 180."
                                    return@Button
                                }
                                val name = if (customName.isNotBlank()) customName.trim() else String.format(Locale.US, "Custom (%.2f, %.2f)", lat, lon)
                                onApplyCustomLocation(name, lat, lon)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("apply_custom_coords_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Apply Coordinates & Fetch Data",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
