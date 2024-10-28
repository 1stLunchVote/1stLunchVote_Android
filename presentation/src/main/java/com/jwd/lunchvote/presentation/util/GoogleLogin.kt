package com.jwd.lunchvote.presentation.util

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.jwd.lunchvote.presentation.BuildConfig
import timber.log.Timber
import java.security.MessageDigest
import java.util.UUID

internal suspend fun loginWithGoogleCredential(context: Context): GoogleIdTokenCredential {
  val credentialManager = CredentialManager.create(context)

  val googleIdOption = GetGoogleIdOption.Builder()
    .setFilterByAuthorizedAccounts(true)
    .setServerClientId(BuildConfig.FIREBASE_WEB_CLIENT_ID)
    .setAutoSelectEnabled(true)
    .setNonce(getNonce())
    .build()

  val credentialRequest = GetCredentialRequest.Builder()
    .addCredentialOption(googleIdOption)
    .build()

  Timber.d("💙 ===ktw=== ${credentialRequest}")

  val result = credentialManager.getCredential(context, credentialRequest)

  Timber.d("💙 ===ktw=== ${result}")
  val credential = result.credential
  
  Timber.d("💙 ===ktw=== ${credential}")

  if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
    return GoogleIdTokenCredential.createFrom(credential.data)
  } else {
    throw RuntimeException("Received an invalid credential type")
  }
}

private fun getNonce(): String {
  val ranNonce: String = UUID.randomUUID().toString()
  val bytes: ByteArray = ranNonce.toByteArray()
  val md: MessageDigest = MessageDigest.getInstance("SHA-256")
  val digest: ByteArray = md.digest(bytes)
  return digest.fold("") { str, it -> str + "%02x".format(it) }
}