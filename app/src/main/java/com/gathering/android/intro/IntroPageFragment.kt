package com.gathering.android.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gathering.android.R

class IntroPageFragment : Fragment() {

    private var imageResource: Int = 0
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageResource = it.getInt(ARG_IMAGE_RESOURCE)
            title = it.getString(ARG_TITLE) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_intro_page, container, false)
        view.findViewById<ImageView>(R.id.imageView).setImageResource(imageResource)
        view.findViewById<TextView>(R.id.textViewTitle).text = title
        return view
    }

    data class AppIntro(var imageId: Int, var description: String)

    companion object {

        private const val ARG_IMAGE_RESOURCE = "imageResource"
        private const val ARG_TITLE = "title"

        fun newInstance(imageResource: Int, title: String) =
            IntroPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_IMAGE_RESOURCE, imageResource)
                    putString(ARG_TITLE, title)
                }
            }
    }
}
