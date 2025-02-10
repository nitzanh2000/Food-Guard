package com.example.foodguard.roomDB;

import android.content.Context
import androidx.room.Room

object DBHolder {
    private var appDatabase: AppDatabase? = null

    fun initDatabase(context: Context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "food-guard-db").fallbackToDestructiveMigration()
                .build()
    }

    fun getDatabase(): AppDatabase {
        return this.appDatabase!!
    }
}