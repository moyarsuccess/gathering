package com.gathering.android.common

import android.widget.ImageView

interface ImageLoader {
    fun loadImage(imageName: String?, imageView: ImageView?)
}
