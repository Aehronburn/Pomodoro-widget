package it.unipd.dei.esp2023.sessions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R

class SessionsAdapter(private val sessionsList: Array<String>): RecyclerView.Adapter<SessionsAdapter.SessionsViewHolder>() {
    class SessionsViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        private val sessionTV:TextView = itemView.findViewById(R.id.sessionTV)

        fun bind(word:String){
            sessionTV.text = word
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.session_item, parent, false)

        return SessionsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sessionsList.size
    }

    override fun onBindViewHolder(holder: SessionsViewHolder, position: Int) {
        holder.bind(sessionsList[position])
    }
}