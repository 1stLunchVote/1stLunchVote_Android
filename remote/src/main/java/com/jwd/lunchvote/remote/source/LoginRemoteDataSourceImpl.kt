package com.jwd.lunchvote.remote.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.data.source.remote.LoginRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class LoginRemoteDataSourceImpl @Inject constructor(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
): LoginRemoteDataSource {
    override suspend fun getCustomToken(accessToken: String): String? = withContext(dispatcher){
        val data = JSONObject()
        data.put("accessToken", accessToken)

        val res = functions.getHttpsCallable("kakaoToken")
            .call(data)
            .await()

        return@withContext res.data as String?
    }

    override suspend fun signInWithCustomToken(token: String) {
        withContext(dispatcher){
            auth.signInWithCustomToken(token).await()
        }
    }
}