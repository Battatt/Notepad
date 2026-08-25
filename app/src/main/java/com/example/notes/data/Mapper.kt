package com.example.notes.data

import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Note.toDbModel(): NoteDbModel {
    val contentAsString = Json.encodeToString(
        content.toContentItemDbModels()
    )
    return NoteDbModel(id, title, contentAsString, updatedAt, isPinned)
}

fun List<ContentItem>.toContentItemDbModels(): List<ContentItemDbModel> {
    return this.map { contentItem ->
        when (contentItem) {
            is ContentItem.Image -> ContentItemDbModel.Image(contentItem.url)
            is ContentItem.Text -> ContentItemDbModel.Text(contentItem.content)
        }
    }
}

fun List<ContentItemDbModel>.toContentItems(): List<ContentItem> {
    return this.map { contentItemDbModel ->
        when (contentItemDbModel) {
            is ContentItemDbModel.Image -> ContentItem.Image(contentItemDbModel.url)
            is ContentItemDbModel.Text -> ContentItem.Text(contentItemDbModel.content)
        }
    }
}

fun NoteDbModel.toEntity(): Note {
    val content = Json.decodeFromString<List<ContentItemDbModel>>(content).toContentItems()
    return Note(id, title, content, updatedAt, isPinned)
}

fun List<NoteDbModel>.toEntities(): List<Note> {
    return map { it.toEntity() }
}