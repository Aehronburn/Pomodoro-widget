package it.unipd.dei.esp2023.session_details

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.TaskExt

class SessionDetailsRecyclerViewAdapter(private val onItemDeletedListener: (TaskExt) -> Unit) : RecyclerView.Adapter<SessionDetailsRecyclerViewAdapter.SDetailsViewHolder>() {
    private var taskList: List<TaskExt> = emptyList<TaskExt>()

    // https://stackoverflow.com/a/46376182
    inner class SDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameSessionDetails)
        private val pomCountTextView: TextView = itemView.findViewById(R.id.taskPomodoroCountSessionDetails)
        private val deleteButton: TextView = itemView.findViewById(R.id.deleteTaskBtnSessionDetails)

        fun bind(task: TaskExt) {
            taskNameTextView.text = task.name
            pomCountTextView.text = "${task.totalPomodoros} ${if(task.totalPomodoros==1) itemView.context.resources.getString(R.string.singular_pomodoro) else itemView.context.resources.getString(R.string.plural_pomodoro)}"
            if(task.completedPomodoros>=task.totalPomodoros){
                taskNameTextView.paintFlags = taskNameTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }else{
                taskNameTextView.paintFlags = taskNameTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            deleteButton.setOnClickListener {
                onItemDeletedListener(task)
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
    fun setTaskList(theList: List<TaskExt>){
        val diffCallback = TaskDiffUtil(taskList, theList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        taskList = theList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class TaskDiffUtil(private val oldList: List<TaskExt>, private val newList: List<TaskExt>): DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }
}