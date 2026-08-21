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

    override suspend fun addNote(
        title: String,
        content: String,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        notesListFlow.update { oldList ->
            val note = Note(
                id = oldList.size,
                title = title,
                content = content,
                updatedAt = updatedAt,
                isPinned = isPinned,
                )

            oldList.toMutableList().apply {
                add(note)
            }
        }
    }

    override suspend fun deleteNote(noteId: Int) {
        notesListFlow.update { list ->
            list.toMutableList().apply {
                removeIf { note ->  note.id == noteId }
            }
        }
    }

    override suspend fun editNote(note: Note) {
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

    override suspend fun getNote(noteId: Int): Note {
        return notesListFlow.value.first { note ->  note.id == noteId }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesListFlow.map { currentList ->
            currentList.filter {
                it.title.contains(query) || it.content.contains(query)
            }
        }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
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