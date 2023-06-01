package com.jwd.lunchvote.data.source.remote.lounge

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.data.util.createRandomString
import com.jwd.lunchvote.domain.entity.Member
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class LoungeRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
) : LoungeRemoteDataSource {

    override fun createLounge(): Flow<String?> = callbackFlow {
        val roomId = createRandomString(Room_Length)

        val roomRef = db.getReference("${Room}/${roomId}")
        auth.currentUser?.let{ user ->
            roomRef.setValue(mapOf("owner" to user.uid))
                .addOnSuccessListener {
                    trySend(roomId)

                    addMember(roomRef, user.uid, user.displayName.toString(), user.photoUrl.toString())
                }
                .addOnFailureListener {
                    Timber.e(it)
                    trySend(null)
                }
        }

        awaitClose()
    }.flowOn(dispatcher)

    override fun getMemberList(loungeId: String): Flow<List<Member>> = callbackFlow {
        val memberRef = db.getReference("${Room}/${loungeId}").child(Member)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val res = snapshot.getValue<HashMap<String, Member>>()
                    Timber.e("get Value : $res")
                    res?.let { trySend(res.values.toList().sortedBy { it.joinedTime }) }
                } catch (e: Exception){
                    Timber.e("error: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("$error")
                close(Throwable(error.message))
            }
        }

        memberRef.addValueEventListener(listener)

        awaitClose()
    }

    private fun addMember(roomRef: DatabaseReference, uid: String, displayName: String, photoUrl: String?){
        roomRef.child(Member).child(uid).setValue(
            Member(displayName, photoUrl, false, LocalDateTime.now().toString())
        )
    }

    companion object{
        const val Room = "rooms"
        const val Room_Length = 10
        const val Member = "members"
    }
}