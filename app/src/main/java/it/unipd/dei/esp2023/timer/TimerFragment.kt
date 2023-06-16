package it.unipd.dei.esp2023.timer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialFade
import it.unipd.dei.esp2023.MainViewModel
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.databinding.FragmentTimerBinding
import it.unipd.dei.esp2023.service.TimerService
import it.unipd.dei.esp2023.session_details.SessionDetailsFragment
import it.unipd.dei.esp2023.settings.SettingsFragment

class TimerFragment : Fragment() {

    private val viewModel: TimerViewModel by viewModels()

    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFade()
        exitTransition = MaterialFade()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentTimerBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)

        val sessionId = requireArguments().getLong(SessionDetailsFragment.ARGUMENT_SESSION_ID)

        /*
        updates pomodoros and breaks duration from preferences
         */
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        viewModel.setPhasesDurations(
            preferences.getInt(
                SettingsFragment.POMODORO_DURATION,
                SettingsFragment.DEFAULT_POMODORO_DURATION
            ),
            preferences.getInt(
                SettingsFragment.SHORT_BREAK_DURATION,
                SettingsFragment.DEFAULT_SHORT_BREAK_DURATION
            ),
            preferences.getInt(
                SettingsFragment.LONG_BREAK_DURATION,
                SettingsFragment.DEFAULT_LONG_BREAK_DURATION
            )
        )

        /*
        every time the use goes back to the previous page and re-enters the timer fragment,
        the list of phases is re-calculated. This means that a pomodoro phase is always the first
        and if a break was due, now the it is lost and the user must complete another pomodoro before
         */
        activityViewModel.getTaskExtList(sessionId).observe(viewLifecycleOwner) {
            if (!viewModel.isInitialized) {
                viewModel.createPhasesList(it)
                viewModel.isInitialized = true
            }
        }

        /*
        set progress indicator max to max of phase duration
         */
        viewModel.currentPhase.observe(viewLifecycleOwner) {
            binding.phaseProgressIndicator.max = it.duration * 60
        }

        /*
        cannot update progress with data binding with animation, so we do it here by calling setProgress(..., animate = true)
         */
        viewModel.progress.observe(viewLifecycleOwner) {
            binding.phaseProgressIndicator.setProgress(it, true)
        }

        /*
        programmatically set progress bar colors based on current phase
         */
        viewModel.currentPhase.observe(viewLifecycleOwner) {
            val trackColor: Int?
            val indicatorColor: Int?
            when (it.taskId) {
                TimerService.TIMER_TYPE_SHORT_BREAK.toLong() -> {
                    trackColor = R.color.short_break_progress_track
                    indicatorColor = R.color.short_break_progress_indicator
                }

                TimerService.TIMER_TYPE_LONG_BREAK.toLong() -> {
                    trackColor = R.color.long_break_progress_track
                    indicatorColor = R.color.long_break_progress_indicator
                }

                else -> {
                    trackColor = R.color.pomodoro_progress_track
                    indicatorColor = R.color.pomodoro_progress_indicator
                }
            }
            binding.phaseProgressIndicator.trackColor =
                resources.getColor(trackColor, context?.theme)
            binding.phaseProgressIndicator.setIndicatorColor(
                resources.getColor(
                    indicatorColor,
                    context?.theme
                )
            )
        }

        binding.toggleStartPlayPause.setOnClickListener {
            /*
            we use the same button for either creating(and start) timer and play/pause. We distinguish using a boolean variable isStarted
             */
            if (viewModel.isStarted.value == false) {
                val timerType = when (viewModel.currentPhase.value!!.taskId) {
                    TimerService.TIMER_TYPE_SHORT_BREAK.toLong() -> TimerService.TIMER_TYPE_SHORT_BREAK
                    TimerService.TIMER_TYPE_LONG_BREAK.toLong() -> TimerService.TIMER_TYPE_LONG_BREAK
                    else -> TimerService.TIMER_TYPE_POMODORO
                }
                mService?.send(
                    Message.obtain(
                        null, TimerService.ACTION_CREATE_TIMER, timerType,
                        TimerService.ONE_MINUTE_IN_MS.toInt() * viewModel.currentPhase.value!!.duration
                    )
                )
            } else {
                val action =
                    if (viewModel.isPlaying.value == true) TimerService.ACTION_PAUSE_TIMER else TimerService.ACTION_START_TIMER
                mService?.send(Message.obtain(null, action))
            }
        }

        binding.resetButton.setOnClickListener {
            mService?.send(Message.obtain(null, TimerService.ACTION_RESET_TIMER, 0, 0))
        }

        /*
        ask for notification permission(Android 13) and bind to service
         */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PermissionChecker.PERMISSION_GRANTED
            )
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    12345
                )
        val intent = Intent(context, TimerService::class.java)
        context?.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    /*
    unsubscribe from service "mailing list" and unbind from service
     */
    override fun onDestroyView() {
        super.onDestroyView()
        mService?.send(Message.obtain(null, TimerService.ACTION_DELETE_TIMER, 0, 0))

        val unsubscribeMsg = Message.obtain(null, TimerService.ACTION_UNSUBSCRIBE, 0, 0)
        unsubscribeMsg.replyTo = viewModel.replyMessenger
        mService?.send(unsubscribeMsg)

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
            val subscribeMsg = Message.obtain(null, TimerService.ACTION_SUBSCRIBE, 0, 0)
            subscribeMsg.replyTo = viewModel.replyMessenger
            mService?.send(subscribeMsg)
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected&mdash;that is, its process crashed.
            println("ServiceConnection onServiceDisconnected")
            mService = null
            bound = false
        }
    }

}