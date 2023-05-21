package it.unipd.dei.esp2023.sessions

import android.app.Application
import androidx.lifecycle.*
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import it.unipd.dei.esp2023.database.Session
import kotlinx.coroutines.cancel
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

    //Solo quando il viewModel non mi serve più e viene cancellato,
    //allora annullo tutte le coroutine che stanno andando
    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    //Ho cancellato due coroutine insieme, sennò si pestavano i piedi a vicenda
    //e beccavo sempre il NULL pointer exception
    //Richiamo l'inizializzazione del database tutto nel SessionFragment
}