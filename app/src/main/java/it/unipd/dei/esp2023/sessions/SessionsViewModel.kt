package it.unipd.dei.esp2023.sessions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao

class SessionsViewModel(application: Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel

    val database: PomodoroDatabaseDao

    init {
        database = PomodoroDatabase.getInstance(application).databaseDao
    }

    //Serve solo per il Toast
    private val _newSessionName = MutableLiveData<String>("")
    val newSessionName : LiveData<String>
        get() = _newSessionName

    fun setNewSessionName(name: String) {
        _newSessionName.value = name
    }
}