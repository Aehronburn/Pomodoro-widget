package it.unipd.dei.esp2023.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val database: PomodoroDatabaseDao

    init {
        database = PomodoroDatabase.getInstance(application).databaseDao
    }

    val todayStats = database.getTodayStatistics()

    val weekStats = database.getCurrentWeekStatistics()

    val monthStats = database.getCurrentMonthStatistics()

    /*
    today
     */
    private val _todayFocusTime = MutableLiveData<Pair<Int, Int>>(Pair(0, 0))
    val todayFocusTime: LiveData<Pair<Int, Int>>
        get() = _todayFocusTime

    private val _todayCompleted = MutableLiveData<Int>(0)
    val todayCompleted: LiveData<Int>
        get() = _todayCompleted

    /*
    week
     */
    private val _weekFocusTime = MutableLiveData<Pair<Int, Int>>(Pair(0, 0))
    val weekFocusTime: LiveData<Pair<Int, Int>>
        get() = _weekFocusTime

    private val _weekCompleted = MutableLiveData<Int>(0)
    val weekCompleted: LiveData<Int>
        get() = _weekCompleted

    /*
    month
     */
    private val _monthFocusTime = MutableLiveData<Pair<Int, Int>>(Pair(0, 0))
    val monthFocusTime: LiveData<Pair<Int, Int>>
        get() = _monthFocusTime

    private val _monthCompleted = MutableLiveData<Int>(0)
    val monthCompleted: LiveData<Int>
        get() = _monthCompleted

    //region setters

    fun setTodayCompleted(completed: Int) {
        _todayCompleted.value = completed
    }
    fun setTodayFocusTime(focusTime: Int) {
        _todayFocusTime.value = timeToHhMm(focusTime)
    }

    fun setWeekCompleted(completed: Int) {
        _weekCompleted.value = completed
    }
    fun setWeekFocusTime(focusTime: Int) {
        _weekFocusTime.value = timeToHhMm(focusTime)
    }

    fun setMonthCompleted(completed: Int) {
        _monthCompleted.value = completed
    }
    fun setMonthFocusTime(focusTime: Int) {
        _monthFocusTime.value = timeToHhMm(focusTime)
    }
    //endregion

    companion object {

        /*
        Given time in minutes, returns the number of hours and number of minutes that do not make into an hour
         */
        fun timeToHhMm(time: Int) : Pair<Int, Int> {
            return Pair(time / 60, time % 60)
        }
    }
}