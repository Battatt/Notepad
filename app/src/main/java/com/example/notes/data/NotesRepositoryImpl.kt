package com.example.notes.data

import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl private constructor(
    private val notesDao: NotesDao
) : NotesRepository {

    override suspend fun addNote(
        title: String,
        content: String,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val noteDbModel = NoteDbModel(
            id = 0,  // DB autogeneration
            title, content, updatedAt, isPinned
        )
        notesDao.addNote(noteDbModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        notesDao.deleteNote(noteId)
    }

    override suspend fun editNote(note: Note) {
        notesDao.addNote(
            note.toDbModel()
        )
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { notesList ->
            notesList.toEntities()
        }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { notesList ->
            notesList.toEntities()
        }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }

    companion object {
        private var instance: NotesRepositoryImpl? = null
        private val LOCK = Any()

        fun getInstance(notesDao: NotesDao): NotesRepositoryImpl {
            instance?.let { return it }
            synchronized(LOCK) {
                instance?.let { return it }

                return NotesRepositoryImpl(notesDao).also { instance = it }
            }
        }
    }
}