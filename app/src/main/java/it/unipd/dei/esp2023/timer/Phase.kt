package it.unipd.dei.esp2023.timer

/*
In the timer fragment we need to interleave tasks' pomodoros with breaks.
A phase can be pomodoro, short break or long break.
Short break and long break phases are not associated with a specific task, so their taskId can be
placeholders.
 */
data class Phase(
    val taskId: Long,
    val name: String,
    var duration: Int
)
