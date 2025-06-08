package com.egorpoprotskiy.note.data

import kotlinx.coroutines.flow.Flow

//3.3
interface NoteRepository {
    fun getAllNotesStream(): Flow<List<Note>>
    fun getNoteStream(id: Int): Flow<Note?>
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun updateNote(note: Note)
}