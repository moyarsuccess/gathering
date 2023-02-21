package com.gathering.android

// this class is used to create our list for the data model

data class AppIntro(var imageId:Int, var description : String )

fun main(args: Array<String>) {
    var appIntro = AppIntro (R.drawable.description1image,"")
    var appIntro2 = AppIntro (R.drawable.description1image,"")
    var appIntro3 = AppIntro (R.drawable.description1image,"")

}