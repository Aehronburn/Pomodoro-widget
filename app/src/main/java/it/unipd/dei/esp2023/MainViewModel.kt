package it.unipd.dei.esp2023

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import it.unipd.dei.esp2023.database.TaskExt
import it.unipd.dei.esp2023.settings.SettingsFragment


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val myDao: PomodoroDatabaseDao
    init{
        myDao = PomodoroDatabase.getInstance(application).databaseDao
    }
    fun getTaskExtList(sessionId: Long):LiveData<List<TaskExt>>{
        return myDao.getTaskExtListFromSessionId(sessionId)
    }

}
