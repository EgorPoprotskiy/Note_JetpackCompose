package com.egorpoprotskiy.note.data

import android.content.Context

//3.5
interface AppContainer {
    val notesRepository: NoteRepository
}

class AppDataContainer(private val context: Context): AppContainer {
    override val notesRepository: NoteRepository by lazy {
        OfflineNotesRepository(NoteDatabase.getDatabase(context).noteDao())
    }
}