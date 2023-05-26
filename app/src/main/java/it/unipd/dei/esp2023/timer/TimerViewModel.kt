package it.unipd.dei.esp2023.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.unipd.dei.esp2023.database.TaskExt

class TimerViewModel : ViewModel() {

    private var pomodoroDuration: Int = 0
    private var shortBreakDuration: Int = 0
    private var longBreakDuration: Int = 0

    val phasesList: MutableList<Phase> = mutableListOf()

    private val _started = MutableLiveData<Boolean>(false)
    val started: LiveData<Boolean>
        get() = _started

    /*
    creates a list of phases from the task list of the session.
    Each phases
     */
    fun createPhasesList(list: List<TaskExt>) {
        var longBreakCounter = 0
        for(item in list) {
            for(i in item.completedPomodoros until item.totalPomodoros) {
                phasesList.add(Phase(item.id, item.name, pomodoroDuration * MILLISECONDS_IN_A_MINUTE))
                if(longBreakCounter == LONG_BREAK_FREQUENCY) {
                    phasesList.add(Phase(BREAK_ID, LONG_BREAK_NAME, longBreakDuration * MILLISECONDS_IN_A_MINUTE))
                    longBreakCounter = 0
                }
                else {
                    phasesList.add(Phase(BREAK_ID, SHORT_BREAK_NAME, shortBreakDuration * MILLISECONDS_IN_A_MINUTE))
                    longBreakCounter++
                }
            }
        }
        if(phasesList.last().taskId == BREAK_ID) phasesList.removeLast()
    }

    fun setPhasesDurations(pomodoro: Int, shortBreak: Int, longBreak: Int) {
        pomodoroDuration = pomodoro
        shortBreakDuration = shortBreak
        longBreakDuration = longBreak
    }

    fun setStarted(started: Boolean) {
        _started.value = started
    }

    companion object {
        private const val MILLISECONDS_IN_A_MINUTE : Long = 60000
        private const val SHORT_BREAK_NAME = "Short break"
        private const val LONG_BREAK_NAME = "Long break"
        private const val LONG_BREAK_FREQUENCY = 3
        private const val BREAK_ID: Long = -1
    }
}