package it.unipd.dei.esp2023

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database: PomodoroDatabaseDao

    init {
        database = PomodoroDatabase.getInstance(application).databaseDao
    }

    private val _newSessionName = MutableLiveData<String>("")
    val newSessionName : LiveData<String>
        get() = _newSessionName

    fun setNewSessionName(name: String) {
        _newSessionName.value = name
    }
}
