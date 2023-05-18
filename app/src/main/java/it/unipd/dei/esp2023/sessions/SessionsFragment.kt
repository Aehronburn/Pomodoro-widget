package it.unipd.dei.esp2023.sessions

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.Session
import kotlinx.coroutines.launch
import java.time.LocalDate

class SessionsFragment : Fragment() {


    private val viewModel: SessionsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_sessions, container, false)

        val extendedFloatingActionButton = view.findViewById<ExtendedFloatingActionButton>(R.id.create_new_session_fab)

        extendedFloatingActionButton.setOnClickListener{
            CreateNewSessionDialog().show(parentFragmentManager, "CreateNewSessionDialog")
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_id)

        //Launch coroutine and pick data from database
        lifecycleScope.launch {
            viewModel.database.deleteAllSessions()
            //Insert some default Sessions
            lateinit var defaultSession: Session
            for(i in 65..75){
                defaultSession = Session(0L,String(charArrayOf(i.toChar())), LocalDate.now().toString())
                viewModel.database.insertSession(defaultSession)
            }

            val mySessionList = viewModel.database.getSessionList()

            //Watch out, "mySession" it's a LiveData variable: observe
            //its changes and get its value through an iterator
            mySessionList.observe(viewLifecycleOwner) {
                recyclerView.adapter = SessionsAdapter(it)
            }
        }

        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY > oldScrollY) extendedFloatingActionButton.shrink()
            else extendedFloatingActionButton.extend()
        }

        return view
    }

}