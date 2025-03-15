// app/src/main/java/com/example/whiskeytastingnote/data/repository/TastingNoteRepository.kt
package com.example.whiskeytastingnote.data.repository

import com.example.whiskeytastingnote.data.database.TastingNoteDao
import com.example.whiskeytastingnote.data.model.TastingNote
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository for accessing TastingNote data
 */
class TastingNoteRepository(private val tastingNoteDao: TastingNoteDao) {

    // Get all tasting notes
    val allNotes: Flow<List<TastingNote>> = tastingNoteDao.getAllNotes()

    // Get unsynced notes
    val unsyncedNotes: Flow<List<TastingNote>> = tastingNoteDao.getUnsyncedNotes()

    // Insert a new tasting note
    suspend fun insert(tastingNote: TastingNote): Long {
        val noteWithTimestamp = tastingNote.copy(lastModified = Date())
        return tastingNoteDao.insert(noteWithTimestamp)
    }

    // Update an existing tasting note
    suspend fun update(tastingNote: TastingNote) {
        val noteWithTimestamp = tastingNote.copy(lastModified = Date())
        tastingNoteDao.update(noteWithTimestamp)
    }

    // Delete a tasting note
    suspend fun delete(tastingNote: TastingNote) {
        tastingNoteDao.delete(tastingNote)
    }

    // Get a tasting note by ID
    fun getNoteById(id: Long): Flow<TastingNote?> {
        return tastingNoteDao.getNoteById(id)
    }

    // Mark a note as synced
    suspend fun markAsSynced(tastingNote: TastingNote) {
        val syncedNote = tastingNote.copy(isSynced = true, lastModified = Date())
        tastingNoteDao.update(syncedNote)
    }


    fun searchNotes(query: String): Flow<List<TastingNote>> {
        return tastingNoteDao.searchNotes(query)
    }
}