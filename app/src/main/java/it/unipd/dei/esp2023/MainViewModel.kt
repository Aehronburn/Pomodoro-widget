package it.unipd.dei.esp2023

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import it.unipd.dei.esp2023.database.TaskExt


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database: PomodoroDatabaseDao

    init {
        database = PomodoroDatabase.getInstance(application).databaseDao
    }

    fun getTaskExtList(sessionId: Long): LiveData<List<TaskExt>> {
        return database.getTaskExtListFromSessionId(sessionId)
    }

}
