package com.gathering.android.event.eventdetail

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class ClickableUnderlineSpan(private val listener: OnLinkClickListener) : ClickableSpan() {

    override fun onClick(widget: View) {
        listener.onLinkClick()
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = true
    }

    interface OnLinkClickListener {
        fun onLinkClick()
    }
}

