package com.example.notes.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.presentation.screens.creation.CreateNoteScreen
import com.example.notes.presentation.screens.editing.EditNoteScreen
import com.example.notes.presentation.screens.notes.NotesScreen


@Composable
fun NavGraph() {
    val navController = rememberNavController()  // keep navigation state
    NavHost(
        navController = navController,
        startDestination = Screen.Notes.route,
    ) {
        composable(
            Screen.Notes.route
        ) {
            NotesScreen(
                onNoteClick = { note ->
                    navController.navigate(
                        Screen.EditNote.createRoute(note.id)
                    )
                },
                onFloatingActionButtonClick = {
                    navController.navigate(Screen.CreateNote.route)
                }
            )
        }
        composable(
            Screen.CreateNote.route
        ) {
            CreateNoteScreen(
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            Screen.EditNote.route
        ) { navBackStackEntry ->
            val noteId = Screen.EditNote.getNoteId(navBackStackEntry.arguments)
            EditNoteScreen(
                noteId = noteId,
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(
    val route: String
) {
    data object Notes : Screen("notes")
    data object CreateNote : Screen("create_note")
    data object EditNote : Screen("edit_note/{note_id}") {  // Bundle()
        fun createRoute(noteId: Int): String {
            return "edit_note/$noteId"
        }

        fun getNoteId(arguments: Bundle?): Int {
            return arguments?.getString("note_id")?.toInt() ?: 0
        }
    }
}