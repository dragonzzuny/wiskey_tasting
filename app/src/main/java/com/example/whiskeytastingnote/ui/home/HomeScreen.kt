// app/src/main/java/com/example/whiskeytastingnote/ui/home/HomeScreen.kt
package com.example.whiskeytastingnote.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whiskeytastingnote.data.model.TastingNote
import com.example.whiskeytastingnote.ui.components.NoteCard
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Home screen showing the list of tasting notes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNoteClick: (Long) -> Unit = {},
    onNewNoteClick: () -> Unit = {}
) {
    val notes by viewModel.notes.collectAsState(initial = emptyList())
    val isOnline by viewModel.isOnline.collectAsState(initial = true)
    val searchResults by viewModel.searchResults.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "위스키 테이스팅 노트",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        // Online status indicator
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .background(
                                    color = if (isOnline) Color(0xFF4CAF50) else Color.Gray,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            color = if (isOnline) Color.White else Color.LightGray,
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = if (isOnline) " 온라인" else " 오프라인",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewNoteClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "새 노트 추가"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 검색 필드 추가
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.search(it) },
                placeholder = { Text("위스키 이름 또는 코멘트로 검색") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 검색 결과에 따라 표시할 노트 목록 결정
            val notesToDisplay = if (viewModel.searchQuery.isBlank()) {
                notes
            } else {
                searchResults
            }

            if (notesToDisplay.isEmpty()) {
                if (viewModel.searchQuery.isBlank() && notes.isEmpty()) {
                    // 노트가 하나도 없는 경우
                    EmptyState()
                } else {
                    // 검색 결과가 없는 경우
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "'${viewModel.searchQuery}'에 대한 검색 결과가 없습니다",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                // 노트 목록 표시
                NotesList(
                    notes = notesToDisplay,
                    onNoteClick = onNoteClick,
                    onDeleteClick = { viewModel.deleteNote(it) }
                )
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "아직 테이스팅 노트가 없습니다",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "오른쪽 하단의 + 버튼을 눌러 첫 번째 노트를 만들어보세요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun NotesList(
    notes: List<TastingNote>,
    onNoteClick: (Long) -> Unit,
    onDeleteClick: (TastingNote) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(notes) { note ->
            NoteCard(
                note = note,
                onClick = { onNoteClick(note.id) },
                onDeleteClick = { onDeleteClick(note) }
            )
        }
    }
}