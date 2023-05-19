package it.unipd.dei.esp2023.session_details

import android.app.Application
import android.util.Log
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

    var sessionId: Long = 0
        get() { return field }
        set(id: Long) {
            Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, "SessionDetailsViewModel set id: $id")
            if(!initialized){
                Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, "!initialized")
                try {
                    Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, "Inizio try")
                    field = id
                    Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, "Dopo set field")
                    taskList = myDao.getTaskListFromSessionId(sessionId)
                    sessionInfo = myDao.getSessionFromId(sessionId)
                    Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, "Dopo query")
                }catch(e: Exception){
                    Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, "catch")
                    Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, e.toString())
                }
                initialized = true
                Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, "initialized settato true")
            }else{
                Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, "already initialized")
            }
        }

    init{
        myDao = PomodoroDatabase.getInstance(app).databaseDao
    }

}