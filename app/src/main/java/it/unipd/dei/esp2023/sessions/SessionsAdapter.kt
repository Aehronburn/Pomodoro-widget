package it.unipd.dei.esp2023.sessions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.Session

class SessionsAdapter(private val sessionList: List<Session>?): RecyclerView.Adapter<SessionsAdapter.SessionsViewHolder>() {
   inner class SessionsViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        private val sessionTV:TextView = itemView.findViewById(R.id.sessionTV)
        private val sessionDateTV:TextView = itemView.findViewById(R.id.sessionDateTV)
        private val sessionCompletedPomodorosTV:TextView = itemView.findViewById(R.id.sessionCompletedPomodorosTV)

        fun bind(position: Int){
            try {
                sessionTV.text = this@SessionsAdapter.sessionList?.get(position)?.name
                sessionDateTV.text = this@SessionsAdapter.sessionList?.get(position)?.creationDate
            }
            catch(e: IndexOutOfBoundsException){

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.session_item, parent, false)

        return SessionsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sessionList?.size!!
    }

    override fun onBindViewHolder(holder: SessionsViewHolder, position: Int) {
        holder.bind(position)
    }
}