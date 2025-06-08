package com.egorpoprotskiy.note

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.egorpoprotskiy.note.home.HomeViewModel
import com.egorpoprotskiy.note.note.NoteDetailsViewModel
import com.egorpoprotskiy.note.note.NoteEditViewModel
import com.egorpoprotskiy.note.note.NoteEntryViewModel

object AppViewModelProvider {
    val factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            NoteEditViewModel(
                this.createSavedStateHandle(),
                //12.4 -> ItemViewModel
                noteApplication().container.notesRepository
            )
        }
        // Initializer for ItemEntryViewModel
        initializer {
            //6.2 передайте экземпляр репозитория в качестве параметра. -> ItemEntryViewModel
            NoteEntryViewModel(noteApplication().container.notesRepository)
        }

        // Initializer for ItemDetailsViewModel
        initializer {
            NoteDetailsViewModel(
                this.createSavedStateHandle(),
                //10.5 обновите инициализатор. -> ItemDetailsScreen
                noteApplication().container.notesRepository
            )
        }

        // Initializer for HomeViewModel
        initializer {
            //7.2 передайте ItemsRepositoryобъект. -> HomeViewModel
            HomeViewModel(noteApplication().container.notesRepository)
        }
    }

    fun CreationExtras.noteApplication(): NoteApplication =
        (this[AndroidViewModelFactory.APPLICATION_KEY] as NoteApplication)

}