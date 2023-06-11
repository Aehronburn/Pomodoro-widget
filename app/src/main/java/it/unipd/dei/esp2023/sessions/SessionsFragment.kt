package it.unipd.dei.esp2023.sessions

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.unipd.dei.esp2023.R
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import it.unipd.dei.esp2023.database.Session
import it.unipd.dei.esp2023.session_details.SessionDetailsFragment
import it.unipd.dei.esp2023.widget.SessionWidget2x2
import it.unipd.dei.esp2023.widget.updateAppWidget

class SessionsFragment : Fragment() {


    private val viewModel: SessionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sessions, container, false)

        /*
        Spawns a dialog requesting user to input data for creating a new session
         */
        val createNewSessionFAB = view.findViewById<ExtendedFloatingActionButton>(R.id.create_new_session_fab)
        createNewSessionFAB.setOnClickListener{
            CreateNewSessionDialog().show(childFragmentManager, "CreateNewSessionDialog")
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.sessions_recyclerview)

        /*
        grid column count depends on the screen size. Default 1, for screen widths >= 400dp column count is 2
         */
        recyclerView.layoutManager = StaggeredGridLayoutManager(resources.getInteger(R.integer.grid_column_count), VERTICAL)
        val adapter = SessionsAdapter(onItemClickedListener = onItemClickedListener, onItemDeletedListener = onItemDeletedListener)
        recyclerView.adapter = adapter

        /*
        shrinks the extended fab when scrolled
         */
        recyclerView.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if(scrollY > oldScrollY) createNewSessionFAB.shrink()
            else createNewSessionFAB.extend()
        }
        /*
        update list of RecyclerView everytime a session is added/deleted
         */
        viewModel.sessionList.observe(viewLifecycleOwner) {
            adapter.updateList(it)
            /*
            Notify the widget that data has changed; this triggers onDataSetChanged()
            in the Factory()
             */
            val appWidgetManager = AppWidgetManager.getInstance(requireContext())
            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetManager.getAppWidgetIds(ComponentName(requireContext(), SessionWidget2x2::class.java)),
                R.id.SessionWidget2x2ID_List)
            /*
            WARNING
            LiveData keeps a strong reference to the observer and the owner as long
             as the given LifecycleOwner is not destroyed.
             When it is destroyed, LiveData removes references
              to the observer & the owner.
             */
        }

        return view
    }

    override fun onResume() {
        /*
            It's necessary to update the widget from here and NOT erroneously from the observation
            of the LiveData. In fact when user swipes up the app and kills it, the LiveData gets
            destroyed and eventually recreated on another launch. However, it's not the same
            so Widget doesn't get updated anymore
        */
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(requireContext(), SessionWidget2x2::class.java)) ?: intArrayOf(-1)
        try {
            SessionWidget2x2.id = ids[0]
            val context = requireContext()
            updateAppWidget(context, appWidgetManager, SessionWidget2x2.id)
        }
        catch(e: ArrayIndexOutOfBoundsException){
            Toast.makeText(context, "Try my awesome Widget", Toast.LENGTH_SHORT).show()
        }

        super.onResume()
    }

    /*
    callback function called when user opens the session's details
     */
    private val onItemClickedListener: (Session) -> Unit =  { session ->
        val bundle = Bundle()
        bundle.putLong(SessionDetailsFragment.ARGUMENT_SESSION_ID, session.id)
        bundle.putString(SessionDetailsFragment.ARGUMENT_SESSION_NAME, session.name)
        findNavController().navigate(R.id.action_sessions_fragment_to_sessionDetails, bundle)
    }

    /*
    callback function called when user presses the delete button of the RecyclerView item
     */
    private val onItemDeletedListener: (Session) -> Unit = { session ->
        viewModel.deleteSession(session)
    }
}