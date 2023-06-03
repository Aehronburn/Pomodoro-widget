package it.unipd.dei.esp2023.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.unipd.dei.esp2023.R
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.google.android.material.transition.MaterialFadeThrough
import it.unipd.dei.esp2023.database.Session
import it.unipd.dei.esp2023.session_details.SessionDetailsFragment

class SessionsFragment : Fragment() {


    private val viewModel: SessionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        The recyclerView needs to load its items first
         */
        postponeEnterTransition()
    }

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
            now the animation can start
             */
            (view.parent as ViewGroup).doOnPreDraw { startPostponedEnterTransition() }
        }

        return view
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