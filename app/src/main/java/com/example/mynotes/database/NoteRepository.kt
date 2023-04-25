package com.example.mynotes.database

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {
    val getAllData: LiveData<List<Note>> = noteDao.getAllData()

    suspend fun insertData(note: Note){
        noteDao.insertData(note)
    }

    suspend fun updateData(note: Note){
        noteDao.updateData(note)
    }
    suspend fun deleteData(note: Note){
        noteDao.deleteData(note)
    }
    fun searchDatabase(searchQuery: String):LiveData<List<Note>>{
        return noteDao.searchDatabase(searchQuery)
    }
}