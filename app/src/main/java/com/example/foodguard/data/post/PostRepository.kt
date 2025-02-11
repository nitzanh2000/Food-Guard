package com.example.foodguard.data.post

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.foodguard.data.user.UserRepository
import com.example.foodguard.roomDB.DBHolder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

    fun getPostById(id: String): PostModel {
        return postsDao.findById(id)
    }

    fun getPostsByUserId(id: String) : LiveData<List<PostWithAuthor>?> {
        return postsDao.findByUserId(id)
    }

    fun getPostsList(
        limit: Int,
        offset: Int,
        scope: CoroutineScope
    ): LiveData<List<PostWithAuthor>> {
        return postsDao.getAllPaginated(limit, offset)
    }


    suspend fun loadPostFromRemoteSource(id: String) = withContext(Dispatchers.IO) {
        val post =
            firestoreHandle.document(id).get().await().toObject(PostDTO::class.java)?.toPostModel()
        if (post != null) {
            postsDao.upsertAll(post)
            usersRepository.cacheUserIfNotExisting(post.author_id)
        }

        return@withContext postsDao.findById(id)
    }

    suspend fun loadPostsFromRemoteSource() =
        withContext(Dispatchers.IO) {
         
            val posts = firestoreHandle
                .get().await().toObjects(PostDTO::class.java).map { it.toPostModel() }
       
//            if (posts.isNotEmpty()) {
//                usersRepository.cacheUsersIfNotExisting(posts.map { it.author_id })
//                postsDao.upsertAll(*posts.toTypedArray())
//
//            }

            Log.d("loadPostsFromRemoteSource", "posts: $posts")
        }
}