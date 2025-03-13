// app/src/main/java/com/example/whiskeytastingnote/data/database/TastingNoteDao.kt
package com.example.whiskeytastingnote.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.whiskeytastingnote.data.model.TastingNote
import kotlinx.coroutines.flow.Flow

/**
 * Database access object for TastingNote
 */
@Dao
interface TastingNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tastingNote: TastingNote): Long

    @Update
    suspend fun update(tastingNote: TastingNote)

    @Delete
    suspend fun delete(tastingNote: TastingNote)

    @Query("SELECT * FROM tasting_notes ORDER BY lastModified DESC")
    fun getAllNotes(): Flow<List<TastingNote>>

    @Query("SELECT * FROM tasting_notes WHERE id = :id")
    fun getNoteById(id: Long): Flow<TastingNote?>

    @Query("SELECT * FROM tasting_notes WHERE isSynced = 0")
    fun getUnsyncedNotes(): Flow<List<TastingNote>>

    @Query("DELETE FROM tasting_notes")
    suspend fun deleteAll()
}