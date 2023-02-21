package com.gathering.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gathering.android.databinding.IntroActivityBinding


class IntroActivity : AppCompatActivity() {
    private lateinit var binding: IntroActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = IntroActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.viewpager
    }

}