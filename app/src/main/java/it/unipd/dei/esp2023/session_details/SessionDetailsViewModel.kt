package it.unipd.dei.esp2023.session_details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import it.unipd.dei.esp2023.database.Session
import it.unipd.dei.esp2023.database.Task


class SessionDetailsViewModel(app: Application): AndroidViewModel(app) {
    lateinit var taskList: LiveData<List<Task>>
    lateinit var sessionInfo: LiveData<Session>
    private val myDao: PomodoroDatabaseDao
    var initialized: Boolean = false

    var sessionId: Long
        get() { return sessionId }
        set(id: Long) {
            if(!initialized){
                sessionId = id
                taskList = myDao.getTaskListFromSessionId(sessionId)
                sessionInfo = myDao.getSessionFromId(sessionId)
                initialized = true
            }
        }

    init{
        myDao = PomodoroDatabase.getInstance(app).databaseDao
    }

}