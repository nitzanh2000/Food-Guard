package com.example.foodguard.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodguard.data.post.PostModel
import com.example.foodguard.data.post.PostRepository
import com.example.foodguard.data.post.PostWithAuthor
import com.example.foodguard.data.user.UserModel
import com.example.foodguard.data.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel : ViewModel() {
    private val postRepository = PostRepository()
    private val usersRepository = UserRepository()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.loadPostsFromRemoteSource()
        }
    }

    fun getAllPosts(): LiveData<List<PostWithAuthor>> {
        return this.postRepository.getPostsList()
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