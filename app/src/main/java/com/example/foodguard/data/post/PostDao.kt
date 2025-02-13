package com.example.foodguard.data.post

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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


    // Insert or update (upsert) posts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(posts: List<PostModel>)

    // Delete posts not in the provided list of IDs
    @Query("DELETE FROM post WHERE id NOT IN (:postIds)")
    suspend fun deletePostsNotIn(postIds: List<String>)

    // Get all current local posts
    @Query("SELECT * FROM post")
    suspend fun getAllPosts(): List<PostModel>


}