package com.gathering.android.profile.editProfile

import com.gathering.android.auth.model.User

interface EditeProfileNavigator {

    fun navigateToAddPic()

    fun navigateToProfile(user:User)
}
