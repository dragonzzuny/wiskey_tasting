// app/src/main/java/com/example/whiskeytastingnote/ui/note/NoteEditorViewModel.kt
package com.example.whiskeytastingnote.ui.note

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.whiskeytastingnote.WhiskeyTastingApp
import com.example.whiskeytastingnote.data.model.Aroma
import com.example.whiskeytastingnote.data.model.TastingNote
import com.example.whiskeytastingnote.data.model.WhiskeyCharacter
import com.example.whiskeytastingnote.data.repository.TastingNoteRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel for the note editor screen
 */
class NoteEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TastingNoteRepository
    private var noteId: Long? = null
    var imageUri by mutableStateOf<Uri?>(null)
    var imagePath by mutableStateOf<String?>(null)

    // Basic info
    var name by mutableStateOf("")
    var abv by mutableStateOf("")
    var score by mutableStateOf("")
    var color by mutableFloatStateOf(1.4f)

    // Aroma
    var aromaSpice by mutableIntStateOf(0)
    var aromaPeat by mutableIntStateOf(0)
    var aromaGrain by mutableIntStateOf(0)
    var aromaFloral by mutableIntStateOf(0)
    var aromaFruit by mutableIntStateOf(0)
    var aromaWood by mutableIntStateOf(0)
    var aromaOther by mutableIntStateOf(0)

    // Selected aromas
    val selectedAromas = mutableStateListOf<Aroma>()

    // Palate
    var palateSweetness by mutableIntStateOf(5)
    var palateSourness by mutableIntStateOf(5)
    var palateBitterness by mutableIntStateOf(5)
    var palateFatty by mutableIntStateOf(5)
    var palateSalty by mutableIntStateOf(5)
    var palateUmami by mutableIntStateOf(5)

    // Retronasal aroma
    var retronasalSpice by mutableIntStateOf(0)
    var retronasalPeat by mutableIntStateOf(0)
    var retronasalGrain by mutableIntStateOf(0)
    var retronasalFloral by mutableIntStateOf(0)
    var retronasalFruit by mutableIntStateOf(0)
    var retronasalWood by mutableIntStateOf(0)
    var retronasalOther by mutableIntStateOf(0)

    // Selected retronasal aromas
    val selectedRetronasalAromas = mutableStateListOf<Aroma>()

    // Characters
    val characters = mutableStateListOf(
        WhiskeyCharacter("드라이", "오일리", 5),
        WhiskeyCharacter("가벼운", "무거운", 5),
        WhiskeyCharacter("순한", "매운", 5),
        WhiskeyCharacter("부드러운", "강렬한", 5)
    )

    // Comments
    var noseComment by mutableStateOf("")
    var palateComment by mutableStateOf("")
    var finishComment by mutableStateOf("")
    var overallComment by mutableStateOf("")

    init {
        val app = application as WhiskeyTastingApp
        repository = app.repository
    }

    /**
     * Load an existing note by ID
     */
    fun loadNote(id: Long) {
        viewModelScope.launch {
            val note = repository.getNoteById(id).first()
            note?.let {
                noteId = it.id
                name = it.name
                abv = it.abv
                score = it.score
                color = it.color
                imagePath = it.imagePath

                // Load aroma values
                aromaSpice = it.aromaSpice
                aromaPeat = it.aromaPeat
                aromaGrain = it.aromaGrain
                aromaFloral = it.aromaFloral
                aromaFruit = it.aromaFruit
                aromaWood = it.aromaWood
                aromaOther = it.aromaOther

                // Load selected aromas
                selectedAromas.clear()
                selectedAromas.addAll(it.selectedAromas)

                // Load palate values
                palateSweetness = it.palateSweetness
                palateSourness = it.palateSourness
                palateBitterness = it.palateBitterness
                palateFatty = it.palateFatty
                palateSalty = it.palateSalty
                palateUmami = it.palateUmami

                // Load retronasal aroma values
                retronasalSpice = it.retronasalSpice
                retronasalPeat = it.retronasalPeat
                retronasalGrain = it.retronasalGrain
                retronasalFloral = it.retronasalFloral
                retronasalFruit = it.retronasalFruit
                retronasalWood = it.retronasalWood
                retronasalOther = it.retronasalOther

                // Load selected retronasal aromas
                selectedRetronasalAromas.clear()
                selectedRetronasalAromas.addAll(it.selectedRetronasalAromas)

                // Load characters
                characters.clear()
                characters.addAll(it.characters)

                // Load comments
                noseComment = it.noseComment
                palateComment = it.palateComment
                finishComment = it.finishComment
                overallComment = it.overallComment
            }
        }
    }

    /**
     * Save the current note
     */
    fun saveNote(): Boolean {
        try {
            val note = TastingNote(
                id = noteId ?: 0,
                name = name,
                date = Date(),
                abv = abv,
                score = score,
                imagePath = imagePath,
                color = color,

                // Aroma values
                aromaSpice = aromaSpice,
                aromaPeat = aromaPeat,
                aromaGrain = aromaGrain,
                aromaFloral = aromaFloral,
                aromaFruit = aromaFruit,
                aromaWood = aromaWood,
                aromaOther = aromaOther,

                // Selected aromas
                selectedAromas = selectedAromas.toList(),

                // Palate values
                palateSweetness = palateSweetness,
                palateSourness = palateSourness,
                palateBitterness = palateBitterness,
                palateFatty = palateFatty,
                palateSalty = palateSalty,
                palateUmami = palateUmami,

                // Retronasal aroma values
                retronasalSpice = retronasalSpice,
                retronasalPeat = retronasalPeat,
                retronasalGrain = retronasalGrain,
                retronasalFloral = retronasalFloral,
                retronasalFruit = retronasalFruit,
                retronasalWood = retronasalWood,
                retronasalOther = retronasalOther,

                // Selected retronasal aromas
                selectedRetronasalAromas = selectedRetronasalAromas.toList(),

                // Characters
                characters = characters.toList(),

                // Comments
                noseComment = noseComment,
                palateComment = palateComment,
                finishComment = finishComment,
                overallComment = overallComment,

                // Sync status
                isSynced = false,
                lastModified = Date()
            )

            viewModelScope.launch {
                if (noteId != null) {
                    repository.update(note)
                } else {
                    val newId = repository.insert(note)
                    noteId = newId
                }
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Toggle aroma selection
     */
    fun toggleAroma(category: String, subCategory: String, aromaName: String) {
        val aroma = Aroma(category, subCategory, aromaName)
        val isSelected = selectedAromas.any {
            it.category == category && it.subCategory == subCategory && it.name == aromaName
        }

        if (isSelected) {
            selectedAromas.removeIf {
                it.category == category && it.subCategory == subCategory && it.name == aromaName
            }
        } else {
            selectedAromas.add(aroma)
        }
    }

    /**
     * Toggle retronasal aroma selection
     */
    fun toggleRetronasalAroma(category: String, subCategory: String, aromaName: String) {
        val aroma = Aroma(category, subCategory, aromaName)
        val isSelected = selectedRetronasalAromas.any {
            it.category == category && it.subCategory == subCategory && it.name == aromaName
        }

        if (isSelected) {
            selectedRetronasalAromas.removeIf {
                it.category == category && it.subCategory == subCategory && it.name == aromaName
            }
        } else {
            selectedRetronasalAromas.add(aroma)
        }
    }

    /**
     * Update character value
     */
    fun updateCharacter(index: Int, value: Int) {
        if (index in characters.indices) {
            val character = characters[index]
            characters[index] = character.copy(value = value)
        }
    }

    /**
     * Set image URI
     */
    //fun setImageUri(uri: Uri) {
    //    imageUri = uri
    //}

    /**
     * Get image URI
     */
    //fun getImageUri(): Uri? = imageUri

    /**
     * Set image path
     */
    //fun setImagePath(path: String) {
    //    imagePath = path
    //}
    fun updateImageUri(uri: Uri) {
        currentImageUri = uri
    }
    fun updateImagePath(path: String) {
        currentImagePath = path
    }
}