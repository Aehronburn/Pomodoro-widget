package it.unipd.dei.esp2023.timer

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
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

    var isInitialized = false

    /*
    messenger that will be sent as replyTo of outcoming message to service, which will use it to message back status changes
     */
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
    remaining total time in seconds, to be displayed in the progress bar
     */
    private val _progress = MutableLiveData<Int>(0)
    val progress: LiveData<Int>
        get() = _progress

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
    are all phases completed?
     */
    private var _isPhasesListCompleted = MutableLiveData<Boolean>(false)
    val isPhasesListCompleted: LiveData<Boolean>
        get() = _isPhasesListCompleted

    /*
    current first phase of the phasesList
     */
    private var _currentPhase = MutableLiveData<Phase>()
    val currentPhase: LiveData<Phase>
        get() = _currentPhase

    /*
    observe it to know when to update the statistics widget
     */
    private var _pomodoroCompleted = MutableLiveData<Boolean>(false)
    val pomodoroCompleted: LiveData<Boolean>
        get() = _pomodoroCompleted

    fun setPomodoroNotCompleted() {
        _pomodoroCompleted.value = false
    }

    /*
    creates a list of phases from the task list of the session.
    After a task pomodoro a short break is added. After 4 task pomodoros a log break is added.
     */
    fun createPhasesList(list: List<TaskExt>) {
        var longBreakCounter = 0
        for(item in list) {
            for(i in item.completedPomodoros until item.totalPomodoros) {
                phasesList.add(Phase(item.id, String.format("%s (%d / %d)", item.name, i + 1, item.totalPomodoros), pomodoroDuration))
                if(longBreakCounter == LONG_BREAK_FREQUENCY) {
                    phasesList.add(Phase(TimerService.TIMER_TYPE_LONG_BREAK.toLong(), LONG_BREAK_NAME, longBreakDuration))
                    longBreakCounter = 0
                }
                else {
                    phasesList.add(Phase(TimerService.TIMER_TYPE_SHORT_BREAK.toLong(), SHORT_BREAK_NAME, shortBreakDuration))
                    longBreakCounter++
                }
            }
        }
        /*
        remove eventual short or long break if it is the last phase
         */
        if(phasesList.isNotEmpty()) {
            if(phasesList.last().taskId != TimerService.TIMER_TYPE_SHORT_BREAK.toLong() ||
                phasesList.last().taskId != TimerService.TIMER_TYPE_LONG_BREAK.toLong())
                phasesList.removeLast()
            updateCurrentPhase()
        } else {
            _isPhasesListCompleted.value = true
        }
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

    /*
    update current phase with the new first element of phases list
     */
    private fun updateCurrentPhase() {
        _currentPhase.value = phasesList.first()
        setRemainingTime(_currentPhase.value!!.duration * TimerService.ONE_MINUTE_IN_MS.toInt())
    }

    /*
    updates remaining time to be displayed in textviews and progress bar
     */
    fun setRemainingTime(remainingTimeMillis: Int) {
        _remainingMinutes.value = remainingTimeMillis / TimerService.ONE_MINUTE_IN_MS.toInt()
        _remainingSeconds.value = (remainingTimeMillis % TimerService.ONE_MINUTE_IN_MS.toInt()) / ONE_SECOND_IN_MS
        _progress.value = remainingTimeMillis / ONE_SECOND_IN_MS
    }

    /*
    insert completed pomodoro phase into database and prepares the next phase
     */
    fun advancePhase() {
        /*
        insert pomodoro into database if it is not a short or long break
         */
        if(_currentPhase.value!!.taskId != TimerService.TIMER_TYPE_LONG_BREAK.toLong() &&
            _currentPhase.value!!.taskId != TimerService.TIMER_TYPE_SHORT_BREAK.toLong()) {
            viewModelScope.launch {
                database.insertCompletedPomodoro(CompletedPomodoro(task = _currentPhase.value!!.taskId, duration = _currentPhase.value!!.duration))
                _pomodoroCompleted.value = true
            }
        }
        phasesList.removeFirst()
        if(phasesList.isEmpty()) {
            _isPhasesListCompleted.value = true
        } else {
            updateCurrentPhase()
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
                    viewModel.advancePhase()
                }
            }
        }
    }

    companion object {
        private const val SHORT_BREAK_NAME = "Short break"
        private const val LONG_BREAK_NAME = "Long break"
        private const val LONG_BREAK_FREQUENCY = 3 //one long break each 4 pomodoros(0-3)
        private const val ONE_SECOND_IN_MS = 1000
    }
}