package it.unipd.dei.esp2023.sessions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.Session

class SessionsAdapter(
    private var sessionList: List<Session> = emptyList(),
    private val onItemClickedListener: (Session) -> Unit,
    private val onItemDeletedListener: (Session) -> Unit
) : RecyclerView.Adapter<SessionsAdapter.SessionsViewHolder>() {
    inner class SessionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sessionTV: TextView = itemView.findViewById(R.id.sessionTV)
        private val sessionDateTV: TextView = itemView.findViewById(R.id.sessionDateTV)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_session_button)
        private val seeTasksButton: Button = itemView.findViewById(R.id.see_tasks_button)

        fun bind(
            session: Session
        ) {
            sessionTV.text = session.name
            sessionDateTV.text = session.creationDate
            deleteButton.setOnClickListener { onItemDeletedListener(session) }
            seeTasksButton.setOnClickListener { onItemClickedListener(session) }
        }
    }

    fun updateList(newList: List<Session>) {
        /*
        first the result of difference between current list and newList is calculated efficiently, then the difference is updated
         */
        val diffCallback = SessionDiffUtil(sessionList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        sessionList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.session_item, parent, false)
        return SessionsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sessionList.size
    }

    override fun onBindViewHolder(holder: SessionsViewHolder, position: Int) {
        holder.bind(sessionList[position])
    }

    /*
    Since notifying efficiently insertion of items and deletions of items at random positions can be tricky, instead we use a DiffUtil
     */
    inner class SessionDiffUtil(
        private val oldList: List<Session>,
        private val newList: List<Session>
    ) : DiffUtil.Callback() {
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