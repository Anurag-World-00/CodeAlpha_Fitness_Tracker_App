package com.codinghits.fitnesstracker

data class Workout(
    val id: String = "",
    val type: String = "",
    val duration: Int = 0, // in minutes
    val date: Long = System.currentTimeMillis()
)




