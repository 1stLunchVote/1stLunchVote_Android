package com.jwd.lunchvote.remote.util

import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

internal inline fun <reified T> DatabaseReference.getValueEventFlow() = callbackFlow {
  val listener = object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
      val value = snapshot.children.associate { it.key!! to it.getValue(T::class.java) }
      trySend(value)
    }

    override fun onCancelled(error: DatabaseError) {
      close(error.toException())
    }
  }

  addValueEventListener(listener)
  awaitClose { removeEventListener(listener) }
}

internal fun CollectionReference.whereNotDeleted() =
  this.whereEqualTo("deletedAt", null)

internal fun DocumentReference.deleteDocument() =
  this.update("deletedAt", Timestamp.now())

internal fun DatabaseReference.deleteChild() =
  this.updateChildren(mapOf("deletedAt" to System.currentTimeMillis()))