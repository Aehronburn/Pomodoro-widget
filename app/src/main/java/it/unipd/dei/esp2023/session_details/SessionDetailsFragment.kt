package it.unipd.dei.esp2023.session_details

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFade
import it.unipd.dei.esp2023.MainViewModel
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.TaskExt
import it.unipd.dei.esp2023.sessions_widget.SessionsWidget

class SessionDetailsFragment : Fragment() {
    private val sessionDetailsViewModel: SessionDetailsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFade()
        exitTransition = MaterialFade()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val argumentSessionId: Long? = arguments?.getLong(ARGUMENT_SESSION_ID)

        require(argumentSessionId != null)
        val sessionId: Long = argumentSessionId
        sessionDetailsViewModel.sessionId = sessionId
        val view = inflater.inflate(R.layout.fragment_session_details, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.taskListRecyclerView)
        val theAdapter = SessionDetailsRecyclerViewAdapter(onItemDeletedListener)
        recyclerView.addItemDecoration(DividerItemDecoration(context, VERTICAL))
        recyclerView.adapter = theAdapter

        // https://developer.android.com/guide/topics/resources/string-resource#formatting-strings
        val tCountTV = view.findViewById<TextView>(R.id.sessionTaskCountTV)
        sessionDetailsViewModel.taskCountProgress.observe(viewLifecycleOwner) {
            tCountTV.text = getString(R.string.task_count_session_details, it.first, it.second)
        }
        val pCountTV = view.findViewById<TextView>(R.id.sessionPomodoroCountTV)
        sessionDetailsViewModel.pomCountProgress.observe(viewLifecycleOwner) {
            pCountTV.text = getString(R.string.pom_count_session_details, it.first, it.second)
        }
        val timeTV = view.findViewById<TextView>(R.id.sessionTimeTV)
        sessionDetailsViewModel.timeProgress.observe(viewLifecycleOwner) {
            timeTV.text = getString(R.string.time_progress_session_details, it.first, it.second)
        }

        val startSessionFAB: ExtendedFloatingActionButton = view.findViewById(R.id.start_session)
        startSessionFAB.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(ARGUMENT_SESSION_ID, argumentSessionId)
            findNavController().navigate(R.id.action_sessionDetails_to_timerFragment, bundle)
        }

        val createNewTaskFAB: FloatingActionButton = view.findViewById(R.id.create_new_task_fab)
        createNewTaskFAB.setOnClickListener {
            CreateNewTaskDialog().show(childFragmentManager, "CreateNewTaskDialog")
        }

        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) startSessionFAB.shrink()
            else startSessionFAB.extend()
        }

        mainViewModel.getTaskExtList(sessionId).observe(viewLifecycleOwner) { list ->
            theAdapter.setTaskList(list)
            (view.parent as ViewGroup).doOnPreDraw {
                startPostponedEnterTransition()
            }
            startSessionFAB.isEnabled = list.isNotEmpty()

            /*
            Necessario per non far piantare il widget in caso di modifica dei task
            e successivo kill dell'applicazione
            Notify the widget that data has changed; this triggers onDataSetChanged()
            in the Factory()
             */
            val myAppWidgetManager = AppWidgetManager.getInstance(requireContext())
            myAppWidgetManager.notifyAppWidgetViewDataChanged(
                myAppWidgetManager.getAppWidgetIds(
                    ComponentName(
                        requireContext(),
                        SessionsWidget::class.java
                    )
                ),
                R.id.sessions_widget_list
            )
        }

        return view
    }

    private val onItemDeletedListener: (TaskExt) -> Unit = {
        sessionDetailsViewModel.deleteTask(it)
    }

    companion object {
        /*
        Parameters to pass in the bundle when navigating to this fragment
         */
        const val ARGUMENT_SESSION_ID = "sessionId"
        const val ARGUMENT_SESSION_NAME = "sessionName"
    }

}