// app/src/main/java/com/example/whiskeytastingnote/ui/detail/NoteDetailViewModel.kt
package com.example.whiskeytastingnote.ui.detail

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.whiskeytastingnote.WhiskeyTastingApp
import com.example.whiskeytastingnote.data.model.TastingNote
import com.example.whiskeytastingnote.data.repository.TastingNoteRepository
import com.example.whiskeytastingnote.util.ImageGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for the NoteDetailScreen
 */
class NoteDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TastingNoteRepository
    private var currentNoteId: Long? = null

    val note: Flow<TastingNote?>
        get() = repository.getNoteById(currentNoteId ?: 0)

    val showDeleteDialog = mutableStateOf(false)

    init {
        val app = application as WhiskeyTastingApp
        repository = app.repository
    }

    /**
     * Load note by ID
     */
    fun loadNote(id: Long) {
        currentNoteId = id
    }

    /**
     * Delete the current note
     */
    fun deleteNote() {
        viewModelScope.launch {
            val noteToDelete = currentNoteId?.let { repository.getNoteById(it).first() }

            noteToDelete?.let {
                // Delete associated image file if exists
                it.imagePath?.let { path ->
                    try {
                        val file = File(path)
                        if (file.exists()) {
                            file.delete()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Delete the note from the database
                repository.delete(it)
            }

            dismissDeleteConfirmation()
        }
    }

    /**
     * Show delete confirmation dialog
     */
    fun showDeleteConfirmation() {
        showDeleteDialog.value = true
    }

    /**
     * Dismiss delete confirmation dialog
     */
    fun dismissDeleteConfirmation() {
        showDeleteDialog.value = false
    }

    /**
     * Generate and share tasting note as an image
     * @param context Context for generating and sharing the image
     * @param saveOnly If true, saves the image to gallery without sharing
     */
    fun generateAndShareImage(context: Context, saveOnly: Boolean = false) {
        viewModelScope.launch {
            // Flow에서 현재 값을 가져옴
            val currentNote = currentNoteId?.let { repository.getNoteById(it).first() }

            currentNote?.let { tastingNote ->
                ImageGenerator.generateTastingNoteImage(context, tastingNote) { uri ->
                    if (saveOnly) {
                        // 이미지를 갤러리에 저장
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, "Whiskey_Note_${System.currentTimeMillis()}.png")
                            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        }

                        try {
                            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { savedUri ->
                                context.contentResolver.openOutputStream(savedUri)?.use { outputStream ->
                                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                        inputStream.copyTo(outputStream)
                                    }
                                }
                                Toast.makeText(context, "이미지가 갤러리에 저장되었습니다", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "이미지 저장에 실패했습니다", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    } else {
                        // 공유 인텐트 생성
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/png"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "테이스팅 노트 공유"))
                    }
                }
            }
        }
    }
}