package it.unipd.dei.esp2023.sessions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.Session

//TODO: Change input parameter to be a SessionList instead of a single Session object
class SessionsAdapter(private val session: Session?): RecyclerView.Adapter<SessionsAdapter.SessionsViewHolder>() {
   inner class SessionsViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        private val sessionTV:TextView = itemView.findViewById(R.id.sessionTV)

        fun bind(word:String){
            sessionTV.text = this@SessionsAdapter.session?.name
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
        holder.bind("foo")
    }
}