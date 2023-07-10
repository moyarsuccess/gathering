package com.gathering.android.auth.verification

interface TokenListener {

    fun onTokenReceived(token: String?)
}