package com.example.notes.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.notes.presentation.screens.creation.CreateNoteScreen
import com.example.notes.presentation.screens.editing.EditNoteScreen
import com.example.notes.presentation.screens.notes.NotesScreen

@Composable
fun NavGraph() {
    val screen = remember {
        mutableStateOf<Screen>(Screen.Notes)
    }

    when (val currentScreen = screen.value) {
        Screen.CreateNote -> {
            CreateNoteScreen(
                onFinished = {
                    screen.value = Screen.Notes
                }
            )
        }
        is Screen.EditNote -> {
            EditNoteScreen(
                noteId = currentScreen.noteId,
                onFinished = {
                    screen.value = Screen.Notes
                }
            )
        }
        Screen.Notes -> {
            NotesScreen(
                onNoteClick = { note ->
                    Log.d(".NavGraph", "Note $note clicked!")
                    screen.value = Screen.EditNote(note.id)
                },
                onFloatingActionButtonClick = {
                    screen.value = Screen.CreateNote
                }
            )
        }
    }
}

sealed interface Screen {
    data object Notes : Screen
    data object CreateNote : Screen
    data class EditNote(val noteId: Int) : Screen
}