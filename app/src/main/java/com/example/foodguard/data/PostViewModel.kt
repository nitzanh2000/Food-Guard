package com.example.foodguard.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.foodguard.data.post.PostModel
import com.example.foodguard.data.post.PostRepository
import com.example.foodguard.data.post.PostWithAuthor
import com.example.foodguard.data.user.UserModel
import com.example.foodguard.data.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class PostViewModel : ViewModel() {
    private val postRepository = PostRepository()
    private val usersRepository = UserRepository()

    fun getAllAvailablePosts(): LiveData<List<PostWithAuthor>> {
        return getAllPost().map { posts -> posts.filter { isAvailablePost(it) } }
    }

    fun getAllPost(): LiveData<List<PostWithAuthor>> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        val postsLiveData: LiveData<List<PostWithAuthor>> = this.postRepository.getPostsList()
        val sortedPostsLiveData: LiveData<List<PostWithAuthor>> = postsLiveData.map { posts ->
            posts.sortedBy { LocalDateTime.parse(it.post.expiration_date, dateFormatter) }
        }

        return sortedPostsLiveData;
    }

    fun isAvailablePost(post : PostWithAuthor): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:m", Locale.getDefault())
        val formattedExpirationDate = dateFormat.parse(post.post.expiration_date)

        var valid = true;

        try {
            valid = formattedExpirationDate.after(Date())
        }
        catch (e : Exception ){
            Log.e("Date", "Invalid date")
        }

        return valid && !post.post.is_delivered
    }

    fun getAllPostsByUserId(id: String): LiveData<List<PostWithAuthor>?> {
        return this.postRepository.getPostsByUserId(id)
    }

    suspend fun refreshPostsFromRemote() {
        postRepository.loadPostsFromRemoteSource()
    }

    fun getUserById(id: String): LiveData<UserModel?> {
        val userLiveData = MutableLiveData<UserModel?>()
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) {
                usersRepository.getUserByUid(id)
            }
            userLiveData.postValue(user)
        }

        return userLiveData
    }

    fun updateUser(
        user: UserModel,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            usersRepository.upsertUser(user)
        }
    }

    fun addPost(
        post: PostModel,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            postRepository.addPost(post)
        }
    }

    fun savePost(
        post: PostModel,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            postRepository.editPost(post)
        }
    }

    fun deletePostById(
        postId: String,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            postRepository.deletePostById(postId)
        }
    }
}