package com.example.midterm.presentation

import com.example.midterm.data.Note

sealed interface NotesEvent {

    object SortNotes: NotesEvent
    data class DeleteNote(val note: Note): NotesEvent
    data class SaveNote(
        val noteID: Int = -1,
        val title: String,
        val description: String,
        val imageUri: String? = null
    ): NotesEvent
}