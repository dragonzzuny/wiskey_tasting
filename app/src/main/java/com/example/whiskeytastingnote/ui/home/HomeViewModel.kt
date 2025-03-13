// app/src/main/java/com/example/whiskeytastingnote/ui/home/HomeViewModel.kt
package com.example.whiskeytastingnote.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.whiskeytastingnote.WhiskeyTastingApp
import com.example.whiskeytastingnote.data.model.TastingNote
import com.example.whiskeytastingnote.data.repository.TastingNoteRepository
import com.example.whiskeytastingnote.util.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the HomeScreen
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TastingNoteRepository
    val notes: Flow<List<TastingNote>>

    private val _isOnline = MutableStateFlow(NetworkUtils.isNetworkAvailable(application))
    val isOnline: StateFlow<Boolean> = _isOnline

    init {
        val tastingApp = application as WhiskeyTastingApp
        repository = tastingApp.repository
        notes = repository.allNotes

        // Monitor network state
        monitorNetworkState()
    }

    /**
     * Delete a tasting note
     */
    fun deleteNote(note: TastingNote) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    /**
     * Monitor network state changes
     */
    private fun monitorNetworkState() {
        viewModelScope.launch {
            NetworkUtils.networkStateFlow(getApplication()).collect { isConnected ->
                _isOnline.value = isConnected

                // If back online, try to sync unsynced notes
                if (isConnected) {
                    trySyncUnsyncedNotes()
                }
            }
        }
    }

    /**
     * Try to sync unsynced notes when back online
     */
    private fun trySyncUnsyncedNotes() {
        viewModelScope.launch {
            repository.unsyncedNotes.collect { unsyncedNotes ->
                if (unsyncedNotes.isNotEmpty() && isOnline.value) {
                    // For each unsynced note, try to sync with server
                    unsyncedNotes.forEach { note ->
                        // In a real app, we would call an API here to sync the note
                        // If sync successful, mark as synced in the local DB
                        repository.markAsSynced(note)
                    }
                }
            }
        }
    }
}