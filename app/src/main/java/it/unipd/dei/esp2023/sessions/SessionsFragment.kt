package it.unipd.dei.esp2023.sessions

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import it.unipd.dei.esp2023.SessionsContentProvider
import it.unipd.dei.esp2023.database.Session

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

            //TODO temporary test of content provider
            val resolver: ContentResolver by lazy {
                requireContext().contentResolver
            }
            val uri = Uri.parse(SessionsContentProvider.URI)
            /*
            wrong uri
            val uri = Uri.parse("content://it.unipd.dei.esp2023.SessionsContentProvider/session")
            */
            val cursor = resolver.query(uri, null, null, null, null)
            cursor.use {
                val count = cursor!!.count
                Log.d("debug", "Conto $count")
                Log.d("debug", cursor.columnNames.component1())
                Log.d("debug", cursor.columnNames.component2())
                Log.d("debug", cursor.columnNames.component3())
                while (cursor.moveToNext()) {
                    Log.d(
                        "debug",
                        cursor.getLong(0).toString() + cursor.getString(1) + cursor.getString(2)
                    )
                }
            }
            //end
        }

        return view
    }

    /*
    callback function called when user opens the session's details
     */
    private val onItemClickedListener: (Session) -> Unit =  { session ->
        val bundle = Bundle()
        bundle.putLong("sessionId", session.id)
        findNavController().navigate(R.id.action_sessions_fragment_to_sessionDetails, bundle)
    }

    /*
    callback function called when user presses the delete button of the RecyclerView item
     */
    private val onItemDeletedListener: (Session) -> Unit = { session ->
        viewModel.deleteSession(session)
    }
}