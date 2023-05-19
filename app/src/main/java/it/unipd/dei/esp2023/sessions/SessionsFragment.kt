package it.unipd.dei.esp2023.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.unipd.dei.esp2023.MainViewModel
import it.unipd.dei.esp2023.R

// Aggiunti per prova apertura session details
// TODO toglimi
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.Session
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import it.unipd.dei.esp2023.session_details.SessionDetailsFragment
import kotlinx.coroutines.launch
import android.util.Log
import it.unipd.dei.esp2023.database.Task

class SessionsFragment : Fragment() {


    private val viewModel: MainViewModel by activityViewModels()

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

        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY > oldScrollY) extendedFloatingActionButton.shrink()
            else extendedFloatingActionButton.extend()
        }


        // TODO toglimi
        var listaLiveData: LiveData<List<Session>> = PomodoroDatabase.getInstance(requireContext()).databaseDao.getSessionList()
        lifecycleScope.launch {
            PomodoroDatabase.getInstance(requireContext()).databaseDao.insertSession(Session(0, "NomeSessione"))
        }
        listaLiveData.observe(viewLifecycleOwner){
            lista->
                Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, "Conteggio lista sessions: "+lista.count().toString())
                if(lista.count()>0){
                    val navController: NavController = Navigation.findNavController(view)
                    val bundle = Bundle()
                    Log.d(SessionDetailsFragment.DEBUG_LOG_TAG, "Id sessione: "+lista.last().id.toString())
                    lifecycleScope.launch {
                        PomodoroDatabase.getInstance(requireContext()).databaseDao.insertTask(
                            Task(0, lista.last().id, "Task 1 ultima sessione", 1, 1)
                        )
                        PomodoroDatabase.getInstance(requireContext()).databaseDao.insertTask(
                            Task(0, lista.last().id, "Task 2 ultima sessione", 2, 2)
                        )
                        PomodoroDatabase.getInstance(requireContext()).databaseDao.insertTask(
                            Task(0, lista.last().id, "Task 3 ultima sessione", 3, 3)
                        )
                    }
                    bundle.putLong("sessionId", lista.last().id)
                    navController.navigate(R.id.action_sessions_fragment_to_sessionDetails, bundle);
                }
        }


        return view
    }

}