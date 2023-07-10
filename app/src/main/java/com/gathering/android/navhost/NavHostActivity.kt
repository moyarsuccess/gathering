package com.gathering.android.navhost

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.gathering.android.R
import com.gathering.android.auth.verification.TokenListener
import com.gathering.android.databinding.ActNavHostBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NavHostActivity : AppCompatActivity() {

    private lateinit var binding: ActNavHostBinding

    private val activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActNavHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setupWithNavController(findNavController())
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val token = intent?.data?.getQueryParameter("token")

        findNavController().handleDeepLink(intent)

//        val activeFragment = supportFragmentManager.findFragmentByTag("fragmentTag")
//        Log.d("WTF1", token.toString())
//        Log.d("WTF1.1", activeFragment.toString())
//        if (activeFragment is TokenListener) {
//            Log.d("WTF2", token.toString())
//            activeFragment.onTokenReceived(token)
//        }
    }

    private fun findNavController(): NavController {
        return findNavController(R.id.nav_host_fragment_activity_main)
    }
}