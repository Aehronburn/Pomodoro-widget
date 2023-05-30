package it.unipd.dei.esp2023.timer

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.unipd.dei.esp2023.database.CompletedPomodoro
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import it.unipd.dei.esp2023.database.TaskExt
import it.unipd.dei.esp2023.service.TimerService
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val database: PomodoroDatabaseDao

    init{
        database = PomodoroDatabase.getInstance(application).databaseDao
    }

    var replyMessenger: Messenger = Messenger(HandlerReplyMsg(this))

    private var pomodoroDuration: Int = 0
    private var shortBreakDuration: Int = 0
    private var longBreakDuration: Int = 0

    /*
    list of pomodoros(one for each pomodoro of a task, interleaved with breaks) of a task list
     */
    private val phasesList: MutableList<Phase> = mutableListOf()

    private val _remainingMinutes =  MutableLiveData<Int>(0)
    val remainingMinutes : LiveData<Int>
        get() = _remainingMinutes

    private val _remainingSeconds = MutableLiveData<Int>(0)
    val remainingSeconds: LiveData<Int>
        get() = _remainingSeconds


    /*
    is there already a countdown timer which has been created previously?
     */
    private val _isStarted = MutableLiveData<Boolean>(false)
    val isStarted: LiveData<Boolean>
        get() = _isStarted

    /*
    is the countdown timer running or paused?
     */
    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    /*
    are all tasks completed=
     */
    private var _isPhasesListCompleted = MutableLiveData<Boolean>(false)
    val isPhasesListCompleted: LiveData<Boolean>
        get() = _isPhasesListCompleted

    /*
    first phase of the phasesList
     */
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
        if(phasesList.isNotEmpty()) {
            if(phasesList.last().taskId == BREAK_ID) phasesList.removeLast()

            updateCurrentPhase()
            //initializes time left text views with full duration of the phase. Il will be then updated by the service only
            setRemainingTime(_currentPhase.value!!.duration * TimerService.ONE_MINUTE_IN_MS.toInt())
        } else {
            _isPhasesListCompleted.value = true
        }
        Log.d("debug", phasesList.toString())
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

    fun setRemainingTime(remainingTimeMillis: Int) {
        _remainingMinutes.value = remainingTimeMillis / TimerService.ONE_MINUTE_IN_MS.toInt()
        _remainingSeconds.value = (remainingTimeMillis % TimerService.ONE_MINUTE_IN_MS.toInt()) / ONE_SECOND_IN_MS
    }

    fun insertCompletedPomodoro() {
        /*
        insert pomodoro into database if it is not a short or long break
         */
        if(_currentPhase.value!!.taskId != BREAK_ID) {
            viewModelScope.launch {
                database.insertCompletedPomodoro(CompletedPomodoro(task = _currentPhase.value!!.taskId, duration = _currentPhase.value!!.duration))
            }
        }
    }

    // https://stackoverflow.com/questions/14351674/send-data-from-service-to-activity-android
    internal class HandlerReplyMsg(val viewModel: TimerViewModel) : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                TimerService.PROGRESS_STATUS_RUNNING -> {
                    viewModel.setIsStarted(true)
                    viewModel.setIsPlaying(true)
                    viewModel.setRemainingTime(msg.arg1)
                }
                TimerService.PROGRESS_STATUS_DELETED -> {
                    viewModel.setIsStarted(false)
                    viewModel.setIsPlaying(false)
                    viewModel.setRemainingTime(msg.arg1)
                }
                TimerService.PROGRESS_STATUS_PAUSED -> {
                    viewModel.setIsStarted(true)
                    viewModel.setIsPlaying(false)
                    viewModel.setRemainingTime(msg.arg1)
                }
                TimerService.PROGRESS_STATUS_COMPLETED -> {
                    viewModel.setIsStarted(false)
                    viewModel.setIsPlaying(false)
                    viewModel.insertCompletedPomodoro()
                }
            }
        }
    }

    companion object {
        private const val SHORT_BREAK_NAME = "Short break" // TODO strings
        private const val LONG_BREAK_NAME = "Long break" // TODO strings
        private const val LONG_BREAK_FREQUENCY = 3 //one long break each 4 pomodoros(0-3)
        private const val BREAK_ID: Long = -1
        private const val ONE_SECOND_IN_MS = 1000
    }
}