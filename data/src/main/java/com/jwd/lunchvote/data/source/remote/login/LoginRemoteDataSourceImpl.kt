package com.jwd.lunchvote.data.source.remote.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class LoginRemoteDataSourceImpl @Inject constructor(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
): LoginRemoteDataSource{
    override fun getCustomToken(accessToken: String): Flow<String?> = flow {
        val data = JSONObject()
        data.put("accessToken", accessToken)

        val res = functions.getHttpsCallable("kakaoToken")
            .call(data)
            .await()

        emit(res.data as String)

    }.flowOn(dispatcher)

    override fun signInWithCustomToken(token: String): Flow<Unit> = flow {
        auth.signInWithCustomToken(token).await()

        emit(Unit)
    }.flowOn(dispatcher)

//    override fun createUserData(): Flow<Unit> = callbackFlow {
//        val user = auth.currentUser ?: throw Exception("User is not signed in")
//
//        if (!db.getReference("users/${user.uid}").get().await().exists()){
//            db.getReference("users/${user.uid}")
//                .setValue(mapOf("nickName" to user.displayName, "email" to user.email,
//                    "profileImage" to user.photoUrl.toString()))
//                .addOnSuccessListener {
//                    Timber.e("User created")
//                    trySend(Unit)
//                }
//                .addOnFailureListener {
//                    throw it
//                }
//        } else {
//            Timber.e("User already exists")
//            trySend(Unit)
//        }
//        awaitClose()
//    }.flowOn(dispatcher)
}