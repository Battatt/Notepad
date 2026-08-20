package com.example.notes.presentation.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.domain.Note

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier
            .padding(top = 40.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            LazyRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.pinnedNotes,
                    key = { note -> note.id }
                ) { note ->
                    NotesCard(
                        note = note,
                        onNoteClick = {
                            viewModel.processCommand(
                                NotesCommand.SwitchPinnedStatus(note.id)
                            )
                        }
                    )
                }
            }
        }

        items(
            items = state.otherNotes,
            key = { note -> note.id }
            ) { note ->
            NotesCard(
                note = note,
                onNoteClick = {
                    viewModel.processCommand(
                        NotesCommand.SwitchPinnedStatus(note.id)
                    )
                }
            )
        }
    }
}

@Composable
fun NotesCard(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (Note) -> Unit = {},
) {
    Text(
        modifier = modifier.clickable {
            onNoteClick(note)
        },
        text = "${note.title} - ${note.content}",
        fontSize = 24.sp,
    )
}