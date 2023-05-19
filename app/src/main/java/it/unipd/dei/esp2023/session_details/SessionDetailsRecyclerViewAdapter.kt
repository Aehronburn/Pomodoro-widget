package it.unipd.dei.esp2023.session_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.Task

class SessionDetailsRecyclerViewAdapter : RecyclerView.Adapter<SessionDetailsRecyclerViewAdapter.SDetailsViewHolder>() {
    private var taskList: List<Task> = emptyList<Task>()

    // Describes an item view and its place within the RecyclerView
    class SDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameSessionDetails)

        fun bind(task: Task) {
            taskNameTextView.text = task.name
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