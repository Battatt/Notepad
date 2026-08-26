@file:OptIn(ExperimentalFoundationApi::class)

package com.example.notes.presentation.screens.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.notes.R
import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import com.example.notes.presentation.ui.theme.OtherNotesColors
import com.example.notes.presentation.ui.theme.PinnedNotesColors
import com.example.notes.presentation.utils.DateFormatter

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel(),
    onNoteClick: (Note) -> Unit,
    onFloatingActionButtonClick: () -> Unit,

    ) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFloatingActionButtonClick,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_note),
                    contentDescription = "Button add note"
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
        ) {
            item {
                Title(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "All Notes"
                )
            }

            item {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }

            item {
                SearchBar(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    query = state.query,
                    onQueryChange = {
                        viewModel.processCommand(NotesCommand.InputSearchQuery(it))
                    }
                )
            }

            item {
                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }

            if (state.pinnedNotes.isNotEmpty()) {
                item {
                    SubTitle(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = "Pinned"
                    )
                }

                item {
                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }

                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)  // Without horizontal cropping content
                    ) {
                        itemsIndexed(
                            items = state.pinnedNotes,
                            key = { _, note -> note.id }
                        ) { index, note ->
                            NoteCard(
                                modifier = Modifier
                                    .widthIn(max = 160.dp),
                                note = note,
                                onNoteClick = onNoteClick,
                                onLongClick = {
                                    viewModel.processCommand(
                                        NotesCommand.SwitchPinnedStatus(note.id)
                                    )
                                },
                                backgroundColor = PinnedNotesColors[index % PinnedNotesColors.size],
                            )
                        }
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }

            item {
                SubTitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "Others"
                )
            }

            if (state.otherNotes.isNotEmpty()) {
                item {
                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }

                itemsIndexed(
                    items = state.otherNotes,
                    key = { _, note -> note.id }
                )
                { index, note ->
                    NoteCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        note = note,
                        onNoteClick = onNoteClick,
                        onLongClick = {
                            viewModel.processCommand(
                                NotesCommand.SwitchPinnedStatus(note.id)
                            )
                        },
                        backgroundColor = OtherNotesColors[index % OtherNotesColors.size],
                    )
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
            } else {
                item {
                    EmptyNotesMessage(
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }

        }
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    @Suppress("SameParameterValue") text: String = "Title",
) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search notes",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
private fun SubTitle(
    modifier: Modifier = Modifier,
    text: String = ""
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColor: Color,
    onNoteClick: (Note) -> Unit = {},
    onLongClick: (Note) -> Unit = {},
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .combinedClickable(
                onClick = {
                    onNoteClick(note)
                },
                onLongClick = {
                    onLongClick(note)
                },
            )
    ) {

        val noteFirstImage = if (note.isPinned) {
            null
        } else {
            note.content.filterIsInstance<ContentItem.Image>().firstOrNull()
        }

        TitleWithBackground(
            title = note.title,
            updatedAt = note.updatedAt,
            imageUrl = noteFirstImage?.url
        )

        note.content
            .filterIsInstance<ContentItem.Text>()
            .filter { it.content.isNotBlank() }
            .joinToString("\n") {
                it.content
            }.takeIf {
                it.isNotBlank()
            }?.let {
                Text(
                    modifier = Modifier
                        .padding(16.dp),
                    text = it,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }


    }
}

@Composable
private fun TitleWithBackground(
    modifier: Modifier = Modifier,
    title: String,
    updatedAt: Long,
    imageUrl: String?
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        var textColor = MaterialTheme.colorScheme.onSurfaceVariant

        imageUrl?.let {
            AsyncImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .heightIn(max = 120.dp)
                    .fillMaxWidth(),
                model = imageUrl,
                contentDescription = "First image from note",
                contentScale = ContentScale.FillWidth,
            )
            textColor = MaterialTheme.colorScheme.onPrimary
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomStart)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = DateFormatter.formatDateToString(updatedAt),
                fontSize = 12.sp,
                color = textColor,
            )

        }
    }
}

@Composable
private fun EmptyNotesMessage(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = "The Notes List is empty...",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}