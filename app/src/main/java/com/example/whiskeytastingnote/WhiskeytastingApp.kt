// app/src/main/java/com/example/whiskeytastingnote/WhiskeyTastingApp.kt
package com.example.whiskeytastingnote

import android.app.Application
import com.example.whiskeytastingnote.data.database.AppDatabase
import com.example.whiskeytastingnote.data.repository.TastingNoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Application class for WhiskeyTastingNote
 */
class WhiskeyTastingApp : Application() {
    // Application scope to run coroutines that need to live as long as the application
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Database and repository instances
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TastingNoteRepository(database.tastingNoteDao()) }

    override fun onCreate() {
        super.onCreate()
        // Initialize anything needed at application startup
    }
}