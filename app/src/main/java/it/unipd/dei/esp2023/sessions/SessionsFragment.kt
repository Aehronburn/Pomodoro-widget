package it.unipd.dei.esp2023.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.unipd.dei.esp2023.R
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import it.unipd.dei.esp2023.database.Session

class SessionsFragment : Fragment() {


    private val viewModel: SessionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_sessions, container, false)

        val createNewSessionFAB = view.findViewById<ExtendedFloatingActionButton>(R.id.create_new_session_fab)
        createNewSessionFAB.setOnClickListener{
            CreateNewSessionDialog().show(parentFragmentManager, "CreateNewSessionDialog")
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.sessions_recyclerview)
        recyclerView.layoutManager = StaggeredGridLayoutManager(resources.getInteger(R.integer.grid_column_count), VERTICAL)
        val adapter = SessionsAdapter(onItemClickedListener = onItemClickedListener, onItemDeletedListener = onItemDeletedListener)
        recyclerView.adapter = adapter

        recyclerView.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if(scrollY > oldScrollY) createNewSessionFAB.shrink()
            else createNewSessionFAB.extend()
        }

        viewModel.sessionList.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }

        return view
    }

    private val onItemClickedListener: (Session) -> Unit =  { session ->
        val bundle = Bundle()
        bundle.putLong("sessionId", session.id)
        findNavController().navigate(R.id.action_sessions_fragment_to_sessionDetails, bundle)
    }

    private val onItemDeletedListener: (Session) -> Unit = { session ->
        viewModel.deleteSession(session)
    }
}