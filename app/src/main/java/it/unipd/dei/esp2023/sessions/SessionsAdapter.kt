package it.unipd.dei.esp2023.sessions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.Session

class SessionsAdapter(private var sessionList: List<Session> = emptyList()): RecyclerView.Adapter<SessionsAdapter.SessionsViewHolder>() {
   inner class SessionsViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        private val sessionTV:TextView = itemView.findViewById(R.id.sessionTV)
        private val sessionDateTV:TextView = itemView.findViewById(R.id.sessionDateTV)
        private val sessionCompletedPomodorosTV:TextView = itemView.findViewById(R.id.sessionCompletedPomodorosTV)

        fun bind(session: Session) {
            sessionTV.text = session.name
            sessionDateTV.text = session.creationDate
        }
    }

    fun updateList(newList: List<Session>) {
        sessionList = newList
        //TODO to be changed to a more efficient method
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
        holder.bind(sessionList[position])
        holder.itemView.setOnClickListener { view ->
            val bundle = Bundle()
            bundle.putLong("sessionId", sessionList[position].id)
            view.findNavController().navigate(R.id.action_sessions_fragment_to_sessionDetails, bundle)
        }
    }
}