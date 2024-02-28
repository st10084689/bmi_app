package com.harmless.bmiproject.models

import java.util.Date

data class TrackModel(
    val weight: String,
    val bmi: Double,
    val date: Date?,
    val age: String,
    val user: String,
    val unit: String
){
    constructor(): this("",0.0,null,"","","")
}