package com.gathering.android.navhost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.gathering.android.R
import com.gathering.android.databinding.ActNavHostBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NavHostActivity : AppCompatActivity() {

    private lateinit var binding: ActNavHostBinding

    @Inject
    lateinit var viewModel: NavHostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActNavHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setupWithNavController(findNavController())

        viewModel.viewState.observe(this) { state ->
            when (state) {
                NavHostViewState.NavigateToIntroScreen -> findNavController().navigate(R.id.action_homeFragment_to_introFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onScreenResumed()
    }

    private fun findNavController(): NavController {
        return findNavController(R.id.nav_host_fragment_activity_main)
    }
}