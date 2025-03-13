// app/src/main/java/com/example/whiskeytastingnote/ui/detail/NoteDetailScreen.kt
package com.example.whiskeytastingnote.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.whiskeytastingnote.data.model.Aroma
import com.example.whiskeytastingnote.ui.note.components.RadarChart
import com.example.whiskeytastingnote.util.DateUtils
import com.example.whiskeytastingnote.util.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * Screen for displaying a tasting note in detail
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Long,
    viewModel: NoteDetailViewModel = viewModel(),
    onNavigateUp: () -> Unit = {},
    onEditClick: (Long) -> Unit = {},
    onDeleteConfirm: () -> Unit = {}
) {
    val context = LocalContext.current
    val note by viewModel.note.collectAsState(initial = null)

    // Load the note when the screen is first displayed
    LaunchedEffect(key1 = noteId) {
        viewModel.loadNote(noteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = note?.name ?: "테이스팅 노트",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Share note as image
                        note?.let { tastingNote ->
                            CoroutineScope(Dispatchers.Main).launch {
                                // In a real app, this would generate an image of the note
                                // and share it using the intent system

                                // For now, just share text
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "위스키 테이스팅 노트: ${tastingNote.name}")
                                    putExtra(Intent.EXTRA_TEXT,
                                        """
                                        위스키 테이스팅 노트
                                        
                                        이름: ${tastingNote.name}
                                        날짜: ${DateUtils.formatDate(tastingNote.date)}
                                        ABV: ${tastingNote.abv}
                                        점수: ${tastingNote.score}
                                        
                                        향 코멘트: ${tastingNote.noseComment}
                                        맛 코멘트: ${tastingNote.palateComment}
                                        피니시 코멘트: ${tastingNote.finishComment}
                                        총평: ${tastingNote.overallComment}
                                        """.trimIndent()
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "테이스팅 노트 공유"))
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "공유하기"
                        )
                    }

                    IconButton(onClick = {
                        // Show delete confirmation
                        viewModel.showDeleteConfirmation()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "삭제",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEditClick(noteId) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "편집"
                )
            }
        }
    ) { paddingValues ->
        note?.let { tastingNote ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Basic information card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = tastingNote.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = DateUtils.formatDate(tastingNote.date),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (tastingNote.score.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = tastingNote.score,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = "/100",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }

                        if (tastingNote.abv.isNotEmpty()) {
                            Text(
                                text = "ABV ${tastingNote.abv}%",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Color indicator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "색상:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(end = 16.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(getWhiskeyColor(tastingNote.color))
                                    .align(Alignment.CenterVertically)
                            )

                            Text(
                                text = tastingNote.color.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image if available
                tastingNote.imagePath?.let { path ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(File(path))
                                .crossfade(true)
                                .build(),
                            contentDescription = "위스키 사진",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Nose section
                SectionCard(
                    title = "향 / Nose",
                    values = mapOf(
                        "향신료" to tastingNote.aromaSpice,
                        "피트" to tastingNote.aromaPeat,
                        "곡물" to tastingNote.aromaGrain,
                        "꽃" to tastingNote.aromaFloral,
                        "과일" to tastingNote.aromaFruit,
                        "나무" to tastingNote.aromaWood,
                        "기타" to tastingNote.aromaOther
                    ),
                    selectedAromas = tastingNote.selectedAromas,
                    comment = tastingNote.noseComment
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Palate section
                SectionCard(
                    title = "맛 / Palate",
                    values = mapOf(
                        "단맛" to tastingNote.palateSweetness,
                        "신맛" to tastingNote.palateSourness,
                        "쓴맛" to tastingNote.palateBitterness,
                        "지방맛" to tastingNote.palateFatty,
                        "짠맛" to tastingNote.palateSalty,
                        "감칠맛" to tastingNote.palateUmami
                    ),
                    comment = tastingNote.palateComment
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Retronasal section
                SectionCard(
                    title = "비후방 후각 / Retronasal",
                    values = mapOf(
                        "향신료" to tastingNote.retronasalSpice,
                        "피트" to tastingNote.retronasalPeat,
                        "곡물" to tastingNote.retronasalGrain,
                        "꽃" to tastingNote.retronasalFloral,
                        "과일" to tastingNote.retronasalFruit,
                        "나무" to tastingNote.retronasalWood,
                        "기타" to tastingNote.retronasalOther
                    ),
                    selectedAromas = tastingNote.selectedRetronasalAromas,
                    comment = tastingNote.finishComment
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Character section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "개성 / Characters",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        tastingNote.characters.forEach { character ->
                            CharacterBar(
                                leftText = character.left,
                                rightText = character.right,
                                value = character.value
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Finish and overall comments
                if (tastingNote.finishComment.isNotEmpty() || tastingNote.overallComment.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            if (tastingNote.overallComment.isNotEmpty()) {
                                Text(
                                    text = "총평 / Overall",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = tastingNote.overallComment,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Add space at the bottom for the FAB
                Spacer(modifier = Modifier.height(80.dp))
            }
        } ?: run {
            // Loading or error state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "노트를 불러오는 중...",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }

    // Show delete confirmation dialog if needed
    if (viewModel.showDeleteDialog.value) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deleteNote()
                onDeleteConfirm()
            },
            onDismiss = {
                viewModel.dismissDeleteConfirmation()
            }
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    values: Map<String, Int>,
    selectedAromas: List<Aroma> = emptyList(),
    comment: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Radar chart
            RadarChart(
                values = values,
                selectedAromas = selectedAromas,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            // Selected aromas list
            if (selectedAromas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "감지된 향:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedAromas.forEach { aroma ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = aroma.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Comment
            if (comment.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "코멘트:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = comment,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun CharacterBar(
    leftText: String,
    rightText: String,
    value: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = leftText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (value < 5) FontWeight.Bold else FontWeight.Normal,
                color = if (value < 5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = when {
                    value < 4 -> "${(5 - value) * 20}% $leftText"
                    value > 6 -> "${(value - 5) * 20}% $rightText"
                    else -> "균형 잡힘"
                },
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = rightText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (value > 5) FontWeight.Bold else FontWeight.Normal,
                color = if (value > 5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value / 10f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("노트 삭제") },
        text = { Text("이 테이스팅 노트를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.") },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirm) {
                Text("삭제")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun getWhiskeyColor(colorValue: Float): Color {
    return when {
        colorValue < 0.4f -> Color(0xFFFEF9C3) // Very light
        colorValue < 0.8f -> Color(0xFFFDE68A) // Pale gold
        colorValue < 1.2f -> Color(0xFFFBBF24) // Gold
        colorValue < 1.6f -> Color(0xFFF59E0B) // Amber
        else -> Color(0xFFB45309) // Deep amber/mahogany
    }
}