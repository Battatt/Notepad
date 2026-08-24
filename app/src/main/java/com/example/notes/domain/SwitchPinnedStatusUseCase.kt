package com.example.notes.domain

import jakarta.inject.Inject

class SwitchPinnedStatusUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(noteId: Int) {
        repository.switchPinnedStatus(noteId)
    }
}