package it.unipd.dei.esp2023.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {


    private val _pomodoroDuration = MutableLiveData<Int>(1)
    val pomodoroDuration: LiveData<Int>
        get() = _pomodoroDuration

    private val _shortBreakDuration = MutableLiveData<Int>(1)
    val shortBreakDuration: LiveData<Int>
        get() = _shortBreakDuration

    private val _longBreakDuration = MutableLiveData<Int>(1)
    val longBreakDuration: LiveData<Int>
        get() = _longBreakDuration

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