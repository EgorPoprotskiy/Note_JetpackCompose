package com.egorpoprotskiy.note.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.note.data.NoteRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NoteEditViewModel(
    savedStateHandle: SavedStateHandle,
    //12.2
    private val noteRepository: NoteRepository
) : ViewModel() {

    /**
     * Удерживает текущее состояние пользовательского интерфейса
     */
    var noteUiState by mutableStateOf(NoteUiState())
        private set

    private val noteId: Int = checkNotNull(savedStateHandle[NoteEditDestination.noteIdArg])

    private fun validateInput(uiState: NoteDetails = noteUiState.itemDetails): Boolean {
        // проверка на заполнение поля heading.
        return uiState.heading.isNotBlank()
            // проверка на заполнение всех полей.
//        with(uiState) {
//            heading.isNotBlank()
//                    && description.isNotBlank()
//                    && color.toString().isNotBlank()
//        }
    }
    //12.3 извлеките сведения о сущности с помощью itemsRepository.getItemStream(itemId) -> AppViewModelProvider
    init {
        viewModelScope.launch {
            noteUiState = noteRepository.getNoteStream(noteId)
                .filterNotNull()
                .first()
                .toItemUiState(true)
        }
    }
    //12.5 Эта функция обновляет itemUiStateновыми значениями, которые вводит пользователь. -> ItemEditScreen
    fun updateUiState(itemDetails: NoteDetails) {
        noteUiState = NoteUiState(
            itemDetails = itemDetails,
            isEntryValid = validateInput(itemDetails)
        )
    }
    //12.7 -> ItemEditScreen
    suspend fun updateItem() {
        if (validateInput(noteUiState.itemDetails)) {
            noteRepository.updateNote(noteUiState.itemDetails.toItem())
        }
    }
}