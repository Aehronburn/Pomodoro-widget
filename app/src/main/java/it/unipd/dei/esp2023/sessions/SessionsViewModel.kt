package it.unipd.dei.esp2023.sessions

import android.app.Application
import androidx.lifecycle.*
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import it.unipd.dei.esp2023.database.Session
import kotlinx.coroutines.launch
import java.time.LocalDate

class SessionsViewModel(application: Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel

    val database: PomodoroDatabaseDao

    init {
        database = PomodoroDatabase.getInstance(application).databaseDao

        //Prova
        viewModelScope.launch {
            setDefault()
        }
    }

    //Serve solo per il Toast
    private val _newSessionName = MutableLiveData<String>("")
    val newSessionName : LiveData<String>
        get() = _newSessionName

    fun setNewSessionName(name: String) {
        _newSessionName.value = name
    }


    //Prova
    private suspend fun setDefault(){
        //Insert one default Session
        val defaultSession = Session(0L,"Hello", LocalDate.now().toString())
        database.insertSession(defaultSession)
    }
}