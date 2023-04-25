package com.example.mynotes.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application):AndroidViewModel(application) {

    private val noteDao = NoteDatabase.getDatabase(application).noteDao()
    private val repository: NoteRepository
    val getAllData: LiveData<List<Note>>

    init {
        repository= NoteRepository(noteDao)
        getAllData = repository.getAllData

    }
    fun insertData(note: Note){
        viewModelScope.launch (Dispatchers.IO){
            repository.insertData(note)
        }
    }
    fun updateData(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateData(note)
        }
    }
    fun deleteData(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteData(note)
        }
    }
    fun searchDatabase(searchQuery: String):LiveData<List<Note>>{
        return repository.searchDatabase(searchQuery)
    }

}