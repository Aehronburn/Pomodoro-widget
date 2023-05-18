package it.unipd.dei.esp2023.sessions

import android.app.Application
import androidx.lifecycle.*
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import it.unipd.dei.esp2023.database.Session
import kotlinx.coroutines.launch
import java.time.LocalDate

class SessionsViewModel(application: Application) : AndroidViewModel(application) {
    val database: PomodoroDatabaseDao
    //Mi serve questa lista di LiveData<Session> per poterla osservare da
    //più posizioni
    var mySessionList: LiveData<List<Session>>

    init {
        database = PomodoroDatabase.getInstance(application).databaseDao
        mySessionList = database.getSessionList()
    }

    //Serve solo per il Toast
    private val _newSessionName = MutableLiveData<String>("")
    val newSessionName : LiveData<String>
        get() = _newSessionName

    fun setNewSessionName(name: String) {
        _newSessionName.value = name
    }

    //Ho cancellato due coroutine insieme, senmnò si pestavano i piedi a vicenda
    //e beccavo sempre il NULL pointer exception
    //Richiamo l'inizializzazione del database tutto nel SessionFragment
}