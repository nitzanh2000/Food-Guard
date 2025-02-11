package com.example.foodguard.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodguard.data.user.UserModel
import com.example.foodguard.data.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val usersRepository = UserRepository()

    fun register(onFinishUi: () -> Unit, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            usersRepository.upsertUser(UserModel.fromFirebaseAuth(context))
            onFinishUi()
        }
    }
}