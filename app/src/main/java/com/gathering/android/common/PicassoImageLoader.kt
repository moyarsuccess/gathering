package com.gathering.android.common

import android.widget.ImageView
import com.squareup.picasso.Picasso
import javax.inject.Inject

class PicassoImageLoader @Inject constructor(
    private val picasso: Picasso,
) : ImageLoader {

    override fun loadImage(imageName: String?, imageView: ImageView) {
        if (imageName.isNullOrEmpty()) return
        val imageUrl = imageName.toImageUrl()
        picasso.load(imageUrl).into(imageView)
    }
}