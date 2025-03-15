// app/src/main/java/com/example/whiskeytastingnote/util/ImageGenerator.kt
package com.example.whiskeytastingnote.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.whiskeytastingnote.data.model.Aroma
import com.example.whiskeytastingnote.data.model.TastingNote
import com.example.whiskeytastingnote.data.model.WhiskeyCharacter
import com.example.whiskeytastingnote.ui.theme.WhiskeyTastingNoteTheme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Utility for generating shareable images of whiskey tasting notes
 */
object ImageGenerator {
    /**
     * Generate a shareable image for a tasting note
     * @param context Android context
     * @param tastingNote The tasting note data to visualize
     * @param onComplete Callback with the URI of the generated image
     */
    fun generateTastingNoteImage(context: Context, tastingNote: TastingNote, onComplete: (Uri) -> Unit) {
        try {
            // Create ComposeView to render our content
            val composeView = ComposeView(context).apply {
                setContent {
                    WhiskeyTastingNoteTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.White
                        ) {
                            TastingNoteTemplate(tastingNote)
                        }
                    }
                }
            }

            // Measure and layout the view
            val width = 1200  // Image width
            val height = 2100 // Image height
            val specWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val specHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            composeView.measure(specWidth, specHeight)
            composeView.layout(0, 0, width, height)

            // Create bitmap and draw the view onto it
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            composeView.draw(canvas)

