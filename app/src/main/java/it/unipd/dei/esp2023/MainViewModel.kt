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

    private val _pomodoroDuration = MutableLiveData<Int>(1)
    val pomodoroDuration: LiveData<Int>
        get() = _pomodoroDuration

    private val _shortBreakDuration = MutableLiveData<Int>(1)
    val shortBreakDuration: LiveData<Int>
        get() = _shortBreakDuration

    private val _longBreakDuration = MutableLiveData<Int>(1)
    val longBreakDuration: LiveData<Int>
        get() = _longBreakDuration

    fun setNewSessionName(name: String) {
        _newSessionName.value = name
    }

    fun setPomodoroDuration(value: Int) {
        _pomodoroDuration.value = value
    }

    fun setShortBreakDuration(value: Int) {
        _shortBreakDuration.value = value
    }

    fun setLongBreakDuration(value: Int) {
        _longBreakDuration.value = value
    }
}
