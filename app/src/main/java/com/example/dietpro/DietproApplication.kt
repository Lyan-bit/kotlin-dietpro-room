package com.example.dietpro

import android.app.Application
import androidx.room.Room

class DietproApplication : Application() {

    companion object {
        lateinit var database: DietproDatabase
            private set
    }
    override fun onCreate() {
        super.onCreate()
        database = Room
            .databaseBuilder(
                this,
                DietproDatabase::class.java,
                "dietproDatabase"
            )
            .build() }
}
