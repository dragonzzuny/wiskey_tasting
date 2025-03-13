// app/src/main/java/com/example/whiskeytastingnote/ui/components/NoteCard.kt
package com.example.whiskeytastingnote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whiskeytastingnote.data.model.TastingNote
import com.example.whiskeytastingnote.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Card component to display a tasting note in a list
 */
@Composable
fun NoteCard(
    note: TastingNote,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Color indicator
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(getWhiskeyColor(note.color))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Name and date
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = note.name.ifEmpty { "이름 없음" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = DateUtils.formatDate(note.date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Score
                if (note.score.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${note.score}/100",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Delete button
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Display some note details
            if (note.noseComment.isNotEmpty() || note.palateComment.isNotEmpty() || note.finishComment.isNotEmpty()) {
                Text(
                    text = when {
                        note.overallComment.isNotEmpty() -> note.overallComment
                        note.noseComment.isNotEmpty() -> "향: ${note.noseComment}"
                        note.palateComment.isNotEmpty() -> "맛: ${note.palateComment}"
                        note.finishComment.isNotEmpty() -> "피니시: ${note.finishComment}"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Sync status indicator
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (note.isSynced) Color(0xFF4CAF50) else Color.Gray,
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = if (note.isSynced) " 동기화됨" else " 동기화 대기 중",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (note.isSynced) Color(0xFF4CAF50) else Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Get a color based on whiskey SRM value
 */
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