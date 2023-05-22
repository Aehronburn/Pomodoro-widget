package it.unipd.dei.esp2023.sessions

import android.app.Application
import androidx.lifecycle.*
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import it.unipd.dei.esp2023.database.Session
import kotlinx.coroutines.launch

class SessionsViewModel(application: Application) : AndroidViewModel(application) {
    val database: PomodoroDatabaseDao

    //Mi serve questa lista di LiveData<Session> per poterla osservare da più posizioni
    val sessionList: LiveData<List<Session>>

    init {
        database = PomodoroDatabase.getInstance(application).databaseDao
        sessionList = database.getSessionList()
    }

    fun insertSession(name: String) {
        viewModelScope.launch {
            database.insertSession(Session(name = name))
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            database.deleteSession(session)
        }
    }

}