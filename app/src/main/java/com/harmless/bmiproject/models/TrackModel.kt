package com.harmless.bmiproject.models

import java.util.Date

data class TrackModel(//model to track the bmi the user has received and store it in the cloud 
    val weight: String,
    val bmi: Double,
    val date: Date?,
    val age: String,
    val user: String,
    val unit: String
){
    constructor(): this("",0.0,null,"","","")
}
