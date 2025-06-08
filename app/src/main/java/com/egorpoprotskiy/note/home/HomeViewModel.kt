package com.egorpoprotskiy.note.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.note.data.Note
import com.egorpoprotskiy.note.data.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//4.2
class HomeViewModel(val noteRepository: NoteRepository): ViewModel() {
    val homeUiState: StateFlow<HomeUiState> = noteRepository.getAllNotesStream()
        .map { HomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    fun deleteNote(notes: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(notes)
        }
    }

    companion object{
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(val noteList: List<Note> = listOf())