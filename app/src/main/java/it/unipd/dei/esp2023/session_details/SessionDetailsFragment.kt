package it.unipd.dei.esp2023.session_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.TaskExt

class SessionDetailsFragment : Fragment() {
    private val viewModel: SessionDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val argumentSessionId: Long? = arguments?.getLong(ARGUMENT_SESSION_ID)

        require(argumentSessionId!=null)
        val sessionId: Long = argumentSessionId
        viewModel.sessionId = sessionId
        val view = inflater.inflate(R.layout.fragment_session_details, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.taskListRecyclerView)
        val theAdapter = SessionDetailsRecyclerViewAdapter(onItemDeletedListener)
        recyclerView.addItemDecoration(DividerItemDecoration(context, VERTICAL))
        viewModel.taskList.observe(viewLifecycleOwner){
            list ->
                theAdapter.setTaskList(list)
        }
        recyclerView.adapter = theAdapter

        // https://developer.android.com/guide/topics/resources/string-resource#formatting-strings
        val tCountTV = view.findViewById<TextView>(R.id.sessionTaskCountTV)
        viewModel.taskCountProgress.observe(viewLifecycleOwner){
            tCountTV.text = getString(R.string.task_count_session_details, it.first, it.second)
        }
        val pCountTV = view.findViewById<TextView>(R.id.sessionPomodoroCountTV)
        viewModel.pomCountProgress.observe(viewLifecycleOwner){
            pCountTV.text = getString(R.string.pom_count_session_details, it.first, it.second)
        }
        val timeTV = view.findViewById<TextView>(R.id.sessionTimeTV)
        viewModel.timeProgress.observe(viewLifecycleOwner){
            timeTV.text = getString(R.string.time_progress_session_details, it.first, it.second)
        }

        val startSessionFAB: ExtendedFloatingActionButton = view.findViewById(R.id.start_session)
        startSessionFAB.setOnClickListener {
            //TODO implement navigation
        }

        val createNewTaskFAB: FloatingActionButton = view.findViewById(R.id.create_new_task_fab)
        createNewTaskFAB.setOnClickListener {
            CreateNewTaskDialog().show(childFragmentManager, "CreateNewTaskDialog")
        }

        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY > oldScrollY) startSessionFAB.shrink()
            else startSessionFAB.extend()
        }
        return view
    }

    private val onItemDeletedListener: (TaskExt) -> Unit = {
        viewModel.deleteTask(it)
    }

    companion object {
        /*
        Parameters to pass in the bundle when navigating to this fragment
         */
        const val ARGUMENT_SESSION_ID = "sessionId"
        const val ARGUMENT_SESSION_NAME = "sessionName"
    }

}