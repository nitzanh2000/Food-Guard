package com.example.foodguard.data.user;
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id LIKE :id LIMIT 1")
    suspend fun getUserByUid(id: String): UserModel?

    @Query("SELECT id FROM user WHERE id IN (:ids)")
    suspend fun getExistingUserIds(ids: List<String>): List<String>

    @Upsert
    fun upsertAll(vararg users: UserModel)

    @Query("DELETE FROM user WHERE id = :id")
    fun deleteByUid(id: String)

    @Update
    fun updateUserData(user: UserModel)
}