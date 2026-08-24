package com.example.notes.di

import android.content.Context
import com.example.notes.data.NotesDao
import com.example.notes.data.NotesDatabase
import com.example.notes.data.NotesRepositoryImpl
import com.example.notes.domain.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    companion object {
        @Singleton
        @Provides
        fun provideNotesRepositoryImpl(notesDao: NotesDao): NotesRepository {
            return NotesRepositoryImpl.getInstance(notesDao)
        }

        @Singleton
        @Provides
        fun provideNotesDatabase(
            @ApplicationContext context: Context
        ): NotesDatabase {
            return NotesDatabase.getInstance(context)
        }

        @Singleton
        @Provides
        fun provideNotesDao(
            database: NotesDatabase
        ): NotesDao {
            return database.notesDao()
        }
    }
}