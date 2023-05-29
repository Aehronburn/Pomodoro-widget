package it.unipd.dei.esp2023.timer

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.unipd.dei.esp2023.database.TaskExt
import it.unipd.dei.esp2023.service.TimerService


class TimerViewModel : ViewModel() {

    private var pomodoroDuration: Int = 0
    private var shortBreakDuration: Int = 0
    private var longBreakDuration: Int = 0

    private val phasesList: MutableList<Phase> = mutableListOf()

    /*
    is the countdown timer started
     */
    private val _isStarted = MutableLiveData<Boolean>(false)
    val isStarted: LiveData<Boolean>
        get() = _isStarted

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    private var _currentPhase = MutableLiveData<Phase>()
    val currentPhase: LiveData<Phase>
        get() = _currentPhase
    /*
    creates a list of phases from the task list of the session.
    After a task pomodoro a short break is added. After 4 task pomodoros a log break is added.
     */
    fun createPhasesList(list: List<TaskExt>) {
        var longBreakCounter = 0
        for(item in list) {
            for(i in item.completedPomodoros until item.totalPomodoros) {
                phasesList.add(Phase(item.id, item.name, pomodoroDuration))
                if(longBreakCounter == LONG_BREAK_FREQUENCY) {
                    phasesList.add(Phase(BREAK_ID, LONG_BREAK_NAME, longBreakDuration))
                    longBreakCounter = 0
                }
                else {
                    phasesList.add(Phase(BREAK_ID, SHORT_BREAK_NAME, shortBreakDuration))
                    longBreakCounter++
                }
            }
        }
        if(phasesList.last().taskId == BREAK_ID) phasesList.removeLast()
    }

    /*
    initialise phases durations. To be called before createPhaseList()
     */
    fun setPhasesDurations(pomodoro: Int, shortBreak: Int, longBreak: Int) {
        pomodoroDuration = pomodoro
        shortBreakDuration = shortBreak
        longBreakDuration = longBreak
    }

    fun setIsStarted(started: Boolean) {
        _isStarted.value = started
    }

    fun setIsPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun updateCurrentPhase() {
        _currentPhase.value = phasesList.first()
    }
    var replyMessenger: Messenger = Messenger(HandlerReplyMsg(this))

    // https://stackoverflow.com/questions/14351674/send-data-from-service-to-activity-android
    internal class HandlerReplyMsg(val viewModel: TimerViewModel) : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                TimerService.PROGRESS_STATUS_RUNNING -> {
                    viewModel.setIsStarted(true)
                    viewModel.setIsPlaying(true)
                }
                TimerService.PROGRESS_STATUS_DELETED -> {
                    viewModel.setIsStarted(false)
                    viewModel.setIsPlaying(false)
                }
                TimerService.PROGRESS_STATUS_PAUSED -> {
                    viewModel.setIsStarted(true)
                    viewModel.setIsPlaying(false)
                }
                TimerService.PROGRESS_STATUS_COMPLETED -> {
                    viewModel.setIsStarted(false)
                    viewModel.setIsPlaying(false)
                }
            }
        }
    }

    companion object {
        private const val SHORT_BREAK_NAME = "Short break" // TODO strings
        private const val LONG_BREAK_NAME = "Long break" // TODO strings
        private const val LONG_BREAK_FREQUENCY = 3 //one long break each 4 pomodoros(0-3)
        private const val BREAK_ID: Long = -1
    }
}