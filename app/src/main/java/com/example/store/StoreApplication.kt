package com.example.store

import android.app.Application
import androidx.room.Room

class StoreApplication:Application() {
    companion object{
        lateinit var databse: StoreDatabase
    }

    override fun onCreate() {
        super.onCreate()
        databse = Room.databaseBuilder(this,
            StoreDatabase::class.java,
            "StoreDatabase")
            .build()
    }
}