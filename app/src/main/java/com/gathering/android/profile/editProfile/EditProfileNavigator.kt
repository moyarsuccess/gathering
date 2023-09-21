package com.gathering.android.profile.editProfile

import com.gathering.android.auth.model.User

interface EditProfileNavigator {

    fun navigateToAddPic()

    fun navigateToProfile(user:User)
}
