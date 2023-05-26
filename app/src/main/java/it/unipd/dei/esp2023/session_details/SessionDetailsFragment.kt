package it.unipd.dei.esp2023.session_details

// region todo toglimi
import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import it.unipd.dei.esp2023.service.TimerService
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
// endregion
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.unipd.dei.esp2023.MainViewModel
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.TaskExt

class SessionDetailsFragment : Fragment() {
    private val sessionDetailsViewModel: SessionDetailsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val argumentSessionId: Long? = arguments?.getLong(ARGUMENT_SESSION_ID)

        require(argumentSessionId!=null)
        val sessionId: Long = argumentSessionId
        sessionDetailsViewModel.sessionId = sessionId
        val view = inflater.inflate(R.layout.fragment_session_details, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.taskListRecyclerView)
        val theAdapter = SessionDetailsRecyclerViewAdapter(onItemDeletedListener)
        recyclerView.addItemDecoration(DividerItemDecoration(context, VERTICAL))
        mainViewModel.getTaskExtList(sessionId).observe(viewLifecycleOwner){
            list ->
                theAdapter.setTaskList(list)
        }
        recyclerView.adapter = theAdapter

        // https://developer.android.com/guide/topics/resources/string-resource#formatting-strings
        val tCountTV = view.findViewById<TextView>(R.id.sessionTaskCountTV)
        sessionDetailsViewModel.taskCountProgress.observe(viewLifecycleOwner){
            tCountTV.text = getString(R.string.task_count_session_details, it.first, it.second)
        }
        val pCountTV = view.findViewById<TextView>(R.id.sessionPomodoroCountTV)
        sessionDetailsViewModel.pomCountProgress.observe(viewLifecycleOwner){
            pCountTV.text = getString(R.string.pom_count_session_details, it.first, it.second)
        }
        val timeTV = view.findViewById<TextView>(R.id.sessionTimeTV)
        sessionDetailsViewModel.timeProgress.observe(viewLifecycleOwner){
            timeTV.text = getString(R.string.time_progress_session_details, it.first, it.second)
        }

        val startSessionFAB: ExtendedFloatingActionButton = view.findViewById(R.id.start_session)
        startSessionFAB.setOnClickListener {
            //TODO implement navigation
            mService?.send(Message.obtain(null, TimerService.MSG_START_TIMER, 0, 0)) // todo toglimi
        }

        val createNewTaskFAB: FloatingActionButton = view.findViewById(R.id.create_new_task_fab)
        createNewTaskFAB.setOnClickListener {
            CreateNewTaskDialog().show(childFragmentManager, "CreateNewTaskDialog")
        }

        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY > oldScrollY) startSessionFAB.shrink()
            else startSessionFAB.extend()
        }
        // region todo toglimi
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PermissionChecker.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), 12345)
        val intent = Intent(context, TimerService::class.java)
        context?.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        // endregion
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

    // region todo toglimi
    override fun onDestroyView() {
        super.onDestroyView()
        mService?.send(Message.obtain(null, TimerService.MSG_STOP_TIMER, 0, 0))
        val intent = Intent(context, TimerService::class.java)
        context?.unbindService(mConnection)
    }

    /** Messenger for communicating with the service.  */
    private var mService: Messenger? = null

    /** Flag indicating whether we have called bind on the service.  */
    private var bound: Boolean = false

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            println("ServiceConnection onServiceConnected")
            mService = Messenger(service)
            var bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected&mdash;that is, its process crashed.
            println("ServiceConnection onServiceDisconnected")
            mService = null
            bound = false
        }
    }
    // endregion
}