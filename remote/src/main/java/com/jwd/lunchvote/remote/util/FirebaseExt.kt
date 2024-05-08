package com.jwd.lunchvote.remote.util

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

inline fun <reified T> DatabaseReference.getValueEventFlow() = callbackFlow<T> {
  val listener = object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
      val value = snapshot.getValue(T::class.java)
      if (value != null) {
        trySend(value)
      }
    }

    override fun onCancelled(error: DatabaseError) {
      close(error.toException())
    }
  }

  addValueEventListener(listener)
  awaitClose { removeEventListener(listener) }
}