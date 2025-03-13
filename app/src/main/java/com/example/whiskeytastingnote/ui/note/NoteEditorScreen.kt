// app/src/main/java/com/example/whiskeytastingnote/ui/note/NoteEditorScreen.kt
package com.example.whiskeytastingnote.ui.note


import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction


import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whiskeytastingnote.ui.note.components.AromaSelector
import com.example.whiskeytastingnote.ui.note.components.CharacterSlider
import com.example.whiskeytastingnote.ui.note.components.ColorSelector
import com.example.whiskeytastingnote.ui.note.components.RadarChart
import com.example.whiskeytastingnote.ui.components.ImageUploader
import com.example.whiskeytastingnote.data.model.Aroma
import com.example.whiskeytastingnote.util.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Screen for editing or creating a new tasting note
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Long? = null,
    viewModel: NoteEditorViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Set up image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.imageUri = it
            // Load the image from URI
            CoroutineScope(Dispatchers.Main).launch {
                ImageUtils.getBitmapFromUri(context, it)?.let { bitmap ->
                    val compressedBitmap = ImageUtils.compressBitmap(bitmap)
                    val file = ImageUtils.createImageFile(context)
                    ImageUtils.saveBitmapToFile(compressedBitmap, file)
                    viewModel.imagePath = file.absolutePath
                }
            }
        }
    }

    // Set up camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.imageUri?.let { uri ->
                viewModel.imageUri = uri
            }
        }
    }

    // Save the note
    val saveNote = {
        val result = viewModel.saveNote()
        if (result) {
            Toast.makeText(context, "노트가 저장되었습니다", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        } else {
            Toast.makeText(context, "저장 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (noteId != null) "테이스팅 노트 편집" else "새 테이스팅 노트",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Share functionality
                        Toast.makeText(context, "공유 기능은 준비 중입니다", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "공유하기"
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
                onClick = saveNote,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "저장"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Tabs for different sections
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTabIndex) {
                    0 -> BasicInfoSection(
                        viewModel = viewModel,
                        onImagePickerClick = { imagePickerLauncher.launch("image/*") },
                        onCameraClick = {
                            // Create a temporary file and URI for camera
                            val file = ImageUtils.createImageFile(context)
                            val uri = ImageUtils.getUriForFile(context, file)
                            viewModel.imagePath = file.absolutePath
                            viewModel.imageUri = uri
                            cameraLauncher.launch(uri)
                        }
                    )
                    1 -> AromaSection(viewModel = viewModel)
                    2 -> PalateSection(viewModel = viewModel)
                    3 -> RetronasalSection(viewModel = viewModel)
                    4 -> CharacterSection(viewModel = viewModel)
                    5 -> CommentSection(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BasicInfoSection(
    viewModel: NoteEditorViewModel,
    onImagePickerClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Basic information fields
        OutlinedTextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text("위스키 이름") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.abv,
            onValueChange = { viewModel.abv = it },
            label = { Text("ABV %") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.score,
            onValueChange = { viewModel.score = it },
            label = { Text("점수 (100점 만점)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Image uploader
        ImageUploader(
            currentImageUri = viewModel.imageUri,
            onGalleryClick = onImagePickerClick,
            onCameraClick = onCameraClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Color selector
        ColorSelector(
            colorValue = viewModel.color,
            onColorChange = { viewModel.color = it }
        )
    }
}

@Composable
fun AromaSection(viewModel: NoteEditorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Radar chart visualization
        RadarChart(
            values = mapOf(
                "향신료" to viewModel.aromaSpice,
                "피트" to viewModel.aromaPeat,
                "곡물" to viewModel.aromaGrain,
                "꽃" to viewModel.aromaFloral,
                "과일" to viewModel.aromaFruit,
                "나무" to viewModel.aromaWood,
                "기타" to viewModel.aromaOther
            ),
            selectedAromas = viewModel.selectedAromas,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(vertical = 16.dp)
        )

        // Display selected aromas
        if (viewModel.selectedAromas.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "선택된 향: ${viewModel.selectedAromas.size}개",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    viewModel.selectedAromas.groupBy { it.category }.forEach { (category, aromas) ->
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            aromas.forEach { aroma ->
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = aroma.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Aroma selection controls
        AromaSelector(
            category = "향신료",
            value = viewModel.aromaSpice,
            onValueChange = { viewModel.aromaSpice = it },
            selectedAromas = viewModel.selectedAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "피트",
            value = viewModel.aromaPeat,
            onValueChange = { viewModel.aromaPeat = it },
            selectedAromas = viewModel.selectedAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "곡물",
            value = viewModel.aromaGrain,
            onValueChange = { viewModel.aromaGrain = it },
            selectedAromas = viewModel.selectedAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "꽃",
            value = viewModel.aromaFloral,
            onValueChange = { viewModel.aromaFloral = it },
            selectedAromas = viewModel.selectedAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "과일",
            value = viewModel.aromaFruit,
            onValueChange = { viewModel.aromaFruit = it },
            selectedAromas = viewModel.selectedAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "나무",
            value = viewModel.aromaWood,
            onValueChange = { viewModel.aromaWood = it },
            selectedAromas = viewModel.selectedAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "기타",
            value = viewModel.aromaOther,
            onValueChange = { viewModel.aromaOther = it },
            selectedAromas = viewModel.selectedAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleAroma(category, subCategory, aroma)
            }
        )
    }
}

@Composable
fun PalateSection(viewModel: NoteEditorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        RadarChart(
            values = mapOf(
                "단맛" to viewModel.palateSweetness,
                "신맛" to viewModel.palateSourness,
                "쓴맛" to viewModel.palateBitterness,
                "지방맛" to viewModel.palateFatty,
                "짠맛" to viewModel.palateSalty,
                "감칠맛" to viewModel.palateUmami
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(vertical = 16.dp)
        )

        // Palate sliders
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "단맛",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                PalateSlider(
                    value = viewModel.palateSweetness,
                    onValueChange = { viewModel.palateSweetness = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "신맛",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                PalateSlider(
                    value = viewModel.palateSourness,
                    onValueChange = { viewModel.palateSourness = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "쓴맛",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                PalateSlider(
                    value = viewModel.palateBitterness,
                    onValueChange = { viewModel.palateBitterness = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "지방맛",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                PalateSlider(
                    value = viewModel.palateFatty,
                    onValueChange = { viewModel.palateFatty = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "짠맛",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                PalateSlider(
                    value = viewModel.palateSalty,
                    onValueChange = { viewModel.palateSalty = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "감칠맛",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                PalateSlider(
                    value = viewModel.palateUmami,
                    onValueChange = { viewModel.palateUmami = it }
                )
            }
        }
    }
}

@Composable
fun PalateSlider(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            androidx.compose.material3.Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun RetronasalSection(viewModel: NoteEditorViewModel) {
    // Similar to AromaSection but for retronasal aromas
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        RadarChart(
            values = mapOf(
                "향신료" to viewModel.retronasalSpice,
                "피트" to viewModel.retronasalPeat,
                "곡물" to viewModel.retronasalGrain,
                "꽃" to viewModel.retronasalFloral,
                "과일" to viewModel.retronasalFruit,
                "나무" to viewModel.retronasalWood,
                "기타" to viewModel.retronasalOther
            ),
            selectedAromas = viewModel.selectedRetronasalAromas,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(vertical = 16.dp)
        )

        // Display selected retronasal aromas
        if (viewModel.selectedRetronasalAromas.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "선택된 향: ${viewModel.selectedRetronasalAromas.size}개",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    viewModel.selectedRetronasalAromas.groupBy { it.category }.forEach { (category, aromas) ->
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            aromas.forEach { aroma ->
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = aroma.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Retronasal aroma selection controls
        AromaSelector(
            category = "향신료",
            value = viewModel.retronasalSpice,
            onValueChange = { viewModel.retronasalSpice = it },
            selectedAromas = viewModel.selectedRetronasalAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleRetronasalAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "피트",
            value = viewModel.retronasalPeat,
            onValueChange = { viewModel.retronasalPeat = it },
            selectedAromas = viewModel.selectedRetronasalAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleRetronasalAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "곡물",
            value = viewModel.retronasalGrain,
            onValueChange = { viewModel.retronasalGrain = it },
            selectedAromas = viewModel.selectedRetronasalAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleRetronasalAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "꽃",
            value = viewModel.retronasalFloral,
            onValueChange = { viewModel.retronasalFloral = it },
            selectedAromas = viewModel.selectedRetronasalAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleRetronasalAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "과일",
            value = viewModel.retronasalFruit,
            onValueChange = { viewModel.retronasalFruit = it },
            selectedAromas = viewModel.selectedRetronasalAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleRetronasalAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "나무",
            value = viewModel.retronasalWood,
            onValueChange = { viewModel.retronasalWood = it },
            selectedAromas = viewModel.selectedRetronasalAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleRetronasalAroma(category, subCategory, aroma)
            }
        )

        AromaSelector(
            category = "기타",
            value = viewModel.retronasalOther,
            onValueChange = { viewModel.retronasalOther = it },
            selectedAromas = viewModel.selectedRetronasalAromas,
            onAromaToggle = { category, subCategory, aroma ->
                viewModel.toggleRetronasalAroma(category, subCategory, aroma)
            }
        )

        // 비후방 후각 코멘트 필드
        OutlinedTextField(
            value = viewModel.finishComment,  // 기존 finishComment 변수 재활용
            onValueChange = { viewModel.finishComment = it },
            label = { Text("비후방 후각 코멘트") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )


        // Add similar selectors for other retronasal categories
        // (Code similar to AromaSection)
    }
}

@Composable
fun CharacterSection(viewModel: NoteEditorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "위스키 개성",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                viewModel.characters.forEachIndexed { index, character ->
                    CharacterSlider(
                        leftText = character.left,
                        rightText = character.right,
                        value = character.value,
                        onValueChange = { newValue ->
                            viewModel.updateCharacter(index, newValue)
                        }
                    )

                    if (index < viewModel.characters.size - 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CommentSection(viewModel: NoteEditorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Nose comments
        OutlinedTextField(
            value = viewModel.noseComment,
            onValueChange = { viewModel.noseComment = it },
            label = { Text("향 코멘트") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )

        )

        Spacer(modifier = Modifier.height(16.dp))

        // Palate comments
        OutlinedTextField(
            value = viewModel.palateComment,
            onValueChange = { viewModel.palateComment = it },
            label = { Text("맛 코멘트") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Finish comments
        OutlinedTextField(
            value = viewModel.finishComment,
            onValueChange = { viewModel.finishComment = it },
            label = { Text("피니시 코멘트") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Overall comments
        OutlinedTextField(
            value = viewModel.overallComment,
            onValueChange = { viewModel.overallComment = it },
            label = { Text("총평") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
    }
}

private val tabTitles = listOf("기본정보", "향", "맛", "비후방 후각", "개성", "코멘트")