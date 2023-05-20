package it.unipd.dei.esp2023.session_details

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import it.unipd.dei.esp2023.MainActivity
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import it.unipd.dei.esp2023.database.Session
import it.unipd.dei.esp2023.database.Task
import it.unipd.dei.esp2023.settings.SettingsFragment


class SessionDetailsViewModel(app: Application): AndroidViewModel(app) {
    // TODO spiegare i crimini di guerra commessi dentro questo viewmodel

    private var pomodoroDuration: Int
    private val myDao: PomodoroDatabaseDao
    init{
        myDao = PomodoroDatabase.getInstance(app).databaseDao
        pomodoroDuration = app.getSharedPreferences(MainActivity::class.simpleName, Context.MODE_PRIVATE).getInt(SettingsFragment.POMODORO_DURATION, SettingsFragment.DEFAULT_POMODORO_DURATION)
    }

    var initialized: Boolean = false

    var sessionId: Long = 0
        get() { return field }
        set(id: Long) {
            if(!initialized){
                field = id
                taskList = myDao.getTaskListFromSessionId(sessionId)
                sessionInfo = myDao.getSessionFromId(sessionId)
                initProgressInfo()
                initialized = true
            }
        }
    lateinit var taskList: LiveData<List<Task>>
    lateinit var sessionInfo: LiveData<Session>

    val taskCountProgress: LiveData<Pair<Int, Int>>
        get() = _taskCountProgress
    val pomCountProgress: LiveData<Pair<Int, Int>>
        get() = _pomCountProgress
    val timeProgress: LiveData<Pair<Int, Int>>
        get() = _timeProgress

    private var _taskCountProgress: MutableLiveData<Pair<Int, Int>> = MutableLiveData<Pair<Int, Int>>()
    private var _pomCountProgress: MutableLiveData<Pair<Int, Int>> = MutableLiveData<Pair<Int, Int>>()
    private var _timeProgress: MutableLiveData<Pair<Int, Int>> = MutableLiveData<Pair<Int, Int>>()

    private var totalTasks: Int? = null
    private var completedTasks: Int? = null
    private var totalPom: Int? = null
    private var completedPom: Int? = null
    private var completedMinutes: Int? = null


    private lateinit var tt: LiveData<Int> //total tasks
    private lateinit var ct: LiveData<Int> //completed tasks
    private lateinit var tp: LiveData<Int> //total pomodoros
    private lateinit var cp: LiveData<Int> //completed pomodoros
    private lateinit var cm: LiveData<Int> //completed minutes

    // TODO posso assicurarmi che runni solo un observer alla volta e evitare che i !! facciano danni?
    private val ttObserver: Observer<Int> = Observer<Int>{
        totalTasks = it
        if(completedTasks!=null){
            _taskCountProgress.value = Pair<Int, Int>(completedTasks!!,totalTasks!!)
        }
    }
    private val ctObserver: Observer<Int> = Observer<Int>{
        completedTasks = it
        if(totalTasks!=null){
            _taskCountProgress.value = Pair<Int, Int>(completedTasks!!, totalTasks!!)
        }
    }
    private val tpObserver: Observer<Int> = Observer<Int>{
        totalPom = it
        if(completedPom!=null){
            _pomCountProgress.value = Pair<Int, Int>(completedPom!!,totalPom!!)
            updateTimeProgress()
        }
    }
    private val cpObserver: Observer<Int> = Observer<Int>{
        completedPom = it
        if(totalPom!=null){
            _pomCountProgress.value = Pair<Int, Int>(completedPom!!, totalPom!!)
            updateTimeProgress()
        }
    }
    private val cmObserver: Observer<Int> = Observer<Int>{
        completedMinutes = it
        updateTimeProgress()
    }

    // totalMinutes dipende da cm, tp e cp
    // per non replicare il codice dell'emissione del dato timeprogress faccio questa funzione
    // da chiamare ogni volta che ciascuno dei tre dati cambia
    private fun updateTimeProgress(): Unit{
        if(completedMinutes != null && totalPom != null && completedPom != null){
            val totalMinutes = completedMinutes!!+(totalPom!!-completedPom!!)*pomodoroDuration
            _timeProgress.value = Pair<Int, Int>(completedMinutes!!, totalMinutes)
        }
    }
    private fun initProgressInfo(): Unit{
        // Powered by: Ufficio complicazione affari semplici

        tt = myDao.getTotalTaskCountFromSessionId(sessionId)
        ct = myDao.getCompletedTaskCountFromSessionId(sessionId)
        tp = myDao.getTotalPomodorosCountFromSessionId(sessionId)
        cp = myDao.getCompletedPomodorosCountFromSessionId(sessionId)
        cm = myDao.getCompletedTimeFromSessionId(sessionId)

        tt.observeForever (ttObserver)
        ct.observeForever (ctObserver)
        tp.observeForever (tpObserver)
        cp.observeForever (cpObserver)
        cm.observeForever (cmObserver)
    }

    override fun onCleared() {
        super.onCleared()
        // https://kotlinlang.org/docs/whatsnew12.html#check-whether-a-lateinit-var-is-initialized
        if(tt.isInitialized && tt.hasObservers()){
            tt.removeObserver(ttObserver)
        }
        if(ct.isInitialized && ct.hasObservers()){
            ct.removeObserver(ctObserver)
        }
        if(tp.isInitialized && tp.hasObservers()){
            tp.removeObserver(tpObserver)
        }
        if(cp.isInitialized && cp.hasObservers()){
            cp.removeObserver(cpObserver)
        }
        if(cm.isInitialized && cm.hasObservers()){
            cm.removeObserver(cmObserver)
        }
    }

}