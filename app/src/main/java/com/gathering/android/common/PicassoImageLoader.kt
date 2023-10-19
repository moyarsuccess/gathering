package com.gathering.android.common

import android.widget.ImageView
import com.squareup.picasso.Picasso
import javax.inject.Inject

class PicassoImageLoader @Inject constructor(
    private val baseurl: String,
    private val picasso: Picasso,
) : ImageLoader {

    override fun loadImage(imageName: String?, imageView: ImageView) {
        if (imageName.isNullOrEmpty()) return
        val imageUrl = imageName.toImageUrl()
        picasso.load(imageUrl).into(imageView)
    }

    private fun String.toImageUrl(): String {
        if (this.startsWith(PREFIX)) return this
        return "$baseurl/$PHOTO/$this"
    }

    companion object {
        const val PREFIX = "content:"
        const val PHOTO = "photo"
    }
}