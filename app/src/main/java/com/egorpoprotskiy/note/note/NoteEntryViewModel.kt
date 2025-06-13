package com.egorpoprotskiy.note.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.egorpoprotskiy.note.data.Note
import com.egorpoprotskiy.note.data.NoteRepository
import com.egorpoprotskiy.note.R

class NoteEntryViewModel(private val noteRepository: NoteRepository): ViewModel() {
    var noteUiState by mutableStateOf(NoteUiState())
        private set

    fun updateUiState(itemDetails: NoteDetails) {
        noteUiState =
            NoteUiState(
                itemDetails = itemDetails,
                isEntryValid = validateInput(itemDetails)
            )
    }

    private fun validateInput(uiState: NoteDetails = noteUiState.itemDetails): Boolean {
        // проверка на заполнение поля heading.
        return uiState.heading.isNotBlank()
        // проверка на заполнение всех полей.
//        with(uiState) {
//            heading.isNotBlank() && description.isNotBlank() && color.toString().isNotBlank()
//        }
    }
    suspend fun saveItem() {
        if (validateInput()) {
            noteRepository.insertNote(noteUiState.itemDetails.toItem())
        }
    }
}

data class NoteUiState(
    val itemDetails:NoteDetails = NoteDetails(),
    val isEntryValid: Boolean = false
)
data class NoteDetails(
    val id: Int = 0,
    val heading: String = "",
    val description: String = "",
    val color: Int = R.color.white
)
fun NoteDetails.toItem(): Note = Note(
    id = id,
    heading = heading,
    description = description,
    color = color
)

fun Note.toItemUiState(isEntryValid: Boolean = false): NoteUiState = NoteUiState(
    itemDetails = this.toNoteDetails(),
    isEntryValid = isEntryValid
)


fun Note.toNoteDetails(): NoteDetails = NoteDetails(
    id = id,
    heading = heading,
    description = description,
    color = color
)