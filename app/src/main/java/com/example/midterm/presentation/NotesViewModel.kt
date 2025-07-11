package com.example.midterm.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midterm.data.Note
import com.example.midterm.data.NoteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel(
    private val dao: NoteDao
): ViewModel() {

private val isSortedByDateAdded = MutableStateFlow(true)
    private var notes =
        isSortedByDateAdded.flatMapLatest { sort ->
            if (sort) {
                dao.getNotesOrderedByDateAdded()
            }else{
                dao.getNotesOrderedByTitle()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val _state = MutableStateFlow(NoteState())
    val state =
        combine(_state, isSortedByDateAdded, notes) { state, isSortedByDateAdded, notes ->
            state.copy(
                notes = notes
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteState())
    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    dao.deleteNote(event.note)
                }
            }
            is NotesEvent.SaveNote -> {
                val note = Note(
                        id = if (event.noteID != -1) event.noteID else 0, // 0 để Room auto-generate nếu là note mới
                        title = state.value.title.value,
                        description = state.value.description.value,
                        dateAdded = System.currentTimeMillis(),
                    imageUri = event.imageUri // Gán vào đây
                    )
viewModelScope.launch {
    dao.upsertNote(note)
}
                _state.update {
                    it.copy(
                        title =mutableStateOf(""),
                        description = mutableStateOf(""),
                        imageUri = mutableStateOf(null)
                    )
                }

            }

            NotesEvent.SortNotes ->{
                isSortedByDateAdded.value = !isSortedByDateAdded.value
            }
        }
    }
}