            // Create directory for image if needed
            val imagesDir = File(context.cacheDir, "images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            // Save bitmap to file
            val file = File(imagesDir, "whiskey_note_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }

            // Create and return URI
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            onComplete(uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Compose template for the tasting note image
     */
    @Composable
    private fun TastingNoteTemplate(tastingNote: TastingNote) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White)
        ) {
            // Title
            Text(
                text = "-Whiskey Tasting Note-",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            )

            // Top info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Left column - Distillery/brand
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Distillery / Brand",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = tastingNote.name,
                        style = TextStyle(fontSize = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ABV
                    Text(
                        text = "ABV",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${tastingNote.abv}%",
                        style = TextStyle(fontSize = 16.sp)
                    )
                }

                // Middle column - Date info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tasting Date",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = DateUtils.formatDate(tastingNote.date),
                        style = TextStyle(fontSize = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Score
                    Text(
                        text = "Score",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (tastingNote.score.isNotEmpty()) "${tastingNote.score}/100" else "-",
                        style = TextStyle(fontSize = 16.sp)
                    )
                }

                // Right column - Image indicator
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    if (tastingNote.imagePath != null) {
                        Text(
                            text = "With Photo",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Color section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Color",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Color chart
                ColorChart(tastingNote.color)
            }

            // Middle section - Nose, palate, retronasal, characters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(800.dp)
            ) {
                // Left column - Nose and finish
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    // Nose radar chart
                    NoseSection(
                        aromaSpice = tastingNote.aromaSpice,
                        aromaPeat = tastingNote.aromaPeat,
                        aromaGrain = tastingNote.aromaGrain,
                        aromaFloral = tastingNote.aromaFloral,
                        aromaFruit = tastingNote.aromaFruit,
                        aromaWood = tastingNote.aromaWood,
                        aromaOther = tastingNote.aromaOther,
                        selectedAromas = tastingNote.selectedAromas,
                        noseComment = tastingNote.noseComment,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    // Finish section
                    FinishSection(
                        finishComment = tastingNote.finishComment,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                // Right column - Palate, characters, retronasal
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    // Palate radar chart
                    PalateSection(
                        sweetness = tastingNote.palateSweetness,
                        sourness = tastingNote.palateSourness,
                        bitterness = tastingNote.palateBitterness,
                        fattyness = tastingNote.palateFatty,
                        saltiness = tastingNote.palateSalty,
                        umami = tastingNote.palateUmami,
                        palateComment = tastingNote.palateComment,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )

                    // Character bars
                    CharactersSection(
                        characters = tastingNote.characters,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    // Retronasal radar chart
                    RetronasalSection(
                        retronasalSpice = tastingNote.retronasalSpice,
                        retronasalPeat = tastingNote.retronasalPeat,
                        retronasalGrain = tastingNote.retronasalGrain,
                        retronasalFloral = tastingNote.retronasalFloral,
                        retronasalFruit = tastingNote.retronasalFruit,
                        retronasalWood = tastingNote.retronasalWood,
                        retronasalOther = tastingNote.retronasalOther,
                        selectedAromas = tastingNote.selectedRetronasalAromas,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            // Overall section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black)
                    .padding(16.dp)
            ) {
                Text(
                    text = "총평 / Overall",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = tastingNote.overallComment.ifEmpty { "No overall comment provided." },
                    style = TextStyle(fontSize = 16.sp)
                )
            }

            // Footer with watermark
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Generated by Whiskey Tasting Note App",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray
                    )
                )
            }
        }
    }

    /**
     * Color chart visualization
     */
    @Composable
    private fun ColorChart(colorValue: Float) {
        val colors = listOf(
            Color(0xFFFEF9C3), // 0.0 - Very light
            Color(0xFFFDE68A), // 0.2 - Pale gold
            Color(0xFFFCD34D), // 0.4
            Color(0xFFFBBF24), // 0.6 - Gold
            Color(0xFFF59E0B), // 0.8
            Color(0xFFD97706), // 1.0 - Amber
            Color(0xFFB45309), // 1.2
            Color(0xFF92400E), // 1.4 - Deep amber
            Color(0xFF7C2D12), // 1.6
            Color(0xFF601E16), // 1.8 - Mahogany
            Color(0xFF450A05)  // 2.0
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            // Color gradient
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.Black)
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(color)
                    )
                }
            }

            // Value marker
            val position = (colorValue / 2.0f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .offset(x = (position * 100).coerceIn(0f, 100f).dp)
                    .size(width = 2.dp, height = 40.dp)
                    .background(Color.Black)
            )

            // Value label
            Text(
                text = String.format("%.1f", colorValue),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    background = Color.White.copy(alpha = 0.7f)
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = ((position * 100) - 50).dp)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }

    /**
     * Nose (aroma) section
     */
    @Composable
    private fun NoseSection(
        aromaSpice: Int,
        aromaPeat: Int,
        aromaGrain: Int,
        aromaFloral: Int,
        aromaFruit: Int,
        aromaWood: Int,
        aromaOther: Int,
        selectedAromas: List<Aroma>,
        noseComment: String = "",
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            Text(
                text = "향 / Nose",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AromaRadarChart(
                    values = mapOf(
                        "향신료" to aromaSpice,
                        "피트" to aromaPeat,
                        "곡물" to aromaGrain,
                        "꽃" to aromaFloral,
                        "과일" to aromaFruit,
                        "나무" to aromaWood,
                        "기타" to aromaOther
                    ),
                    fillColor = Color(0xFFFFE082).copy(alpha = 0.6f),
                    strokeColor = Color(0xFFFFA000),
                    backgroundLines = true
                )
            }

            // Selected aromas
            if (selectedAromas.isNotEmpty()) {
                Text(
                    text = "감지된 향:",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    selectedAromas.take(5).forEach { aroma ->
                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE3F2FD))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = aroma.name,
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    }

                    if (selectedAromas.size > 5) {
                        Text(
                            text = "+${selectedAromas.size - 5}",
                            style = TextStyle(fontSize = 12.sp),
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }

            // Comment
            if (noseComment.isNotEmpty()) {
                Text(
                    text = "코멘트:",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = noseComment,
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    /**
     * Palate section
     */
    @Composable
    private fun PalateSection(
        sweetness: Int,
        sourness: Int,
        bitterness: Int,
        fattyness: Int,
        saltiness: Int,
        umami: Int,
        palateComment: String,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            Text(
                text = "맛 / Palate",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AromaRadarChart(
                    values = mapOf(
                        "단맛" to sweetness,
                        "신맛" to sourness,
                        "쓴맛" to bitterness,
                        "지방맛" to fattyness,
                        "짠맛" to saltiness,
                        "감칠맛" to umami
                    ),
                    fillColor = Color(0xFFEF9A9A).copy(alpha = 0.6f),
                    strokeColor = Color(0xFFD32F2F),
                    backgroundLines = true
                )
            }

            // Comment
            if (palateComment.isNotEmpty()) {
                Text(
                    text = "코멘트:",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = palateComment,
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    /**
     * Characters section
     */
    @Composable
    private fun CharactersSection(
        characters: List<WhiskeyCharacter>,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            Text(
                text = "개성 / Characters",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            characters.forEach { character ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left label
                    Text(
                        text = character.left,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.width(60.dp)
                    )

                    // Bar chart
                    val gradient = when {
                        character.left == "순한" -> Brush.horizontalGradient(
                            colors = listOf(Color(0xFFC5E1A5), Color(0xFFEF5350)),
                            startX = 0f,
                            endX = 250f
                        )
                        character.left == "부드러운" -> Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFFCCBC), Color(0xFFD32F2F)),
                            startX = 0f,
                            endX = 250f
                        )
                        else -> Brush.horizontalGradient(
                            colors = listOf(Color(0xFFE0E0E0), Color(0xFF795548)),
                            startX = 0f,
                            endX = 250f
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                            .background(Color.LightGray, RoundedCornerShape(12.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(character.value / 10f)
                                .background(gradient, RoundedCornerShape(12.dp))
                        )

                        // Marker
                        Text(
                            text = "▼",
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(x = ((character.value / 10f) * 100f).dp - 15.dp),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }

                    // Right label
                    Text(
                        text = character.right,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.width(60.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }

    /**
     * Finish section
     */
    @Composable
    private fun FinishSection(
        finishComment: String,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            Text(
                text = "피니시 / Finish",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Finish comment
            if (finishComment.isNotEmpty()) {
                Text(
                    text = finishComment,
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Text(
                    text = "No finish comment provided.",
                    style = TextStyle(fontSize = 14.sp, fontStyle = FontStyle.Italic),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

    /**
     * Retronasal section
     */
    @Composable
    private fun RetronasalSection(
        retronasalSpice: Int,
        retronasalPeat: Int,
        retronasalGrain: Int,
        retronasalFloral: Int,
        retronasalFruit: Int,
        retronasalWood: Int,
        retronasalOther: Int,
        selectedAromas: List<Aroma>,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            Text(
                text = "비후방 후각 / Retronasal Aroma",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AromaRadarChart(
                    values = mapOf(
                        "향신료" to retronasalSpice,
                        "피트" to retronasalPeat,
                        "곡물" to retronasalGrain,
                        "꽃" to retronasalFloral,
                        "과일" to retronasalFruit,
                        "나무" to retronasalWood,
                        "기타" to retronasalOther
                    ),
                    fillColor = Color(0xFFFFD54F).copy(alpha = 0.6f),
                    strokeColor = Color(0xFFFF8F00),
                    backgroundLines = true
                )
            }

            // Selected aromas
            if (selectedAromas.isNotEmpty()) {
                Text(
                    text = "감지된 향:",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    selectedAromas.take(5).forEach { aroma ->
                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFFF8E1))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = aroma.name,
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    }

                    if (selectedAromas.size > 5) {
                        Text(
                            text = "+${selectedAromas.size - 5}",
                            style = TextStyle(fontSize = 12.sp),
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
    }

    /**
     * Radar chart for aroma visualization
     */
    @Composable
    private fun AromaRadarChart(
        values: Map<String, Int>,
        fillColor: Color,
        strokeColor: Color,
        backgroundLines: Boolean = false,
        maxValue: Int = 10
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = minOf(centerX, centerY) * 0.8f

            // Calculate axis count and angles
            val categories = values.keys.toList()
            val numVertices = categories.size
            val angleStep = 2 * PI / numVertices

            // Draw background circles and axes
            if (backgroundLines) {
                // Circular radar background
                for (level in 1..maxValue step 2) {
                    val ratio = level.toFloat() / maxValue
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        radius = radius * ratio,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 1f)
                    )
                }

                // Draw axes
                for (i in 0 until numVertices) {
                    val angle = i * angleStep - PI / 2
                    val endX = centerX + (radius * cos(angle)).toFloat()
                    val endY = centerY + (radius * sin(angle)).toFloat()

                    drawLine(
                        color = Color.LightGray,
                        start = Offset(centerX, centerY),
                        end = Offset(endX, endY),
                        strokeWidth = 1f
                    )

                    // Category label position
                    val labelRadius = radius * 1.1f
                    val labelX = centerX + (labelRadius * cos(angle)).toFloat()
                    val labelY = centerY + (labelRadius * sin(angle)).toFloat()

                    // Draw category label
                    val category = categories[i]
                    drawContext.canvas.nativeCanvas.drawText(
                        category,
                        labelX,
                        labelY,
                        android.graphics.Paint().apply {
                            textSize = 30f
                            color = android.graphics.Color.BLACK
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }

            // Calculate data points
            val points = mutableListOf<Offset>()
            categories.forEachIndexed { index, category ->
                val value = values[category] ?: 0
                if (value > 0) {
                    val ratio = value.toFloat() / maxValue
                    val angle = index * angleStep - PI / 2
                    val x = centerX + (radius * ratio * cos(angle)).toFloat()
                    val y = centerY + (radius * ratio * sin(angle)).toFloat()
                    points.add(Offset(x, y))
                } else {
                    // If value is 0, add a point near center
                    val angle = index * angleStep - PI / 2
                    val x = centerX + (radius * 0.05f * cos(angle)).toFloat()
                    val y = centerY + (radius * 0.05f * sin(angle)).toFloat()
                    points.add(Offset(x, y))
                }
            }

            // Draw data polygon
            if (points.size > 2) {
                // Fill polygon
                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                    close()
                }
                drawPath(
                    path = path,
                    color = fillColor,
                    style = Fill
                )

                // Draw polygon outline
                drawPath(
                    path = path,
                    color = strokeColor,
                    style = Stroke(width = 2f)
                )

                // Draw data points
                points.forEach { point ->
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = point
                    )
                    drawCircle(
                        color = strokeColor,
                        radius = 4f,
                        center = point,
                        style = Stroke(width = 2f)
                    )
                }
            }
        }
    }
}