package com.gathering.android.navhost

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.gathering.android.R
import com.gathering.android.databinding.ActNavHostBinding
import com.gathering.android.event.KEY_ARGUMENT_EVENT_ID
import com.gathering.android.notif.FCMNotificationService.Companion.KEY_EVENT_ID
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavHostActivity : AppCompatActivity(), RequestCallback {

    private lateinit var binding: ActNavHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActNavHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionX.init(this)
                .permissions(Manifest.permission.POST_NOTIFICATIONS)
                .request(this)
        }
        intent?.getEventId()?.let { eventId ->
            val bundle = bundleOf(KEY_ARGUMENT_EVENT_ID to eventId)
            findNavController().navigate(
                R.id.action_navigation_home_to_EventDetailScreen,
                bundle
            )
        }
        binding.navView.setupWithNavController(findNavController())
    }

    private fun Intent.getEventId(): Long? {
        val eventId = getLongExtra(KEY_EVENT_ID, -1)
        if (eventId == -1L) return null
        return eventId
    }

    private fun findNavController(): NavController {
        return findNavController(R.id.nav_host_fragment_activity_main)
    }

    override fun onResult(
        allGranted: Boolean,
        grantedList: MutableList<String>,
        deniedList: MutableList<String>
    ) {
        Log.d(TAG, "Notification permission granting status: $allGranted")
    }

    companion object {
        private const val TAG = "WTF: NavHostActivity"
    }
}