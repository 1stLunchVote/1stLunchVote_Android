package com.jwd.lunchvote.data.source.remote.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class LoginRemoteDataSourceImpl @Inject constructor(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
): LoginRemoteDataSource{
    override fun getCustomToken(accessToken: String): Flow<String?> = callbackFlow {
        val data = JSONObject()
        data.put("accessToken", accessToken)
        functions.getHttpsCallable("kakaoToken")
            .call(data)
            .addOnSuccessListener {
                trySend(it.data as String)
            }
            .addOnFailureListener {
                trySend(null)
            }

        awaitClose()
    }.flowOn(dispatcher)

    override fun signInWithCustomToken(token: String): Flow<Unit> = callbackFlow {
        auth.signInWithCustomToken(token)
            .addOnSuccessListener {
                trySend(Unit)
            }
            .addOnFailureListener {
                throw it
            }

        awaitClose()
    }.flowOn(dispatcher)

    override fun createUserData(): Flow<Unit> = callbackFlow {
        val user = auth.currentUser ?: throw Exception("User is not signed in")

        if (!db.getReference("users/${user.uid}").get().await().exists()){
            db.getReference("users/${user.uid}")
                .setValue(mapOf("nickName" to user.displayName, "email" to user.email,
                    "profileImage" to user.photoUrl.toString()))
                .addOnSuccessListener {
                    Timber.e("User created")
                    trySend(Unit)
                }
                .addOnFailureListener {
                    throw it
                }
        } else {
            Timber.e("User already exists")
            trySend(Unit)
        }
        awaitClose()
    }.flowOn(dispatcher)
}