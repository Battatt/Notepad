package com.example.notes.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notes.presentation.screens.notes.NotesScreen
import com.example.notes.presentation.ui.theme.NotesTheme

private const val TAG = ".MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                NotesScreen(
                    onNoteClick = {
                        Log.d(TAG, "onNoteCLick: $it")
                    },
                    onFloatingActionButtonClick = {
                        Log.d(TAG, "onFloatingActionButtonClick")
                    }
                )
            }
        }
    }
}