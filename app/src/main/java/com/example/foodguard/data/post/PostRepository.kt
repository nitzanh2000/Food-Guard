package com.example.foodguard.data.post

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.foodguard.data.user.UserRepository
import com.example.foodguard.roomDB.DBHolder
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostRepository() {
    private val postsDao = DBHolder.getDatabase().postDad()
    private val usersRepository = UserRepository()
    private val firestoreHandle = Firebase.firestore.collection("post")

    suspend fun addPost(vararg posts: PostModel) = withContext(Dispatchers.IO) {
        val batchHandle = Firebase.firestore.batch()
        posts.forEach {
            batchHandle.set(firestoreHandle.document(it.id), it)
        }

        batchHandle.commit().await()
        postsDao.upsertAll(*posts)
    }

    suspend fun editPost(post: PostModel) = withContext(Dispatchers.IO) {
        firestoreHandle.document(post.id).set(post).await()
        postsDao.update(post)
    }

    suspend fun deletePostById(id: String) = withContext(Dispatchers.IO) {
        firestoreHandle.document(id).delete().await()
        postsDao.deleteById(id)
    }

    fun getPostsByUserId(id: String) : LiveData<List<PostWithAuthor>?> {
        return postsDao.findByUserId(id)
    }

    fun getPostsList(): LiveData<List<PostWithAuthor>> {
        return postsDao.getAllPaginated()
    }

    suspend fun syncPosts(remotePosts: List<PostModel>) {
        // Extract IDs of posts from the remote source
        val remotePostIds = remotePosts.map { it.id }
        // Insert or update remote posts into the local database
        postsDao.insertOrUpdate(remotePosts)

        // Delete local posts that are not in the remote source
        postsDao.deletePostsNotIn(remotePostIds)
    }


    suspend fun loadPostsFromRemoteSource() =
        withContext(Dispatchers.IO) {
            val posts = firestoreHandle
                .get().await().toObjects(PostDTO::class.java).map { it.toPostModel() }

            Log.d("loadPostsFromRemoteSource", "posts: $posts")
            usersRepository.cacheUsersIfNotExisting(posts.map { it.author_id })
            syncPosts(posts)

        }
}