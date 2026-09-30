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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SkyBluePrimary
import java.util.Locale

// Neomorphic Palette
val NeoBackground = Color(0xFFF1F5F9) // Calm soft blue-grey
val NeoSurface = Color(0xFFF8FAFC)    // Soft elevated surface
val NeoShadowDark = Color(0xFFCBD5E1) // Bottom-right soft shadow
val NeoShadowLight = Color(0xFFFFFFFF)// Top-left bright highlight
val NeoBorder = Color(0xFFE2E8F0)     // Subtle edge definition

@Composable
fun NeomorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = NeoSurface,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = NeoShadowDark.copy(alpha = 0.5f),
                spotColor = NeoShadowDark.copy(alpha = 0.5f)
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeoBorder)
    ) {
        content()
    }
}

@Composable
fun NeomorphicPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accentColor: Color = SkyBluePrimary
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) accentColor else NeoSurface,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, NeoBorder),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .shadow(
                elevation = if (isSelected) 0.dp else 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = NeoShadowDark.copy(alpha = 0.4f),
                spotColor = NeoShadowDark.copy(alpha = 0.4f)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else accentColor,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun GlossaryTooltipIcon(
    term: String,
    definition: String,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showDialog = true },
        modifier = modifier.size(18.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Help: $term",
            tint = SkyBluePrimary.copy(alpha = 0.75f),
            modifier = Modifier.size(14.dp)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(SkyBluePrimary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SkyBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = term, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = definition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Got it", fontWeight = FontWeight.Bold, color = SkyBluePrimary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = NeoSurface
        )
    }
}

@Composable
fun PlainEnglishVerdictCard(
    cityName: String,
    leadDay: Int?,
    skillPercent: Double,
    isBlendCloser: Boolean,
    modifier: Modifier = Modifier
) {
    NeomorphicCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        backgroundColor = if (isBlendCloser) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isBlendCloser) "✨" else "📊",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Operational Verdict (Plain English):",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isBlendCloser) Color(0xFF166534) else Color(0xFF991B1B)
                )
                Text(
                    text = if (isBlendCloser) {
                        val leadText = if (leadDay != null) "at Day $leadDay" else "across Day 1–7 leads"
                        "HybridCast was closer to reality than the best single NWP model in $cityName $leadText, delivering a +${String.format(Locale.US, "%.1f", skillPercent)}% reduction in error."
                    } else {
                        "NWP models are in close agreement for $cityName. HybridCast matches the leading consensus with robust uncertainty protection."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isBlendCloser) Color(0xFF15803D) else Color(0xFFB91C1C)
                )
            }
        }
    }
}
