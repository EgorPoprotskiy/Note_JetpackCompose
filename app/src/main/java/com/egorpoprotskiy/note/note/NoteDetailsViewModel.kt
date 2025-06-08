package com.egorpoprotskiy.note.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.note.data.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NoteDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
): ViewModel() {
    private val noteId: Int = checkNotNull(savedStateHandle[NoteDetailsDestination.noteIdArg])
    val uiState: StateFlow<NoteDetailsUiState> =
        noteRepository.getNoteStream(noteId)
            .filterNotNull()
            .map {
                NoteDetailsUiState(outOfStock = it.color <= "0", noteDetails = it.toNoteDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = NoteDetailsUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun deleteNote() {
        noteRepository.deleteNote(uiState.value.noteDetails.toItem())
    }
}

data class NoteDetailsUiState(
    val outOfStock: Boolean = true,
    val noteDetails: NoteDetails = NoteDetails()
)