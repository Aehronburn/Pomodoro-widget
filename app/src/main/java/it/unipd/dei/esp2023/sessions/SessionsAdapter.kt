package it.unipd.dei.esp2023.sessions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.Session

//TODO: Change input parameter to be a List<Session> instead of a single Session object
class SessionsAdapter(private val session: Session?): RecyclerView.Adapter<SessionsAdapter.SessionsViewHolder>() {
   inner class SessionsViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        private val sessionTV:TextView = itemView.findViewById(R.id.sessionTV)
        private val sessionDateTV:TextView = itemView.findViewById(R.id.sessionDateTV)
        private val sessionCompletedPomodorosTV:TextView = itemView.findViewById(R.id.sessionCompletedPomodorosTV)

        fun bind(){
            sessionTV.text = this@SessionsAdapter.session?.name
            sessionDateTV.text = this@SessionsAdapter.session?.creationDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.session_item, parent, false)

        return SessionsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: SessionsViewHolder, position: Int) {
        holder.bind()
    }
}