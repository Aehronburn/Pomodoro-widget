package it.unipd.dei.esp2023.session_details

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import it.unipd.dei.esp2023.MainActivity
import it.unipd.dei.esp2023.database.*
import it.unipd.dei.esp2023.settings.SettingsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SessionDetailsViewModel(app: Application): AndroidViewModel(app) {
    /*
    * Powered by: Ufficio complicazione affari semplici
    *
    * This ViewModel is probably more complicate than it should be.
    * The process for the creation of taskList and sessionInfo observables is straightforward:
    * - the Dao is initialized in init{}
    * - when the session id is set the first time, the two observables are constructed by two Dao queries
    *   - sessionId MUST be specified before reading any ViewModel data
    *   - sessionId is set exactly once: if it is already set, its value will not change using the setter method
    * The process for session stats data is not as elegant. Data is provided to SessionDetailsFragment via three
    * observables: taskCountProgress, pomCountProgress and timeProgress (all LiveData<Pair<Int, Int>>, first
    * value is completed and second value is total).
    * Each one of these public observables is constructed manually starting from the single integers that
    * it's composed of: number of completed/total tasks for taskCountProgress, number of completed/total pomodoros
    * for pomCountProgress, number of completed minutes, completed pomodoros and total pomodoros for timeProgress.
    * Each integer is observed individually and, when there is enough data to construct a stat (eg: when both the
    * number of completed tasks and total tasks have been read at least once) the corresponding integer pair is
    * "injected" in the public observable.
    * Observation of the single integer stats is being done with observeForever, hence every observer MUST be
    * cancelled in onCleared.
    */

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
                taskList = myDao.getTaskExtListFromSessionId(sessionId)
                sessionInfo = myDao.getSessionFromId(sessionId)
                initProgressInfo()
                initialized = true
            }
        }
    lateinit var taskList: LiveData<List<TaskExt>>
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

    fun deleteTask(t: TaskExt): Unit{
        val toDelete: Task = Task(t.id, t.session, t.name, t.taskOrder, t.totalPomodoros)
        viewModelScope.launch(Dispatchers.IO){
            myDao.deleteTask(toDelete)
        }
    }
    fun newTask(name: String, totPom: Int): Unit{
        println(sessionId)
        viewModelScope.launch(Dispatchers.IO){
            myDao.insertLastTask(sessionId, name, totPom)
        }
    }
    // TODO posso assicurarmi che runni solo un observer alla volta e evitare che i !! facciano danni?
    // I !! non dovrebbero fare danni perchè una volta che viene dato un valore alle variabili non tornano più
    // a null, ma magari un minimo di semaforo non farebbe comunque male, dunno
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
        tt = myDao.getTotalTaskCountFromSessionId(sessionId)
        ct = myDao.getCompletedTaskCountFromSessionId(sessionId)
        tp = myDao.getTotalPomodorosCountFromSessionId(sessionId)
        cp = myDao.getCompletedPomodorosCountFromSessionId(sessionId)
        cm = myDao.getCompletedTimeFromSessionId(sessionId)

        tt.observeForever(ttObserver)
        ct.observeForever(ctObserver)
        tp.observeForever(tpObserver)
        cp.observeForever(cpObserver)
        cm.observeForever(cmObserver)
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