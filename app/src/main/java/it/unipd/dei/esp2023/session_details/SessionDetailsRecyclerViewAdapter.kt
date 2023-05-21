package it.unipd.dei.esp2023.session_details

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.Session
import it.unipd.dei.esp2023.database.Task

class SessionDetailsRecyclerViewAdapter(protected val viewModel: SessionDetailsViewModel) : RecyclerView.Adapter<SessionDetailsRecyclerViewAdapter.SDetailsViewHolder>() {
    private var taskList: List<Task> = emptyList<Task>()

    // https://stackoverflow.com/a/46376182
    inner class SDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameSessionDetails)
        private val pomCountTextView: TextView = itemView.findViewById(R.id.taskPomodoroCountSessionDetails)
        private val deleteButton: TextView = itemView.findViewById(R.id.deleteTaskBtnSessionDetails)

        fun bind(task: Task) {
            taskNameTextView.text = task.name
            pomCountTextView.text = "${task.totalPomodoros} ${if(task.totalPomodoros==1) itemView.context.resources.getString(R.string.singular_pomodoro) else itemView.context.resources.getString(R.string.plural_pomodoro)}"
            if(true){
                taskNameTextView.paintFlags = taskNameTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            deleteButton.setOnClickListener {
                viewModel.deleteTask(task)
            }
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_details_task_item, parent, false)

        return SDetailsViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return taskList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: SDetailsViewHolder, position: Int) {
        holder.bind(taskList[position])
    }
    fun setTaskList(theList: List<Task>){
        taskList = theList
        this.notifyDataSetChanged()
    }
}