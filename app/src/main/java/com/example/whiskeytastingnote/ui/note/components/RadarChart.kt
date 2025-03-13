// app/src/main/java/com/example/whiskeytastingnote/ui/note/components/RadarChart.kt
package com.example.whiskeytastingnote.ui.note.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whiskeytastingnote.data.model.Aroma
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A radar chart component for visualizing multidimensional data
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun RadarChart(
    values: Map<String, Int>,
    selectedAromas: List<Aroma> = emptyList(),
    maxValue: Int = 10,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    // Colors
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface // 추가된 부분

    Box(modifier = modifier.padding(16.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = minOf(centerX, centerY) * 0.8f

            // Number of vertices (categories)
            val categories = values.keys.toList()
            val numVertices = categories.size
            val angleStep = 2 * PI / numVertices

            // Function to calculate vertex position
            fun getVertexPosition(index: Int, value: Float): Offset {
                val angle = index * angleStep - PI / 2 // Start from top (negative PI/2)
                val distance = radius * (value / maxValue)
                return Offset(
                    x = centerX + (distance * cos(angle)).toFloat(),
                    y = centerY + (distance * sin(angle)).toFloat()
                )
            }

            // Draw background circles and lines
            for (level in 1..maxValue step 2) {
                val levelRadius = radius * level / maxValue

                // Draw circle at this level
                drawCircle(
                    color = surfaceColor.copy(alpha = 0.3f),
                    radius = levelRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 1f)
                )

                // Draw level label
                drawText(
                    textMeasurer = textMeasurer,
                    text = level.toString(),
                    topLeft = Offset(centerX - 5.dp.toPx(), centerY - levelRadius - 15.dp.toPx()),
                    style = TextStyle(
                        color = onSurfaceColor,
                        fontSize = 10.sp
                    )
                )
            }

            // Draw radial lines for each category
            for (i in 0 until numVertices) {
                val angle = i * angleStep - PI / 2
                val endX = centerX + (radius * cos(angle)).toFloat()
                val endY = centerY + (radius * sin(angle)).toFloat()

                drawLine(
                    color = surfaceColor,
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 1f
                )

                // Draw category label
                val labelDistance = radius * 1.1f
                val labelX = centerX + (labelDistance * cos(angle)).toFloat()
                val labelY = centerY + (labelDistance * sin(angle)).toFloat()

                val category = categories[i]
                val textLayoutResult = textMeasurer.measure(
                    text = category,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                )

                val textOffset = Offset(
                    x = labelX - textLayoutResult.size.width / 2,
                    y = labelY - textLayoutResult.size.height / 2
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = category,
                    topLeft = textOffset,
                    style = TextStyle(
                        color = onSurface, // 수정된 부분: onSurfaceTextColor -> onSurface
                        fontSize = 12.sp
                    )
                )

                // Draw selected aromas for this category if any
                val categoryAromas = selectedAromas.filter { it.category == category }
                if (categoryAromas.isNotEmpty()) {
                    val aromaText = categoryAromas.joinToString(", ") { it.name }
                    val aromaLayoutResult = textMeasurer.measure(
                        text = aromaText,
                        style = TextStyle(
                            color = primaryColor,
                            fontSize = 10.sp
                        )
                    )

                    val aromaOffset = Offset(
                        x = labelX - aromaLayoutResult.size.width / 2,
                        y = labelY + textLayoutResult.size.height / 2 + 4.dp.toPx()
                    )

                    drawText(
                        textMeasurer = textMeasurer,
                        text = aromaText,
                        topLeft = aromaOffset,
                        style = TextStyle(
                            color = primaryColor,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Draw the data polygon
            if (values.isNotEmpty()) {
                val path = Path()

                categories.forEachIndexed { index, category ->
                    val value = values[category] ?: 0
                    val position = getVertexPosition(index, value.toFloat())

                    if (index == 0) {
                        path.moveTo(position.x, position.y)
                    } else {
                        path.lineTo(position.x, position.y)
                    }
                }

                path.close()

                // Fill polygon with semi-transparent color
                drawPath(
                    path = path,
                    color = primaryColor.copy(alpha = 0.2f)
                )

                // Draw polygon outline
                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Draw data points
                categories.forEachIndexed { index, category ->
                    val value = values[category] ?: 0
                    if (value > 0) {
                        val position = getVertexPosition(index, value.toFloat())

                        drawCircle(
                            color = primaryColor,
                            radius = 5.dp.toPx(),
                            center = position
                        )

                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = position
                        )
                    }
                }
            }
        }
    }
}