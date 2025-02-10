package com.example.foodguard

import android.app.Application
import com.example.foodguard.roomDB.DBHolder

class ApplicationStarter: Application() {
    override fun onCreate() {
        DBHolder.initDatabase(this)
        super.onCreate()
    }
}