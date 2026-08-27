package com.example.notes.data

import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl private constructor(
    private val notesDao: NotesDao,
    private val imageFileManager: ImageFileManager
) : NotesRepository {

    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val processedContent =  content.processForStorage()
        val noteDbModel = NoteDbModel(
            id = 0, title = title, updatedAt = updatedAt, isPinned = isPinned
        )
        val noteId = notesDao.addNote(noteDbModel).toInt()
        val contentItems = processedContent.toContentItemDbModels(noteId)
        notesDao.addNoteContent(contentItems)
    }

    override suspend fun deleteNote(noteId: Int) {
        val note = notesDao.getNote(noteId).toEntity()
        notesDao.deleteNote(noteId)
        note.content.filterIsInstance<ContentItem.Image>().forEach { image ->
            imageFileManager.deleteImageFromInternalStorage(image.url)
        }
    }

    override suspend fun editNote(note: Note) {
        val oldNote = notesDao.getNote(note.id).toEntity()
        val oldUrls = oldNote.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val newUrls = note.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val removedUrls = oldUrls - newUrls.toSet()

        removedUrls.forEach {
            imageFileManager.deleteImageFromInternalStorage(it)
        }

        val processedContent = note.content.processForStorage()
        val processedNote = note.copy(
            content = processedContent
        )

        notesDao.addNote(
            processedNote.toDbModel()
        )
        notesDao.deleteNoteContent(note.id)
        notesDao.addNoteContent(processedContent.toContentItemDbModels(note.id))
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

    private suspend fun List<ContentItem>.processForStorage(): List<ContentItem> {
        return map { contentItem ->
            when (contentItem) {
                is ContentItem.Image -> {
                    if (imageFileManager.isInternal(contentItem.url)) {
                        contentItem
                    } else {
                        val internalPath =
                            imageFileManager.copyImageToInternalStorage(contentItem.url)
                        ContentItem.Image(internalPath)
                    }
                }

                is ContentItem.Text -> {
                    contentItem
                }
            }
        }
    }

    companion object {
        private var instance: NotesRepositoryImpl? = null
        private val LOCK = Any()

        fun getInstance(
            notesDao: NotesDao,
            imageFileManager: ImageFileManager
        ): NotesRepositoryImpl {
            instance?.let { return it }
            synchronized(LOCK) {
                instance?.let { return it }

                return NotesRepositoryImpl(notesDao, imageFileManager).also { instance = it }
            }
        }
    }
}