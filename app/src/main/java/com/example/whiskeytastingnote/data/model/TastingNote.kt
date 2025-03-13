// app/src/main/java/com/example/whiskeytastingnote/data/model/TastingNote.kt
package com.example.whiskeytastingnote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.whiskeytastingnote.data.database.Converters
import java.util.Date

/**
 * Entity class representing a whiskey tasting note
 */
@Entity(tableName = "tasting_notes")
@TypeConverters(Converters::class)
data class TastingNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Basic information
    val name: String = "",
    val date: Date = Date(),
    val abv: String = "",
    val score: String = "",

    // Image
    val imagePath: String? = null,

    // Color (SRM value)
    val color: Float = 1.4f,

    // Aroma intensity values
    val aromaSpice: Int = 0,
    val aromaPeat: Int = 0,
    val aromaGrain: Int = 0,
    val aromaFloral: Int = 0,
    val aromaFruit: Int = 0,
    val aromaWood: Int = 0,
    val aromaOther: Int = 0,

    // Selected aromas
    val selectedAromas: List<Aroma> = emptyList(),

    // Palate intensity values
    val palateSweetness: Int = 5,
    val palateSourness: Int = 5,
    val palateBitterness: Int = 5,
    val palateFatty: Int = 5,
    val palateSalty: Int = 5,
    val palateUmami: Int = 5,

    // Retronasal aroma intensity values
    val retronasalSpice: Int = 0,
    val retronasalPeat: Int = 0,
    val retronasalGrain: Int = 0,
    val retronasalFloral: Int = 0,
    val retronasalFruit: Int = 0,
    val retronasalWood: Int = 0,
    val retronasalOther: Int = 0,

    // Selected retronasal aromas
    val selectedRetronasalAromas: List<Aroma> = emptyList(),

    // Character values
    val characters: List<WhiskeyCharacter> = listOf(
        WhiskeyCharacter("드라이", "오일리", 5),
        WhiskeyCharacter("가벼운", "무거운", 5),
        WhiskeyCharacter("순한", "매운", 5),
        WhiskeyCharacter("부드러운", "강렬한", 5)
    ),

    // Comments
    val noseComment: String = "",
    val palateComment: String = "",
    val finishComment: String = "",
    val overallComment: String = "",

    // Sync status
    val isSynced: Boolean = false,
    val lastModified: Date = Date()
)