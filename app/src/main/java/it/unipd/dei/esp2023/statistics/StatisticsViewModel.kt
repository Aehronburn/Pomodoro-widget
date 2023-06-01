package it.unipd.dei.esp2023.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val database: PomodoroDatabaseDao

    init {
        database = PomodoroDatabase.getInstance(application).databaseDao
    }

    val todayStats = database.getTodayStatistics()

    val monthStats = database.getCurrentMonthStatistics()

    val weeksStats = database.getCurrentWeekStatistics()

}