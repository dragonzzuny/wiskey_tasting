// app/src/main/java/com/example/whiskeytastingnote/ui/note/components/ColorSelector.kt
package com.example.whiskeytastingnote.ui.note.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Component for selecting whiskey color
 */
@Composable
fun ColorSelector(
    colorValue: Float,
    onColorChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Define whiskey color stops
    val colorStops = listOf(
        ColorStop(0.1f, Color(0xFFFEF9C3), "매우 밝음"),
        ColorStop(0.3f, Color(0xFFFDE68A), "밝은 금색"),
        ColorStop(0.6f, Color(0xFFFBBF24), "금색"),
        ColorStop(0.9f, Color(0xFFF59E0B), "짙은 금색"),
        ColorStop(1.2f, Color(0xFFD97706), "밝은 앰버"),
        ColorStop(1.5f, Color(0xFFB45309), "앰버"),
        ColorStop(1.8f, Color(0xFF92400E), "진한 앰버"),
        ColorStop(2.0f, Color(0xFF7C2D12), "마호가니")
    )

    // Find closest color stop based on current value
    val closestColorStop = colorStops.minByOrNull {
        kotlin.math.abs(it.value - colorValue)
    } ?: colorStops.first()

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "색상 / Color",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Color display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Color sample
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(closestColorStop.color)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = closestColorStop.label,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "색상 값: ${colorValue.toFloat().toString().take(3)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Color gradient bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    colorStops.forEachIndexed { index, colorStop ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .background(colorStop.color)
                                .clickable { onColorChange(colorStop.value) }
                        )
                    }
                }

                // Position indicator
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .width(4.dp)
                        .height(24.dp)
                        .background(Color.White)
                        .align(Alignment.CenterStart)
                        .padding(start = (colorValue / 2f * 100).toFloat().coerceIn(0f, 100f).dp)
                )
            }

            // Value labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0.0",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "1.0",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "2.0",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fine-tuning slider
            Text(
                text = "미세 조정",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Slider(
                value = colorValue,
                onValueChange = { onColorChange(it) },
                valueRange = 0f..2f,
                steps = 19,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

/**
 * Data class for color stop information
 */
data class ColorStop(
    val value: Float,
    val color: Color,
    val label: String
)