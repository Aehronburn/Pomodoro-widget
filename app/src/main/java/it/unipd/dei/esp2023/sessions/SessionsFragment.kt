package it.unipd.dei.esp2023.sessions

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.unipd.dei.esp2023.R
import kotlinx.coroutines.launch

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
        //TODO: Make this change live
        lifecycleScope.launch {
            val mySession = viewModel.database.getSessionFromName("Hello")
            //TODO: Pescando tramite ID ho un null pointer exception;
            //Infatti l'id che vedo effettivamente non è 0L
            // provo con una query modificata
            Log.d("Il mio adapter riceve", mySession.name)
            Log.d("Con ID", mySession.id.toString())
            recyclerView.adapter = SessionsAdapter(mySession)
        }

        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY > oldScrollY) extendedFloatingActionButton.shrink()
            else extendedFloatingActionButton.extend()
        }

        return view
    }

}