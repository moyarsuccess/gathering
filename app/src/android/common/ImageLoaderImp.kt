package com.gathering.android.common

import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageLoaderImp : ImageLoader {

    override fun loadImage(imageUrl: String, imageView: ImageView) {
        if (imageUrl.isEmpty()) return
        Picasso.get().load(imageUrl).into(imageView)
    }
}