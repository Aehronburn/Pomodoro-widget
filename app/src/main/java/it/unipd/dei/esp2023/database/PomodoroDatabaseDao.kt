package it.unipd.dei.esp2023.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PomodoroDatabaseDao {

    // https://stackoverflow.com/questions/44915669/is-there-syntax-just-like-region-endregion-in-kotlin
    // region insert

    @Insert
    suspend fun insertSession(session: Session)
    @Insert
    suspend fun insertTask(task: Task)
    @Insert
    suspend fun insertCompletedPomodoro(completedPomodoro: CompletedPomodoro)

    // endregion

    // region delete

    @Query("DELETE FROM Session")
    suspend fun deleteAllSessions()
    @Delete
    suspend fun deleteSession(session: Session)
    @Query("DELETE FROM Session WHERE id = :sessionId")
    suspend fun deleteSessionFromId(sessionId: Long)
    @Delete
    suspend fun deleteTask(task: Task)
    @Query("DELETE FROM Task WHERE id = :taskId")
    suspend fun deleteTaskFromId(taskId: Long)
    @Delete
    suspend fun deleteCompletedPomodoro(pomodoro: CompletedPomodoro)
    @Query("DELETE FROM completed_pomodoro WHERE id = :pomodoroId")
    suspend fun deleteCompletedPomodoroFromId(pomodoroId: Long)

    // endregion

    // region update

    @Update
    suspend fun updateSession(session: Session)
    @Update
    suspend fun updateTask(task: Task)
    @Update
    suspend fun updateCompletedPomodoro(completedPomodoro: CompletedPomodoro)

    // endregion

    // region read
    @Query("SELECT * FROM session WHERE id = :sessionId")
    fun getSessionFromId(sessionId: Long): LiveData<Session>
    @Query("SELECT * FROM task WHERE id = :taskId")
    fun getTaskFromId(taskId: Long): LiveData<Task>
    @Query("SELECT * FROM completed_pomodoro WHERE id = :pomodoroId")
    fun getCompletedPomodoroFromId(pomodoroId: Long): LiveData<CompletedPomodoro>
    @Query("SELECT * FROM session")
    fun getSessionListNoLive(): List<Session>
    @Query("SELECT * FROM session ORDER BY creation_date DESC")
    fun getSessionList(): LiveData<List<Session>>
    @Query("SELECT * FROM session ORDER BY creation_date ASC")
    fun getSessionListASC(): LiveData<List<Session>>
    @Query("SELECT * FROM task WHERE session = :sessionId ORDER BY task_order, id ASC")
    fun getTaskListFromSessionId(sessionId: Long): LiveData<List<Task>>
    @Query("SELECT count(*) FROM task WHERE session = :sessionId")
    fun getTotalTaskCountFromSessionId(sessionId: Long): LiveData<Int>
    @Query("""
        SELECT count(idtask)
        FROM session LEFT JOIN (
            SELECT S.id as idsessione, T.id as idtask
            FROM session S JOIN task T on S.id = T.session join completed_pomodoro C on T.id = C.task 
            GROUP BY T.id
            HAVING count(*) >= T.total_pomodoros
        ) AS COMPL ON id = idsessione
        WHERE id = :sessionId
    """)
    fun getCompletedTaskCountFromSessionId(sessionId: Long): LiveData<Int>
    @Query("SELECT * FROM completed_pomodoro WHERE task = :taskId")
    fun getCompletedPomodoroListFromTaskId(taskId: Long): LiveData<List<CompletedPomodoro>>
    @Query("SELECT count(*) FROM completed_pomodoro WHERE task = :taskId")
    fun getCompletedPomodorosCountFromTaskId(taskId: Long): LiveData<Int>
    @Query("SELECT count(*) as completed from completed_pomodoro WHERE completion_date = :dateStr")
    fun getCompletedNumberFromDate(dateStr: String): LiveData<Int>
    @Query("SELECT sum(duration) as focus_time from completed_pomodoro WHERE completion_date = :dateStr")
    fun getCompletedTimeFromDate(dateStr: String): LiveData<Int>
    fun getTodayCompletedNumber(): LiveData<Int> {
        return getCompletedNumberFromDate("date()")
    }
    fun getTodayCompletedTime(): LiveData<Int> {
        return getCompletedTimeFromDate("date()")
    }
    @Query("""
        SELECT 0 as dayNumber, :description as dayDescription, count(*) as numCompleted, sum(duration) as focusTime 
        FROM completed_pomodoro WHERE completion_date= :dateStr
        """)
    fun getSingleDayStatisticsFromDate(dateStr: String, description: String = "Today"): LiveData<SingleStat>
    @Query("""
        SELECT 0 as dayNumber, 'Today' as dayDescription, count(*) as numCompleted, sum(duration) as focusTime 
        FROM completed_pomodoro WHERE completion_date= :dateStr
        """)
    fun getTodayStatistics(): LiveData<SingleStat> {
        return getSingleDayStatisticsFromDate("date()")
    }
    @Query("""
        WITH month(day_number) AS (
            VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12),(13),(14),(15),(16),(17),(18),(19),(20),(21),(22),(23),(24),(25),(26),(27),(28),(29),(30),(31)
        ) SELECT month.day_number as dayNumber, month.day_number as dayDescription, count(C.id) as numCompleted, coalesce(sum(duration), 0) as focusTime
        FROM month LEFT JOIN completed_pomodoro C ON C.completion_date = date('now', 'start of month', '+' || (day_number-1) || ' days')
        where month.day_number <= julianday('now', 'start of month', '+1 month') - julianday('now', 'start of month')
        GROUP BY month.day_number;
    """)
    fun getCurrentMonthStatistics(): LiveData<List<SingleStat>>


    // https://stackoverflow.com/a/47730858
    // https://stackoverflow.com/questions/21939918/make-case-when-clause-emit-a-tuple
    // NB con firstDayMonday = true dayNumber va da 1 a 7, altrimenti da 0 a 6
    @Query("""
        WITH week(display_order, number, name) AS(
            VALUES (1, 1,'Mon'),
                (2, 2,'Tue'),
                (3, 3,'Wed'),
                (4, 4,'Thu'),
                (5, 5,'Fri'),
                (6, 6,'Sat'),
                (CASE WHEN :firstDayMonday=0 THEN 0 ELSE 7 END, 0, 'Sun')
        )
        SELECT week.display_order as dayNumber, 
            week.name as dayDescription, count(C.id) as numCompleted, 
            coalesce(sum(duration), 0) as focusTime
        FROM week LEFT JOIN completed_pomodoro C ON C.completion_date = 
            CASE WHEN :firstDayMonday = 0 THEN 
                date('now', '-6 days', 'weekday '||week.number)
            ELSE 
                date('now', '-7 days', 'weekday 0', '+1 days', 'weekday '|| week.number ) 
            END
        GROUP BY week.number
        ORDER BY week.display_order
    """)
    fun getCurrentWeekStatistics(firstDayMonday: Boolean = true): LiveData<List<SingleStat>>
    @Query("SELECT COALESCE ((SELECT sum(total_pomodoros) FROM task WHERE session = :sessionId),0)") // https://stackoverflow.com/questions/4866162/get-0-value-from-a-count-with-no-rows
    fun getTotalPomodorosCountFromSessionId(sessionId: Long): LiveData<Int>
    @Query("SELECT COALESCE ((SELECT count(*) FROM task T JOIN completed_pomodoro C ON T.id = C.task WHERE session = :sessionId),0)")
    fun getCompletedPomodorosCountFromSessionId(sessionId: Long): LiveData<Int>
    @Query("SELECT COALESCE ((SELECT sum(duration) FROM task T JOIN completed_pomodoro C ON T.id = C.task WHERE session = :sessionId),0)")
    fun getCompletedTimeFromSessionId(sessionId: Long): LiveData<Int>
    @Query("""
        SELECT B.id as id, B.session as session, B.name as name, B.task_order as taskOrder, B.total_pomodoros as totalPomodoros, A.pomCount as completedPomodoros 
        FROM (SELECT T.id AS id, count(C.id) AS pomCount FROM task AS T LEFT JOIN completed_pomodoro as C on T.id = C.task GROUP BY T.id) AS A JOIN 
        (SELECT * FROM task WHERE session = :sessionId) AS B on A.id = B.id
        ORDER BY B.task_order ASC, B.id ASC
        """)
    fun getTaskExtListFromSessionId(sessionId: Long): LiveData<List<TaskExt>> // si veda TaskExt.kt
    @Query("SELECT COALESCE(MAX(task_order),-1) FROM task WHERE session = :sessionId")
    fun getMaxTaskOrderFromSessionId(sessionId: Long): Int
    // endregion
    @Transaction
    suspend fun insertLastTask(sessionId: Long, taskName: String, pomCount: Int){
        val newOrder: Int = getMaxTaskOrderFromSessionId(sessionId) + 1
        insertTask(Task(0, sessionId, taskName, newOrder, pomCount))
    }
}

