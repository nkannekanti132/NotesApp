package com.example.notesapp.repository

import androidx.lifecycle.LiveData
import com.example.notesapp.database.NoteDatabase
import com.example.notesapp.model.Note

class NoteRepository(private val db:NoteDatabase) {

    suspend fun insertNote(note: Note) = db.getNoteDAO().insertNote(note)

    suspend fun deleteNote(note: Note) = db.getNoteDAO().deleteNote(note)

    suspend fun updateNote(note: Note) = db.getNoteDAO().updateNote(note)

    fun getNotes():LiveData<List<Note>> = db.getNoteDAO().getAllNotes()

    fun searchNotes(query: String?):LiveData<List<Note>> = db.getNoteDAO().searchNote(query)

    fun getPinnedNotes(): LiveData<List<Note>> = db.getNoteDAO().getPinnedNotes()
}