package com.example.notes.data

import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object TestNotesRepositoryImpl : NotesRepository {

    private val notesListFlow = MutableStateFlow<List<Note>>(listOf())

    override fun addNote(note: Note) {
        notesListFlow.update {
            it.toMutableList().apply {
                add(note)
            }
        }
    }

    override fun deleteNote(noteId: Int) {
        notesListFlow.update { list ->
            list.toMutableList().apply {
                removeIf { note ->  note.id == noteId }
            }
        }
    }

    override fun editNote(note: Note) {
        notesListFlow.update { list ->
            list.map { oldNote ->
                if (oldNote.id == note.id) {
                    note
                } else {
                    oldNote
                }
            }
        }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesListFlow.asStateFlow()
    }

    override fun getNote(noteId: Int): Note {
        return notesListFlow.value.first { note ->  note.id == noteId }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesListFlow.map { currentList ->
            currentList.filter {
                it.title.contains(query) || it.content.contains(query)
            }
        }
    }

    override fun switchPinnedStatus(noteId: Int) {
        notesListFlow.update { list ->
            list.map { oldNote ->
                if (oldNote.id == noteId) {
                    oldNote.copy(isPinned = !oldNote.isPinned)
                } else {
                    oldNote
                }
            }
        }
    }
}