package it.unipd.dei.esp2023.sessions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.Session

class SessionsAdapter(private var sessionList: List<Session> = emptyList(),
                      private val onItemClickedListener: (Long) -> Unit,
                      private val onItemDeletedListener: (Session) -> Unit)
    : RecyclerView.Adapter<SessionsAdapter.SessionsViewHolder>() {
   inner class SessionsViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        private val sessionTV:TextView = itemView.findViewById(R.id.sessionTV)
        private val sessionDateTV:TextView = itemView.findViewById(R.id.sessionDateTV)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_session_button)

        fun bind(session: Session, onItemDeletedListener: (Session) -> Unit) {
            sessionTV.text = session.name
            sessionDateTV.text = session.creationDate
            deleteButton.setOnClickListener { onItemDeletedListener(session) }
        }
    }

    fun updateList(newList: List<Session>) {
        sessionList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.session_item, parent, false)
        return SessionsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sessionList.size
    }

    override fun onBindViewHolder(holder: SessionsViewHolder, position: Int) {
        holder.bind(sessionList[position], onItemDeletedListener)
        holder.itemView.setOnClickListener {
            onItemClickedListener(sessionList[position].id)
        }
    }
}