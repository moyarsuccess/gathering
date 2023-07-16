package com.gathering.android.common

import android.widget.ImageView
import com.squareup.picasso.Picasso
import javax.inject.Inject

class PicassoImageLoader @Inject constructor(
    private val baseurl: String
) : ImageLoader {

    override fun loadImage(imageName: String?, imageView: ImageView) {

        if (imageName.isNullOrEmpty()) return

        Picasso.get().load(imageName.toImageUrl()).into(imageView)
    }

    private fun String.toImageUrl(): String {
        return "$baseurl/photo/$this"
    }
}