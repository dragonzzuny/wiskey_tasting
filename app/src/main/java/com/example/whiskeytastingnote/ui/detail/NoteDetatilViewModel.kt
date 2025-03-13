// app/src/main/java/com/example/whiskeytastingnote/ui/detail/NoteDetailViewModel.kt
package com.example.whiskeytastingnote.ui.detail

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.whiskeytastingnote.WhiskeyTastingApp
import com.example.whiskeytastingnote.data.model.TastingNote
import com.example.whiskeytastingnote.data.repository.TastingNoteRepository
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
}