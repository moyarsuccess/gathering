package com.gathering.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gathering.android.databinding.ActNaveHostBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavHostActivity : AppCompatActivity() {

    private lateinit var binding: ActNaveHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActNaveHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}