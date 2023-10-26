package com.gathering.android.common

fun String.toImageUrl(): String {
    if (this.startsWith(LOCAL_CONTENT_URL_PREFIX)) return this
    return "${BASE_URL}/${PHOTO}/$this"
}
