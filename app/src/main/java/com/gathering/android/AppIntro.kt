package com.gathering.android

// this class is used to create our list for the data model

data class AppIntro(var imageId:Int, var description : String )

fun main(args: Array<String>) {
    var appIntro = AppIntro (R.drawable.img2,"a platform to create events, such as parties, reunions, and gatherings")
    var appIntro2 = AppIntro (R.drawable.img1,"Bringing you family and friends together has never been so easy")
    var appIntro3 = AppIntro (R.drawable.img3,"invite guests and manage the event details")

}