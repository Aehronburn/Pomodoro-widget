package it.unipd.dei.esp2023.session_details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.Session
import it.unipd.dei.esp2023.settings.SettingsViewModel

class SessionDetailsFragment : Fragment() {
    private val viewModel: SessionDetailsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(DEBUG_LOG_TAG, "Inizio onCreateView")
        val param: Long? = arguments?.getLong("sessionId")
        if(param==null){
            Log.d(DEBUG_LOG_TAG, "Lettura param: null")
        }else{
            Log.d(DEBUG_LOG_TAG, "Lettura param: $param")
        }

        require(param!=null)
        val sessionId: Long = param
        viewModel.sessionId = sessionId
        val view = inflater.inflate(R.layout.fragment_session_details, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.taskListReciclerView)
        val theAdapter = SessionDetailsRecyclerViewAdapter()
        viewModel.taskList.observe(viewLifecycleOwner){
            list ->
                Log.d(DEBUG_LOG_TAG, "trigger viewModel.taskList.observe")
                Log.d(DEBUG_LOG_TAG, "Task list count: "+list.count())
                theAdapter.setTaskList(list)
        }
        recyclerView.adapter = theAdapter


        return view
    }
    companion object {
        val DEBUG_LOG_TAG: String = "session_details_debug_tag"
    }

}