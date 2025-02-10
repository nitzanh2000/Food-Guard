package com.example.foodguard.data.user;

import com.example.foodguard.roomDB.DBHolder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {
    private val usersDao = DBHolder.getDatabase().userDao()
    private val firestoreHandle = Firebase.firestore.collection("user")

    suspend fun insertUsers(vararg users: UserModel) = withContext(Dispatchers.IO) {
        firestoreHandle.add(users).await()
        usersDao.upsertAll(*users)
    }

    suspend fun upsertUser(user: UserModel) = withContext(Dispatchers.IO) {
        firestoreHandle.document(user.id).set(user).await()
        usersDao.upsertAll(user)
    }

    suspend fun deleteById(id: String) = withContext(Dispatchers.IO) {
        usersDao.deleteByUid(id)
    }

    suspend fun cacheUserIfNotExisting(uid: String) = withContext(Dispatchers.IO) {
        val cachedResult = usersDao.getUserByUid(uid)
        if (cachedResult == null) {
            this@UserRepository.getUserFromRemoteSource(uid)
        }
    }

    suspend fun cacheUsersIfNotExisting(uids: List<String>) = withContext(Dispatchers.IO) {
        val existingUserIds = usersDao.getExistingUserIds(uids)
        val nonExistingUserIds = uids.filter { !existingUserIds.contains(it) }
   
        this@UserRepository.getUsersFromRemoteSource(nonExistingUserIds)
    }

    suspend fun getUserByUid(uid: String): UserModel? = withContext(Dispatchers.IO) {
        val cachedResult = usersDao.getUserByUid(uid)
        if (cachedResult != null) return@withContext cachedResult

        return@withContext this@UserRepository.getUserFromRemoteSource(uid)
    }

    private suspend fun getUserFromRemoteSource(uid: String): UserModel? =
        withContext(Dispatchers.IO) {
            val user =
                firestoreHandle.document(uid).get().await().toObject(UserDTO::class.java)?.toUserModel()
            if (user != null) {
                usersDao.upsertAll(user)
            }
            return@withContext user
        }

    suspend fun getUsersFromRemoteSource(uids: List<String>): List<UserModel> =
        withContext(Dispatchers.IO) {
            val usersQuery =
                if (uids.isNotEmpty()) firestoreHandle.whereIn("id", uids) else firestoreHandle
            val users = usersQuery.get().await().toObjects(UserDTO::class.java).map {it.toUserModel()}
            if (users.isNotEmpty()) {
                usersDao.upsertAll(*users.toTypedArray())
            }
            return@withContext users
        }
}