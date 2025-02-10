package com.example.foodguard.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodguard.data.post.PostDao
import com.example.foodguard.data.post.PostModel
import com.example.foodguard.data.user.UserModel
import com.example.foodguard.data.user.UserDao

@Database(
    entities = [PostModel::class, UserModel::class], version = 2, exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDad(): PostDao
}