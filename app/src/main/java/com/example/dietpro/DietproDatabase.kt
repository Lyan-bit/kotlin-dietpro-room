package com.example.dietpro

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(MealEntity::class)], version = 1, exportSchema = false)
abstract class DietproDatabase : RoomDatabase() {
    abstract fun mealDao(): MealEntityDao
}
