package com.example.dietpro

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mealTable")
data class MealEntity (
    @PrimaryKey
    val mealId: String, 
    val mealName: String, 
    val calories: Double, 
    val dates: String, 
    val images: String, 
    val analysis: String, 
    val userName: String
)
