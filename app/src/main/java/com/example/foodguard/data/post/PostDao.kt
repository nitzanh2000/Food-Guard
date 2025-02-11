package com.example.foodguard.data.post

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface PostDao {
    @Query("SELECT * FROM post")
    fun getAllPaginated(): LiveData<List<PostWithAuthor>>

    @Query("SELECT * FROM post WHERE id = :id")
    fun findById(id: String): PostModel

    @Query("SELECT * FROM post WHERE author_id = :id")
    fun findByUserId(id: String): LiveData<List<PostWithAuthor>?>

    @Upsert
    fun upsertAll(vararg review: PostModel)

    @Delete
    fun delete(review: PostModel)

    @Query("DELETE FROM post WHERE id = :id")
    fun deleteById(id: String)

    @Update
    fun update(review: PostModel)
}